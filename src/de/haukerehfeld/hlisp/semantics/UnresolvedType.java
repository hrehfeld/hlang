package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class UnresolvedType implements Type {
	private String name;

	private Type type;

	public UnresolvedType(String name) {
		this.name = name;
	}

	public void setResolvedType(Type t) {
		this.type = t;
	}

	/**
	 * get resolvedType
	 */
	public Type getResolvedType() { return type; }

	public boolean isResolved() {
		return type != null;
	}

	@Override public String getName() { return name; }

	@Override public Type getParent() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}

		return type.getParent();
	}
	
	@Override public List<Parameter> getParameters() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.getParameters();
	}
	@Override public Type getDefinedType(String type) {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return this.type.getDefinedType(type);
	}

	@Override public List<Type> getDefinedTypes() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return this.type.getDefinedTypes();
	}

	@Override public void defineType(Type t) {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		type.defineType(t);
	}

	@Override public boolean isTypeDefined(String t) {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.isTypeDefined(t);
	}

	@Override public Body getBody() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.getBody();
	}

	@Override public Type getReturnType() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.getReturnType();
	}

	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.emit(emitter);
	}

	@Override public String toString() { return getClass().getSimpleName() + " " + this.name; }

	@Override public boolean equals(Object o) {
		return o instanceof UnresolvedType
		    && ((UnresolvedType) o).getName().equals(getName())
		    && isResolved() == ((UnresolvedType) o).isResolved()
		    && (isResolved() ? type.equals(((UnresolvedType) o).getResolvedType()) : true);
	}

	@Override public int hashCode() {
		return 435435 + name.hashCode();
	}
}
