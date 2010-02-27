package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class IntValue extends AbstractValue {
	final int value;
	
	public IntValue(Value scope, int value) {
		super(new UnresolvedType("Int"), scope);
		this.value = value;
	}

	public int getInt() { return value; }

	@Override public String toString() { return super.toString() + "('" + value + "')"; }
}