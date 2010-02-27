package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class FloatValue extends AbstractValue {
	final float value;
	
	public FloatValue(Value scope, float value) {
		super(new UnresolvedType("Float"), scope);
		this.value = value;
	}

	public float getFloat() { return value; }

	@Override public String toString() { return super.toString() + "('" + getFloat() + "')"; }
}