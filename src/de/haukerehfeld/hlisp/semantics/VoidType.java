package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class VoidType implements Type {
	public static final String NAME = "void";
	
	private final Type parent;
	@Override public Type getParent() { return this.parent; }
	
	public VoidType(Type parent) {
		this.parent = parent;
	}

	@Override public boolean isStatic() { return false; } 
	@Override public boolean isFunction() { return false; } 

	@Override public Type getReturnType() { return this; }

	@Override public List<Type> getParameterTypes() { return Collections.emptyList(); }

	@Override public Type getDefinedType(String type) { return null; }
	@Override public Map<String, Type> getDefinedTypes() { return Collections.emptyMap(); }
	@Override public void defineType(String name, Type t) {
		throw new RuntimeException("VoidType cannot have child types.");
	}
	@Override public boolean isTypeDefined(String t) { return false; }

	@Override public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * Factory method to get a voidtype reference
	 */
	public static UnresolvedType create() { return new UnresolvedType(NAME); }
}