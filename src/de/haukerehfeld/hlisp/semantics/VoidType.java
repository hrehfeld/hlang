package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class VoidType implements Type, Value {
	public static final String NAME = "void";
	
	private final Type parent;
	
	public VoidType(Type parent) {
		this.parent = parent;
	}


	@Override public Type getReturnType() { return this; }

	@Override public String getName() { return NAME; }

	@Override public Type getParent() { return this.parent; }
	
	@Override public List<Parameter> getParameters() { return Collections.emptyList(); }
	@Override public Type getDefinedType(String type) { return null; }
	@Override public List<Type> getDefinedTypes() { return Collections.emptyList(); }
	@Override public void defineType(Type t) {
		throw new RuntimeException("VoidType cannot have child types.");
	}
	@Override public boolean isTypeDefined(String t) { return false; }
	@Override public Body getBody() { return new ResolvedBody(); }

	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) { return emitter.emit(this); }

	@Override public VoidType getValue() {
		return this;
	}

	@Override public String toString() {
		return getClass().getSimpleName();
	}
}