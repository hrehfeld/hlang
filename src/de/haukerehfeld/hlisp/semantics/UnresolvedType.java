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


	@Override public boolean isStatic() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}

		return type.isStatic();
	}

	@Override public boolean isFunction() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}

		return type.isFunction();
	}

	
	@Override public Type getParent() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}

		return type.getParent();
	}
	
	@Override public List<Type> getParameterTypes() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.getParameterTypes();
	}
	@Override public Type getDefinedType(String type) {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return this.type.getDefinedType(type);
	}

	@Override public Map<String, Type> getDefinedTypes() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.getDefinedTypes();
	}

	@Override public void defineType(String name, Type t) {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		type.defineType(name, t);
	}

	@Override public boolean isTypeDefined(String t) {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.isTypeDefined(t);
	}

	@Override public Type getReturnType() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
		return type.getReturnType();
	}

	@Override public String toString() {
		return getClass().getSimpleName() + "(" + this.name + ")";
	}

	@Override public boolean equals(Object o) {
		return o instanceof UnresolvedType
		    && ((UnresolvedType) o).getName().equals(getName())
		    && isResolved() == ((UnresolvedType) o).isResolved()
		    && (isResolved() ? type.equals(((UnresolvedType) o).getResolvedType()) : true);
	}

	@Override public int hashCode() {
		return isResolved() ? 23434 + type.hashCode() : 435435 + name.hashCode();
	}
}
