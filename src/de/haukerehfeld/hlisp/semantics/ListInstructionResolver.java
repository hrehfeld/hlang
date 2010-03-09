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
	
	private Type lastFunction = null;
	private int lastFunctionI = -1;
	private Instruction lastFunctionCall = null;
	private boolean nextIterationNoParameterCheck = false;

	private boolean lastFunctionCallFinished = false;

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
	 * is candidate of valid type as the next function parameter?
	 */
	private boolean checkParameter(Type function, int parameterI, Type candidate) throws
		SemanticException {
		return function.getParameterTypes().get(parameterI).equals(candidate);
	}

	/**
	 * check if the next type is a function that expects parameters
	 */
	private void parametersExpected(Type next) throws SemanticException {
		if (next.isFunction()
		    && !(next.getParameterTypes().isEmpty())) {
			//expectedParameters.addAll(next.getParameterTypes());
			lastFunctionI = i;
			lastFunction = next;

			r.print("Next is a function " + next, true);
			r.print("with parameters " + Utils.join(next.getParameterTypes(), ", ") + ".", true);
			nextIterationNoParameterCheck = false;
		}
		else {
			if (next.isFunction()) {
				r.print("Next is a function with no parameters.", true);
				makeFunctionCall(next, next.getReturnType());
				r.print("lastFunctionCall = " + lastFunctionCall, true);
			}
			else {
				r.print("Next is not a function.", true);
			}
			nextIterationNoParameterCheck = true;
		}
	}

	private void makeFunctionCall(Type scope, Type next) {
		lastFunction
		    = new AnonymousType(scope,
		                        next.getReturnType(),
		                        next.isFunction(),
		                        next.getParameterTypes(),
		                        next.getParameterNames());
		lastFunction.setInstruction(lastFunctionCall);
		
		lastFunctionCall
		    = new FunctionCallInstruction(lastFunction,
		                                  ((FunctionCallInstruction) lastFunctionCall)
		                                      .getFunction());
		
	}

	public Instruction solve(ListInstruction list, Type scope, Resolver resolver) throws
		SemanticException {

		r.print("/----Solving " + list, true);

		r.indentMore();
		for (; i < list.getInstructions().size(); ++i) {
			Instruction instr = list.getInstructions().get(i);


			instr = resolver.solve(instr, scope);

			r.print(i + ". child " + instr, true);
			r.indentMore();

			int parameterI = i - lastFunctionI - 1;
					
			//remaining parameters
			if (lastFunction != null) {
				r.print((nextIterationNoParameterCheck ? "no check" : "check") + ", "
				        + parameterI + " < " + lastFunction.getParameterTypes().size(), true);
			}
			if (!nextIterationNoParameterCheck
			    && lastFunction != null
			    && parameterI < lastFunction.getParameterTypes().size()) {
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

				if (!resolved || !checkParameter(lastFunction, parameterI, returnType)) {
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
					r.indentLess();
					continue;
				}
				else {
					((FunctionCallInstruction) lastFunctionCall).addParameter(instr);
					r.print(instr + " is parameter.", true);
					
					//parameters finished
					if (parameterI >= lastFunction.getParameterTypes().size() - 1) {
						r.print("Function call to " + lastFunction + " complete.", true);
						//r.print("adding " + lastFunctionCall + " to result.", true);
						//result.add(lastFunctionCall);
						singleStatements = false;
						
						//the return value of the function is a function that expects params
						parametersExpected(lastFunction.getReturnType());
						r.print("lastFunctionCall = " + lastFunctionCall, true);
					}
				}
			}
			else {
				nextIterationNoParameterCheck = false;

				if (isUnresolved(instr)) {
					String id = getId(instr);

					if (lastFunction != null
					    && lastFunction.getReturnType().isTypeDefined(id)) {
						/** @todo 2010-03-08 16:22 hrehfeld    also check in last itself? */
						r.print("Resolving " + id, true);
						r.indentMore();
						r.print("as " + lastFunction.getReturnType().getDefinedTypeRecursive(id), true);
						r.print("from " + lastFunction.getReturnType(), true);
						r.indentLess();

						Type next = lastFunction.getReturnType().getDefinedTypeRecursive(id);
						makeFunctionCall(scope, next);

						singleStatements = false;

						r.print("lastFunctionCall = " + lastFunctionCall, true);

					}
					else {
						if (!first && singleStatements) {
							result.add(lastFunctionCall);
							r.print(lastFunctionCall + " in single statement list", true);
						}
						
						if (first || singleStatements) {
							if (!scope.isTypeDefinedRecursive(id)) {
								new TypePrinter().print(scope);
								throw new SemanticException("Couldn't resolve identifier "
								                            + id + " in scope " + scope);
							}
							lastFunction = scope.getDefinedTypeRecursive(id);
							lastFunctionCall = new FunctionCallInstruction(lastFunction);
							r.print(id + " found in current scope, setting to " + lastFunction, true);
							r.print("lastFunctionCall = " + lastFunctionCall, true);
						}
						else {
							throw new SemanticException("Couldn't resolve identifier "
							                            + id + " in " + lastFunction);
						}
					}
				}
				else {
					if (singleStatements) {
						//wrong?
						lastFunctionCall = instr;
						lastFunction = instr.getReturnType();
					}
					else {
						throw new SemanticException("Unexpected " + instr + ", identifier expected.");
					}
					
				}

				if (first || !singleStatements) {
					parametersExpected(lastFunction);
				}
			}
			first = false;
			r.indentLess();
		}
		r.indentLess();
		r.print("adding " + lastFunctionCall + " to result.", true);
		result.add(lastFunctionCall);

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
