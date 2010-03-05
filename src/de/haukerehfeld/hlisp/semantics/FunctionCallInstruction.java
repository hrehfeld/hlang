package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;

public class FunctionCallInstruction implements Instruction {
	private Type function;
	public Type getFunction() { return function; }
	public void setFunction(Type function) { this.function = function; }

	private List<Instruction> parameters;
	private List<Instruction> getParameters() { return parameters; }
	public void addParameter(Instruction parameter) { parameters.add(parameter); }

	public FunctionCallInstruction(Type function) {
		this(function, new ArrayList<Instruction>());
	}
	public FunctionCallInstruction(Type function, Instruction[] parameters) {
		this(function, Arrays.asList(parameters));
	}
	public FunctionCallInstruction(Type function, List<Instruction> parameters) {
		this.parameters = parameters;
		this.function = function;
	}

	public FunctionCallInstruction(String functionName, Instruction[] parameters) {
		this(functionName, Arrays.asList(parameters));
	}
	public FunctionCallInstruction(String functionName, List<Instruction> parameters) {
		this(new UnresolvedType(functionName), parameters);
	}

	@Override public Type getReturnType() { return function.getReturnType(); }

	@Override public String toString() {
		return "call: " + function + "(" + Utils.join(parameters, ", ") + ")"; }
	
}