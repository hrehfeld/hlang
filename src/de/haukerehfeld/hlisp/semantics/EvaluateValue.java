package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class EvaluateValue extends AbstractValue {

	public EvaluateValue(Type type, Value scope) {
		super(type, scope);
	}

	/** parent */
	private List<Value> values = new ArrayList<Value>();
	public void add(Value v) {
		values.add(v);
	}

	public void finish() throws SemanticException {
		if (values.isEmpty()) {
			System.err.println("Empty evaluate value." + this);
			return;
		}
		int last = values.size() - 1;
		Value lastValue = values.get(last);
		Type lastValueType = lastValue.getType();
		if (getType() != null && !getType().equals(lastValueType)) {
			String e = "Expected type " + getType() + ", but last instruction is of type "
			    + lastValueType + ".";
			//throw new SemanticException(e);
			System.err.println(e);
		}
		setType(lastValueType);
	}
	
	/**
	 * get values
	 */
	public List<Value> getValues() { return values; }
	
}