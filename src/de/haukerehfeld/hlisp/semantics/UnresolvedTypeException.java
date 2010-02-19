package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedTypeException extends RuntimeException {
	private UnresolvedType type;

	public UnresolvedTypeException(UnresolvedType type) {
		super(type.getName() + " is unresolved.");
		this.type = type;
	}

	/**
	 * get type
	 */
	public UnresolvedType getType() { return type; }
}
