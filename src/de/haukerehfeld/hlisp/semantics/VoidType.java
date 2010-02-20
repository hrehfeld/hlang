package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class VoidType implements Type, Value {
	private final Type parent;
	
	public VoidType(Type parent) {
		this.parent = parent;
		
		      // "Void",
		      // new ArrayList<Parameter>(),
		      // new ResolvedBody(),
	}


	
	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		return emitter.emit(this);
	}

	@Override public Type getReturnType() { return this; }

	@Override public String getName() { return "Void"; }

	@Override public Type getParent() { return this.parent; }
	
	@Override public List<Parameter> getParameters() { return Collections.emptyList(); }
	@Override public Type getDefinedType(String type) { return null; }
	@Override public List<Type> getDefinedTypes() { return Collections.emptyList(); }
	@Override public void defineType(Type t) {
		throw new SemanticException("VoidType cannot have child types.");
	}
	@Override public boolean isTypeDefined(String t) { return false; }
	@Override public Body getBody() { return new EmptyBody(); }

	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) { emitter.emit(this); }


}