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

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }


	private void resolvedOrException() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
	}

	@Override public boolean isStatic() {
		resolvedOrException();
		return type.isStatic();
	}

	@Override public boolean isFunction() {
		resolvedOrException();
		return type.isFunction();
	}

	
	@Override public Type getParent() {
		resolvedOrException();
		return type.getParent();
	}
	
	@Override public List<Type> getParameterTypes() {
		resolvedOrException();
		return type.getParameterTypes();
	}
	@Override public Type getDefinedType(String type) {
		resolvedOrException();
		return this.type.getDefinedType(type);
	}

	@Override public Map<String, Type> getDefinedTypes() {
		resolvedOrException();
		return type.getDefinedTypes();
	}

	@Override public void defineType(String name, Type t) {
		resolvedOrException();
		type.defineType(name, t);
	}

	@Override public boolean isTypeDefined(String t) {
		resolvedOrException();
		return type.isTypeDefined(t);
	}

	@Override public Type getReturnType() {
		resolvedOrException();
		return type.getReturnType();
	}

	@Override public String toString() {
		return getClass().getSimpleName() + "(" + this.name + ", " + (isResolved() ? "resolved": "unresolved") + ")";
	}

	@Override public boolean equals(Object o) {
		return o instanceof UnresolvedType
		    && isResolved() == ((UnresolvedType) o).isResolved()
		    && ((UnresolvedType) o).getName().equals(getName())
		    && (isResolved() ? type.equals(((UnresolvedType) o).getResolvedType()) : true);
	}

	@Override public int hashCode() {
		return isResolved() ? 23434 + type.hashCode() : 435435 + name.hashCode();
	}
}
