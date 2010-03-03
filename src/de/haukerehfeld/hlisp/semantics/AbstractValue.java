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
		Value parent = this;
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
	@Override public boolean isMemberDefinedRecursive(final String name) {
		return getDefinedMemberRecursive(name) != null;
	}
	@Override public Value getDefinedMemberRecursive(final String name) {
		return runOnScope(new Value.ValueMethod<Value>() {
		        private boolean success = false;
		        
		        @Override public Value run(Value scope) {
					//System.out.println("Searching " + scope + " for " + name);

					if (scope.isMemberDefined(name)) {
						success = true;
						return scope.getDefinedMember(name);
					}
					return null;
				}
		        @Override public boolean success() { return success; }
		    });		
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