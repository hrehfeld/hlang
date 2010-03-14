package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedTypeException extends RuntimeException {
	private UnresolvedSignature type;

	public UnresolvedTypeException(UnresolvedSignature type) {
		super(type.getName() + " is unresolved.");
		this.type = type;
	}

	/**
	 * get type
	 */
	public UnresolvedSignature getType() { return type; }
}
