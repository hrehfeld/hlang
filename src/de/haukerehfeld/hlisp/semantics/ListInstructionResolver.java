package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.IndentStringBuilder;
import java.util.*;

/**
 * Walk the value structure and link unresolved values
 */
public class ListInstructionResolver {
	private List<UnresolvedInstruction> unresolvableValues = new ArrayList<UnresolvedInstruction>();

	private int i = 0;

	/** first iteration/element */
	private boolean first = true;

	/** a list of single completed statements is imperative code */
	private boolean singleStatements = true;
	
	private int lastFunctionI = -1;
	private Instruction lastIdentifierCall = null;

	private boolean lastIdentifierCallFinished = false;

	private Solver solver = new LookupSolver();
	
	private boolean nextIterationNoParameterCheck = true;
	private boolean nextIterationOnFailInsertCall = false;
	private FunctionCallInstruction onFailCall = null;


	private List<Instruction> result = new ArrayList<Instruction>();
	
	//private ArrayDeque<Instruction> instructionStack = new ArrayDeque<Instruction>();

	private final IndentStringBuilder r;

	public ListInstructionResolver(IndentStringBuilder r) { this.r = r; }

	private boolean isUnresolved(Instruction instr) {
		return (instr instanceof UnresolvedInstruction
		        && !((UnresolvedInstruction) instr).isResolved());
	}
	private String getId(Instruction instr) {
		return ((UnresolvedInstruction) instr).getIdentifier();
	}

	/**
	 * check if the next type is a function that expects parameters
	 */
	private void parametersExpected(Type next, Type scope) throws SemanticException {
		if (next.isFunction()
		    && !(next.getParameterTypes().isEmpty())) {
			//expectedParameters.addAll(next.getParameterTypes());
			lastFunctionI = i;

			r.print("Next is a function " + next, true);
			r.print("with parameters " + Utils.join(next.getParameterTypes(), ", ") + ".", true);
			nextIterationNoParameterCheck = false;
		}
		else {
			nextIterationNoParameterCheck = true;

			if (next.isFunction()) {
				r.print("Next is a function with no parameters.", true);

				onFailCall = new FunctionCallInstruction(next, scope);
				nextIterationOnFailInsertCall = true;
			}
			else {
				r.print("Next is not a function.", true);
			}
		}
	}

	private Type makeAnonymousType(Instruction i, Type scope) {
		Type t = new AnonymousType(scope,
		                           i.getReturnType(),
		                           true);
		t.setInstruction(i);
		return t;
	}

	public interface Solver {
		public boolean solve(Instruction instr, Type scope) throws
			SemanticException;
	}

	public class ParameterSolver implements Solver {
		private int parameterI;
		private Type lastFunction;

		public ParameterSolver(int parameterPosition, Type lastFunction) {
			this.parameterI = parameterPosition;
			this.lastFunction = lastFunction;
		}

		public boolean solve(Instruction instr, Type scope) throws
			SemanticException {
			Type returnType = null;
			boolean resolved = true;
			if (isUnresolved(instr)) {
				resolved = false;
				
				String id = getId(instr);
				// parameters are searched in current scope
				if (scope.isTypeDefinedRecursive(id)) {
					resolved = true;
					
					Type type = scope.getDefinedTypeRecursive(id);
					returnType = type.getReturnType();
					instr = new FunctionCallInstruction(type);
				}
			}
			else if (instr instanceof FunctionCallInstruction) {
				returnType = instr.getReturnType();
			}
			else if (instr instanceof NativeInstruction) {
				returnType = instr.getReturnType();
			}
			else {
				resolved = false;
			}

			if (!resolved || !checkParameter(lastFunction,
			                                 parameterI,
			                                 returnType)) {
				nextIterationNoParameterCheck = true;
				//-1 because for loop does ++i
				i = i - parameterI - 1;
				if (!resolved) {
					r.print("couldn't resolve " + instr, true);
					r.indentMore();
					r.print("in scope " + scope, true);
					r.indentLess();
				}
				else {
					r.print(returnType + " doesn't match as parameterType, "
					        + lastFunction.getParameterTypes().get(parameterI)
					        + " expected.", true);
				}

				r.print("Parametercheck failed, backtracking to " + (i + 1) + "...", true);
				return false;
			}
			else {
				((FunctionCallInstruction) lastIdentifierCall).addParameter(instr);
				r.print(instr + " is parameter.", true);
				
				//parameters finished
				if (parameterI >= lastFunction.getParameterTypes().size() - 1) {
					r.print("Function call to " + lastFunction + " complete.", true);
					//r.print("adding " + lastIdentifierCall + " to result.", true);
					//result.add(lastIdentifierCall);
					singleStatements = false;
					
					//the return value of the function is a function that expects params
					parametersExpected(lastIdentifierCall.getReturnType(),
					                   makeAnonymousType(lastIdentifierCall, scope));
					r.print("lastIdentifierCall = " + lastIdentifierCall, true);
				}
			}
			return true;
		}
		/**
		 * is candidate of valid type as the next function parameter?
		 */
		private boolean checkParameter(Type function, int parameterI, Type candidate) throws
			SemanticException {
			return function.getParameterTypes().get(parameterI).equals(candidate);
		}

	}

	private class LookupSolver implements Solver {
		public boolean solve(Instruction instr, Type scope) throws
			SemanticException {
			Type next;
			if (isUnresolved(instr)) {
				String id = getId(instr);

				if (lastIdentifierCall != null
				    && lastIdentifierCall.getReturnType().isTypeDefined(id)) {
					/** @todo 2010-03-08 16:22 hrehfeld    also check in last itself? */
					r.print("Resolving " + id, true);
					r.indentMore();
					r.print("as " + lastIdentifierCall.getReturnType().getDefinedTypeRecursive(id),
					        true);
					r.print("from " + lastIdentifierCall.getReturnType(), true);
					r.indentLess();

					next = lastIdentifierCall.getReturnType().getDefinedTypeRecursive(id);
					lastIdentifierCall = new FunctionCallInstruction(next, scope);

					singleStatements = false;

					r.print("lastIdentifierCall = " + lastIdentifierCall, true);

				}
				else {
					if (!first && singleStatements) {
						result.add(lastIdentifierCall);
						r.print(lastIdentifierCall + " in single statement list", true);
					}
					
					if (first || singleStatements) {
						if (!scope.isTypeDefinedRecursive(id)) {
							new TypePrinter().print(scope);
							throw new SemanticException("Couldn't resolve identifier "
							                            + id + " in scope " + scope);
						}
						next = scope.getDefinedTypeRecursive(id);
						lastIdentifierCall = new FunctionCallInstruction(next, scope);
						r.print(id + " found in current scope, setting to " + next, true);
						r.print("lastIdentifierCall = " + lastIdentifierCall, true);
					}
					else if (nextIterationOnFailInsertCall) {
						nextIterationOnFailInsertCall = false;
						lastIdentifierCall = onFailCall;
						next = onFailCall.getFunction();
					}
					else {
						throw new SemanticException("Unexpected identifier " + id);
					}
				}
			}
			else {
				if (singleStatements) {
					//wrong?
					lastIdentifierCall = instr;
					next = makeAnonymousType(instr, scope);
				}
				else {
					throw new SemanticException("Unexpected " + instr + ", identifier expected.");
				}
				
			}

			if (first || !singleStatements) {
				parametersExpected(next, scope);
			}
			return true;
		}
	}
	
	public Instruction solve(ListInstruction list, Type scope, Resolver resolver) throws
		SemanticException {

		r.print("/----Solving " + list, true);

		r.indentMore();
		for (; i < list.getInstructions().size(); ++i) {
			Instruction instr = list.getInstructions().get(i);


			if (!(instr instanceof UnresolvedInstruction)) {
				instr = resolver.solve(instr, scope);
			}

			r.print(i + ". child " + instr, true);
			int parameterI = i - lastFunctionI - 1;
			
			r.indentMore();

			if (!nextIterationNoParameterCheck) {
				r.print("Parameter check", true);
				Type lastIdentifier;
				if (lastIdentifierCall instanceof FunctionCallInstruction) {
					lastIdentifier = ((FunctionCallInstruction) lastIdentifierCall).getFunction();
				}
				else {
					lastIdentifier = lastIdentifierCall.getReturnType();
				}

				solver = new ParameterSolver(parameterI, lastIdentifier);
			}
			else {
				nextIterationNoParameterCheck = false;
				solver = new LookupSolver();
			}

			boolean success = solver.solve(instr, scope);
			r.indentLess();
			if (!success) { continue; }

			first = false;
		}
		r.indentLess();
		r.print("adding " + lastIdentifierCall + " to result.", true);
		result.add(lastIdentifierCall);

		Instruction r;
		if (result.size() > 1) {
			r = new ListInstruction(result);
		}
		else {
			r = result.get(0);
		}

		this.r.print("----/Result: " + r, true);
		//list.setInstructions(result);
		return r;
	}
}
