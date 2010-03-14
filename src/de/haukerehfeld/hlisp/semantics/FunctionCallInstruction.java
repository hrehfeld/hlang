package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;

public class FunctionCallInstruction implements Instruction {
	private Instruction scope;
	public Instruction getScope() { return scope; }
	public void setScope(Instruction scope) { this.scope = scope; }

	private Type function;
	public Type getFunction() { return function; }
	public void setFunction(Type function) { this.function = function; }

	private List<Instruction> parameters;
	public List<Instruction> getParameters() { return parameters; }
	public void addParameter(Instruction parameter) { parameters.add(parameter); }

	public FunctionCallInstruction(Type function) {
		this(function, null, new ArrayList<Instruction>());
	}

	public FunctionCallInstruction(Type function, Instruction scope) {
		this(function, scope, new ArrayList<Instruction>());
	}
	public FunctionCallInstruction(Type function, List<Instruction> parameters) {
		this(function, null, parameters);
	}
	public FunctionCallInstruction(Type function, Instruction scope, List<Instruction> parameters) {
		this.parameters = parameters;
		this.scope = scope;
		this.function = function;
	}

	public boolean isStatic() { return scope == null; }

	@Override public Signature getReturnType() { return function.getReturnType(); }

	@Override public String toString() {
		return "call__ "
		    + (scope != null ?
		       (scope instanceof FunctionCallInstruction ? "(" + scope + ")" : scope) + "."
		       : "")
		    + function + "(" + Utils.join(parameters, ", ") + ")";
	}
	
}