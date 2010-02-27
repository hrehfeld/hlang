package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public abstract class AbstractValue implements Value {
	public AbstractValue(Type type, Value scope) {
		this.type = type;
		this.scope = scope;
	}

	private Type type;
	@Override public Type getType() { return type; }
	public void setType(Type type) { this.type = type; }
	
	private Value scope; 
	@Override public Value getScope() { return scope; }
	@Override public <T> T runOnScope(Value.ValueMethod<T> method) {
		Value parent = scope;
		T result = null;
		while (parent != null && !method.success()) {
			result = method.run(parent);
			parent = parent.getScope();
		}
		if (!method.success()) {
			return null;
		}
		return result;
	}

	/** members */
	private final LinkedHashMap<String, Value> members = new LinkedHashMap<String, Value>();
	@Override public Map<String, Value> getDefinedMembers() {
		return members;
	}
	@Override public Value getDefinedMember(String name) {
		return members.get(name);
	}
	@Override public boolean isMemberDefined(String v) {
		return members.get(v) != null;
	}
	@Override public void defineMember(String name, Value v) { members.put(name, v); }
	
	@Override public String toString() { return getClass().getSimpleName(); }
}