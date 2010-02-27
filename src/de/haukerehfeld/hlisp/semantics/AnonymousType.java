package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;

public class AnonymousType implements Type {
	public AnonymousType(Type parent, Type returnType, boolean isFunction) {
		this(parent, returnType, isFunction, Collections.<Type>emptyList());
	}
	
	public AnonymousType(Type parent, Type returnType, boolean isFunction, List<Type> parameters) {
		this.parent = parent;
		this.returnType = returnType;
		this.parameters = parameters;
		this.isFunction = isFunction;
	}

	private boolean isFunction;
	public boolean isFunction() { return isFunction; }
	public void setIsFunction(boolean isFunction) { this.isFunction = isFunction; }

	private boolean isStatic = false;
	public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
	@Override public boolean isStatic() { return isStatic; }

	/** Parent type */
	private final Type parent;
	@Override public Type getParent() { return parent; }

	/** params */
	private List<Type> parameters;
	@Override public List<Type> getParameterTypes() { return parameters; }
	public void setParameterTypes(List<Type> parameters) { this.parameters = parameters; }

	/** return type */
	private Type returnType;
	@Override public Type getReturnType() { return returnType; }
	public void setReturnType(Type returnType) { this.returnType = returnType; }

	/** members */
	private final LinkedHashMap<String, Type> types = new LinkedHashMap<String, Type>();
	@Override public Map<String, Type> getDefinedTypes() {
		return types;
	}
	@Override public boolean isTypeDefined(String v) {
		return types.get(v) != null;
	}
	@Override public void defineType(String name, Type t) { types.put(name, t); }
	@Override public Type getDefinedType(String name) { return types.get(name); }

	@Override public String toString() {
		StringBuilder name = new StringBuilder();
		String self = getClass().getSimpleName();
		name.append(self);
		boolean func = !getParameterTypes().isEmpty();
		if (func) {
			name.append(" (");
			name.append(Utils.join(getParameterTypes(), " "));
			name.append(" ->");
		}
		name.append(" ");
		if (getReturnType() == this) {
			name.append(self);
		}
		else {
			name.append(getReturnType());
		}
		if (func) {
			name.append(")");
		}
		return name.toString();
	}
	
	
}