package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class LambdaFunction extends EvaluateValue implements Function {

	private Value value;
	@Override public Value getValue() { return value; }
	public void setValue(Value value) { this.value = value; }
	
	private List<String> parameterNames;
	public List<String> getParameterNames() { return parameterNames; }
	public void setParameterNames(List<String> parameterNames) { this.parameterNames = parameterNames; }

	public LambdaFunction(Type type, Value scope, List<String> parameterNames) {
		super(type, scope);
		this.parameterNames = parameterNames;
	}

	@Override public void finish() throws SemanticException {
		super.finish();
		List<Value> values = super.getValues();
		if (values.isEmpty()) {
			return;
		}
		int last = values.size() - 1;
		Value lastValue = values.get(last);
		setValue(lastValue);
	}

	@Override public String toString() {
		return super.toString() + " (" + getType() + ")";
	}
}