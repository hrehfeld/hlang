package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class CallValue extends LambdaFunction implements Function {

	List<Value> parameters = new ArrayList<Value>();
	public List<Value> getParameters() { return parameters; }
	public void setParameters(List<Value> parameters) { this.parameters = parameters; }
	public void addParameter(Value p) { parameters.add(p); }
	public int getParameterCount() { return parameters.size(); }

	Function function;
	public Function getFunction() { return function; }
	public void setFunction(Function function) { this.function = function; }

	public CallValue(Function function) {
		super(function.getType(), function.getScope(), function.getParameterNames());
		this.function = function;
	}
	public CallValue(Function function, List<Value> parameters) {
		this(function);
		this.parameters = parameters;
	}
}