package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedInstruction implements Instruction {
	public UnresolvedInstruction(String identifier) {
		this.identifier = identifier;
	}

	final String identifier;
	public String getIdentifier() { return identifier; }

	private Instruction resolved;
	public boolean isResolved() {
		return resolved != null;
	}
	public void setResolved(Instruction resolved) { this.resolved = resolved; }
	public Instruction getResolved() { resolvedOrException(); return this.resolved; }	

	public Type getReturnType() { return getResolved().getReturnType(); }	

	private void resolvedOrException() {
		if (!isResolved()) {
			throw new UnresolvedIdentifierException(this);
		}
	}

	@Override public String toString() {
		return getClass().getSimpleName() + "('" + getIdentifier() + "', "
		    + (isResolved() ? "resolved as " + getResolved() : "unresolved") + ")";
	}
}