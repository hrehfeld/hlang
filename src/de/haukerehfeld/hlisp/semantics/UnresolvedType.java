package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedType implements Type {
	private String name;

	public UnresolvedType(String name) {
		this.name = name;
	}
	
	@Override public String getName() { return name; }

	@Override public Type getParent() { throw new UnresolvedTypeException(this); }
	
	@Override public List<Parameter> getParameters() { throw new UnresolvedTypeException(this); }
	@Override public Type getDefinedType(String type) { throw new UnresolvedTypeException(this); }
	@Override public List<Type> getDefinedTypes() { throw new UnresolvedTypeException(this); }
	@Override public void defineType(Type t) { throw new UnresolvedTypeException(this); }
	@Override public boolean isTypeDefined(String t) { throw new UnresolvedTypeException(this); }
	@Override public Body getBody() { throw new UnresolvedTypeException(this); }
	@Override public Type getReturnType() { throw new UnresolvedTypeException(this); }
	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) { throw new UnresolvedTypeException(this); }
}