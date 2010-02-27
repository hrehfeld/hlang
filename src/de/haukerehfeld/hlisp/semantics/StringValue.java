package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class StringValue extends AbstractValue {
	final String value;
	
	public StringValue(Value scope, String value) {
		super(new UnresolvedType("String"), scope);
		this.value = value;
	}

	public String getString() { return value; }

	@Override public String toString() { return super.toString() + "('" + getString() + "')"; }
}