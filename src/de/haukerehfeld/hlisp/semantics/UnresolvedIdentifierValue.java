package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedIdentifierValue extends AbstractValue {
	final String identifier;
	
	public UnresolvedIdentifierValue(Value scope, String identifier) {
		super(new UnresolvedType(null), scope);
		this.identifier = identifier;
	}

	public String getIdentifier() { return identifier; }

	@Override public String toString() { return super.toString() + "('" + getIdentifier() + "')"; }
}