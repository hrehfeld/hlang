package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class AnonymousType extends AnonymousSignature implements Type {
	private static boolean hashing = false;
	private static boolean equalling = false;

	private Signature signature;

	public AnonymousType(Type parent, Signature returnType) {
		this(parent, returnType, false);
	}

	public AnonymousType(Type parent, Signature returnType, boolean isFunction) {
		this(parent, returnType, isFunction, Collections.<Signature>emptyList());
	}
	
	public AnonymousType(Type parent,
	                     Signature returnType,
	                     boolean isFunction,
	                     List<Signature> parameterTypes) {
		this(parent, returnType, isFunction, parameterTypes, Collections.<String>emptyList());
	}

	
	public AnonymousType(Type parent,
	                     Signature returnType,
	                     List<Signature> parameterTypes,
	                     List<String> parameterNames) {
		this(parent, returnType,
		     true, parameterTypes, parameterNames);
	}

	public AnonymousType(Type parent,
	                     Signature returnType,
	                     boolean isFunction,
	                     List<Signature> parameterTypes,
	                     List<String> parameterNames) {
		super(returnType, isFunction, parameterTypes);
		this.parent = parent;
		this.parameterNames = parameterNames;

		this.types.put("this", new SelfType(this));
	}
	
	public AnonymousType(Type parent, Signature signature, List<String> parameterNames) {
		this(parent,
		     signature.getReturnType(),
		     signature.isFunction(),
		     signature.getParameterTypes(),
		     parameterNames);
	}

	@Override public boolean hasName() { return false; }

	private Instruction instruction = new VoidInstruction();
	@Override public Instruction getInstruction() { return instruction; }
	@Override public void setInstruction(Instruction instruction) { this.instruction = instruction; }

	@Override public boolean isResolved() { return super.isResolved(); }
	

	@Override public boolean isFunction() { return super.isFunction(); }
	//@Override public void setIsFunction(boolean isFunction) { super.setIsFunction(isFunction); }

	private boolean isStatic = false;
	public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
	@Override public boolean isStatic() { return isStatic; }


	private boolean isPublic = false;
	public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
	@Override public boolean isPublic() { return isPublic; }
	
	/** Parent type */
	private Type parent;
	@Override public Type getParent() { return parent; }
	@Override public void setParent(Type parent) { this.parent = parent; }

	/** params */
	@Override public List<Signature> getParameterTypes() { return super.getParameterTypes(); }
	@Override public void setParameterTypes(List<Signature> parameterTypes) {
		super.setParameterTypes(parameterTypes);
	}

	private List<String> parameterNames;
	@Override public List<String> getParameterNames() { return parameterNames; }
	public void setParameterNames(List<String> parameterNames) { this.parameterNames = parameterNames; }
	

	/** return type */
	@Override public Signature getReturnType() { return super.getReturnType(); }
	@Override public void setReturnType(Signature returnType) {
		super.setReturnType(returnType);
	}

	/** child types */
	private final LinkedHashMap<String, Type> types = new LinkedHashMap<String, Type>();

	@Override public Collection<Type> getDefinedTypes() { return types.values(); }

	public HashMap<String, Type> getDefinedTypesInternal() { return types; }

	@Override public Type getDefinedType(String name) {
		Type t = types.get(name);
		if (t != null) {
			return t;
		}
		int i = getParameterNames().indexOf(name);
		if (i >= 0) {
			Type p = new NamedType(name, this, super.getParameterTypes().get(i), false);
			System.out.println("parameter " + p + "  from " + this
			                   + " (" + getParent() + ", " + Utils.join(getParameterTypes(), ", ") + ")");
			new TypePrinter().print(p);
			return p;
		}
		return null;
	}
	
	@Override public boolean isTypeDefined(String v) {
		return getDefinedType(v) != null;
	}

	@Override public void defineType(Type t) { types.put(t.getName(), t); }
	
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

	@Override public Type getDefinedTypeRecursive(final String name) {
		return runOnHierarchy(new TypeMethod<Type>() {
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

	@Override public boolean isCompatible(Signature s) {
		if (equals(s)) {
			return true;
		}
		return super.isCompatible(s);
	}

	@Override public String getName() {
		return super.getName();
	}


	@Override public String toString() {
		return super.toString();
	}

	@Override public boolean equals(Object o) {
		if (!(o instanceof AnonymousType)) { return false; }
		if (o == this) { return true; }
		AnonymousType that = (AnonymousType) o;
		return super.equals(o);
	}

}