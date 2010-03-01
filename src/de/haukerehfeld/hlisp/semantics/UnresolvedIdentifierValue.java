package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedIdentifierValue implements Value {
	public UnresolvedIdentifierValue(Value scope, String identifier) {
		this.scope = scope;
		this.identifier = identifier;
	}

	final private Value scope;
	@Override public Value getScope() { return scope; }

	final String identifier;
	public String getIdentifier() { return identifier; }

	private Value resolved;
	public boolean isResolved() {
		return resolved != null;
	}
	public void setResolved(Value resolved) { this.resolved = resolved; }
	public Value getResolved() { resolvedOrException(); return this.resolved; }	

	private void resolvedOrException() {
		if (!isResolved()) {
			throw new UnresolvedIdentifierException(this);
		}
	}


	@Override public <T> T runOnScope(ValueMethod<T> m) {
		return getResolved().runOnScope(m);
	}

	@Override public Type getType() {  return getResolved().getType(); }
	@Override public void defineMember(String name, Value v) { getResolved().defineMember(name, v); }
	@Override public Map<String, Value> getDefinedMembers() { return getResolved().getDefinedMembers(); }
	@Override public boolean isMemberDefined(String v) { return getResolved().isMemberDefined(v); }
	@Override public boolean isMemberDefinedRecursive(String v) { return getResolved().isMemberDefinedRecursive(v); }
	@Override public Value getDefinedMember(String v) { return getResolved().getDefinedMember(v); }
	@Override public Value getDefinedMemberRecursive(String v) { return getResolved().getDefinedMemberRecursive(v); }
	
	@Override public String toString() { return super.toString() + "('" + getIdentifier() + "')"; }
}