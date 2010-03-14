package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class AnonymousType implements Type {
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
		this(parent,
		     new AnonymousSignature(returnType, isFunction, parameterTypes),
		     parameterNames);
	}
	
	public AnonymousType(Type parent, Signature signature, List<String> parameterNames) {
		this.parent = parent;
		this.signature = signature;

		this.parameterNames = parameterNames;
		this.types.put("this", new SelfType(this));
	}

	@Override public boolean hasName() { return false; }

	private Instruction instruction = new VoidInstruction();
	@Override public Instruction getInstruction() { return instruction; }
	@Override public void setInstruction(Instruction instruction) { this.instruction = instruction; }

	@Override public boolean isResolved() { return signature.isResolved(); }
	

	@Override public boolean isFunction() { return signature.isFunction(); }
	//@Override public void setIsFunction(boolean isFunction) { signature.setIsFunction(isFunction); }

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
	@Override public List<Signature> getParameterTypes() { return signature.getParameterTypes(); }
	@Override public void setParameterTypes(List<Signature> parameterTypes) {
		signature.setParameterTypes(parameterTypes);
	}

	private List<String> parameterNames;
	@Override public List<String> getParameterNames() { return parameterNames; }
	public void setParameterNames(List<String> parameterNames) { this.parameterNames = parameterNames; }
	

	/** return type */
	@Override public Signature getReturnType() { return signature.getReturnType(); }
	@Override public void setReturnType(Signature returnType) {
		signature.setReturnType(returnType);
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
			System.out.println("parameter " + name + "  from " + this);
			new TypePrinter().print(this);
			return new NamedType(name, this, signature.getParameterTypes().get(i), false);
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

	@Override public boolean isCompatible(Signature s) { return signature.isCompatible(s); }

	@Override public String getName() {
		return signature.getName();
	}


	@Override public String toString() {
		return signature.toString();
	}

	// @Override public boolean equals(Object o) {
	// 	if (o instanceof UnresolvedType) {
	// 		return o.equals(this);
	// 	}
	// 	if (!(o instanceof AnonymousType)) { return false; }
	// 	if (hasName()) { return super.equals(o); }

	// 	if ( this == o ) return true;

	// 	boolean entryEqualling = equalling;
	// 	AnonymousType that = (AnonymousType) o;
	// 	equalling = true;
	// 	boolean parametersEqual = true;
	// 	int i = 0;
	// 	List<Signature> otherPs = that.getParameterTypes();
	// 	for (Signature p : getParameterTypes()) {
	// 		if (i >= otherPs.size()) {
	// 			parametersEqual = false;
	// 			break;
	// 		}
	// 		parametersEqual = parametersEqual
	// 		    && (entryEqualling || EqualsUtil.equal(p, otherPs.get(i)));
	// 		i++;
	// 	}
	// 	// System.out.println("parameters " + (parametersEqual ? "equal" : "not equal"));
	// 	// System.out.println("function " + EqualsUtil.equal(this.isFunction, that.isFunction));
	// 	// System.out.println("returntype " + EqualsUtil.equal(this.returnType, that.returnType));
	// 	// System.out.println(this.returnType + " vs. " + that.returnType);
		
	// 	boolean result = true 
	// 	    && EqualsUtil.equal(isFunction(), that.isFunction())
	// 	    && (entryEqualling || EqualsUtil.equal(getReturnType(), that.getReturnType()))
	// 	    && parametersEqual
	// 	    ;

	// 	equalling = false;
	// 	return result;
	// }

	// @Override public int hashCode() {
	// 	hashing = true;
	// 	int result = HashUtil.SEED;
	// 	if (hashing) {
	// 		return result;
	// 	}
		
	// 	result = HashUtil.hash(result, isFunction());
	// 	if (!getReturnType().equals(this)) {
	// 		result = HashUtil.hash(result, getReturnType());
	// 	}
	// 	result = HashUtil.hash(result, isFunction());
	// 	for (Signature t: parameterTypes) {
	// 		if (!t.equals(this)) {
	// 			result = HashUtil.hash(result, t);
	// 		}
	// 	}
	// 	hashing = false;
	// 	return result;
	// }
}