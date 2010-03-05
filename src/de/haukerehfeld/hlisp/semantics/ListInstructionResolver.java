package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

/**
 * Walk the value structure and link unresolved values
 */
public class ListInstructionResolver {
	private List<UnresolvedInstruction> unresolvableValues = new ArrayList<UnresolvedInstruction>();

	private int i = 0;

	private Type last = null;

	private Type lastFunction = null;
	private int lastFunctionI = -1;
	private FunctionCallInstruction lastFunctionCall = null;
	private boolean nextIterationNoParameterCheck = false;

	private boolean lastFunctionCallFinished = false;

	//private List<Instruction> result = new ArrayList<Instruction>();
	
	//private ArrayDeque<Instruction> instructionStack = new ArrayDeque<Instruction>();

	private Type getType(String name, Type scope) {
		if (scope.isTypeDefinedRecursive(name)) {
			return scope.getDefinedTypeRecursive(name);
		}
		return null;
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
			lastFunctionCall = new FunctionCallInstruction(lastFunction);

			System.out.println("Next is a function " + next + " with " + Utils.join(next.getParameterTypes(), ", ") + " parameters.");
			nextIterationNoParameterCheck = false;
		}
		else {
			nextIterationNoParameterCheck = true;
			last = next;
		}
	}


	public Instruction solve(ListInstruction list, Type scope, Resolver resolver) throws
		SemanticException {

		System.out.println("------ Solving " + list);

		boolean first = true;
		for (; i < list.getInstructions().size(); ++i) {
			Instruction instr = list.getInstructions().get(i);
			instr = resolver.solve(instr, scope);
			System.out.println("  " + i + ". child " + instr);

			int parameterI = i - lastFunctionI - 1;
					
			boolean parameterMatches = false;
			//remaining parameters
			if (lastFunction != null) {
				System.out.println((nextIterationNoParameterCheck ? "no check" : "check") + ", " + parameterI + " < " + lastFunction.getParameterTypes().size());
			}
			if (!nextIterationNoParameterCheck && lastFunction != null && parameterI < lastFunction.getParameterTypes().size()) {
				Type returnType = null;
				boolean resolved = true;
				if (instr instanceof UnresolvedInstruction
				    && !((UnresolvedInstruction) instr).isResolved()) {
					resolved = false;
					
					String id = ((UnresolvedInstruction) instr).getIdentifier();

					// parameters are searched in current scope
					Type type = scope.getDefinedTypeRecursive(id);
					if (type != null) {
						resolved = true;
						returnType = type.getReturnType();
						instr = new FunctionCallInstruction(type);
					}
					else {
						parameterMatches = false;
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
					System.out.println(returnType + " doesn't match as parameterType, "
					                   + lastFunction.getParameterTypes().get(parameterI) + " expected.");

					System.out.println("Parametercheck failed, backtracking to " + (i + 1) + "...");
					continue;
				}
				else {
					parameterMatches = true;
					
					lastFunctionCall.addParameter(instr);
					System.out.println(instr + " is parameter.");
					
					//parameters finished
					if (parameterI >= lastFunction.getParameterTypes().size() - 1) {
						System.out.println("Function call to " + lastFunction + " complete.");

						//the return value of the function is a function that expects params
						parametersExpected(lastFunction.getReturnType());
					}
				}
			}
			else {
				nextIterationNoParameterCheck = false;
				Type returnType;
				if (instr instanceof UnresolvedInstruction
				    && !((UnresolvedInstruction) instr).isResolved()) {
					
					UnresolvedInstruction identifier = (UnresolvedInstruction) instr;
					String id = identifier.getIdentifier();
					
					if (last != null) {
						if (!last.isTypeDefined(id)) {
							throw new SemanticException("Couldn't resolve identifier "
							                            + id + " in " + last);
						}
						Type type = last.getDefinedType(id);
						System.out.println("Resolving " + id + " as " + type + " from " + last);
						//hasn't been evaluated yet, so use itself as return type
						returnType = type;
					}
					else {
						Type type = scope.getDefinedTypeRecursive(id);
						if (type == null) {
							throw new SemanticException("Couldn't resolving identifier "
							                            + id + " in " + scope);
						}
						System.out.println(id + " found in current scope, setting to " + type);
						returnType = type.getReturnType();
					}
				}
				else {
					if (first) {
						returnType = instr.getReturnType();
					}
					else {
						throw new SemanticException("Unexpected " + instr + ", identifier expected.");
					}
					
				}
				
				last = returnType;
				parametersExpected(last);

				
			}
			first = false;
		}
		
		//System.out.println("Result: " + Utils.join(result, ", "));
		//list.setInstructions(result);
		return lastFunctionCall;
	}
}
