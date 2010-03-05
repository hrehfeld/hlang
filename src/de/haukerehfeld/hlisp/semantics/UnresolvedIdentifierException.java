package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedIdentifierException extends RuntimeException {
	public UnresolvedIdentifierException(UnresolvedInstruction e) { super(e + " is unresolved!"); }
}
