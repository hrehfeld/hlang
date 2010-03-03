package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;

public abstract class AnonymousType implements Type {
	public AnonymousType(Type parent, Type returnType) {
		this(parent, returnType, false);
	}

	public AnonymousType(Type parent, Type returnType, boolean isFunction) {
		this(parent, returnType, isFunction,
		     Collections.<Type>emptyList(),
		     Collections.<String>emptyList());
	}
	
	
	public AnonymousType(Type parent,
	                     Type returnType,
	                     List<Type> parameterTypes,
	                     List<String> parameterNames) {
		this.parent = parent;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.parameterNames = parameterNames;
		this.isFunction = true;
	}


	private boolean isFunction;
	public boolean isFunction() { return isFunction; }
	public void setIsFunction(boolean isFunction) { this.isFunction = isFunction; }

	private boolean isStatic = false;
	public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
	@Override public boolean isStatic() { return isStatic; }

	private boolean isPublic = false;
	public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
	@Override public boolean isPublic() { return isPublic; }
	
	/** Parent type */
	private final Type parent;
	@Override public Type getParent() { return parent; }

	/** params */
	private List<Type> parameterTypes;
	@Override public List<Type> getParameterTypes() { return parameterTypes; }
	public void setParameterTypes(List<Type> parameterTypes) { this.parameterTypes = parameterTypes; }

	/** return type */
	private Type returnType;
	@Override public Type getReturnType() { return returnType; }
	public void setReturnType(Type returnType) { this.returnType = returnType; }

	private final LinkedHashMap<String, Type> types = new LinkedHashMap<String, Type>();
	@Override public List<Type> getDefinedTypes() { return types.values(); }
	@Override public boolean isTypeDefined(String v) { return types.get(v) != null; }
	@Override public void defineType(Type t) { types.put(t.getName(), t); }
	@Override public Type getDefinedType(String name) { return types.get(name); }
	@Override public <T> T runOnHierarchy(Type.TypeMethod<T> method) {
		Type parent = this;
		T result = null;
		while (parent != null && !method.success()) {
			result = method.run(parent);
			parent = parent.getParent();
		}
		if (!method.success()) {
			return null;
		}
		return result;
	}
	@Override public boolean isTypeDefinedRecursive(final String name) {
		return getDefinedTypeRecursive(name) != null;
	}
	@Override public Value getDefinedTypeRecursive(final String name) {
		return runOnHierarchy(new TypeMethod<Value>() {
		        private boolean success = false;
		        
		        @Override public Type run(Type scope) {
					//System.out.println("Searching " + scope + " for " + name);

					if (scope.isTypeDefined(name)) {
						success = true;
						return scope.getDefinedType(name);
					}
					return null;
				}
		        @Override public boolean success() { return success; }
		    });
	}


	@Override public String toString() {
		StringBuilder name = new StringBuilder();
		String self = getClass().getSimpleName();
		name.append(self);
		if (isFunction()) {
			name.append(" (");
			name.append(Utils.join(getParameterTypes(), " "));
			name.append(" ->");
		}
		name.append(" ");
		if (!getReturnType().equals(this)) {
			name.append(self);
		}
		else {
			name.append(getReturnType());
		}
		if (isFunction()) {
			name.append(")");
		}
		return name.toString();
	}
}