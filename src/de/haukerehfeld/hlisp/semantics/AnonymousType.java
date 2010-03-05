package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class AnonymousType implements Type {
	private static boolean hashing = false;
	private static boolean equalling = false;

	public AnonymousType(Type parent, Type returnType) {
		this(parent, returnType, false);
	}

	public AnonymousType(Type parent, Type returnType, boolean isFunction) {
		this(parent, returnType, isFunction, Collections.<Type>emptyList());
	}
	
	public AnonymousType(Type parent,
	                     Type returnType,
	                     List<Type> parameterTypes) {
		this(parent, returnType, parameterTypes, Collections.<String>emptyList());
	}

	public AnonymousType(Type parent,
	                     Type returnType,
	                     boolean isFunction,
	                     List<Type> parameterTypes) {
		this(parent, returnType, isFunction, parameterTypes, Collections.<String>emptyList());
	}

	
	public AnonymousType(Type parent,
	                     Type returnType,
	                     List<Type> parameterTypes,
	                     List<String> parameterNames) {
		this(parent, returnType, true, parameterTypes, parameterNames);
	}

	public AnonymousType(Type parent,
	                     Type returnType,
	                     boolean isFunction,
	                     List<Type> parameterTypes,
	                     List<String> parameterNames) {
		this.parent = parent;
		this.returnType = returnType;
		this.isFunction = true;
		this.parameterTypes = parameterTypes;
		this.parameterNames = parameterNames;
		defineType(new SelfType(this));
	}


	private Instruction instruction;
	public Instruction getInstruction() { return instruction; }
	public void setInstruction(Instruction instruction) { this.instruction = instruction; }


	private boolean isFunction;
	public boolean isFunction() { return isFunction; }
	public void setIsFunction(boolean isFunction) { this.isFunction = isFunction; }

	private boolean isStatic = false;
	public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
	@Override public boolean isStatic() { return isStatic; }

	@Override public boolean isResolved() { return true; }
	

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

	private List<String> parameterNames;
	public List<String> getParameterNames() { return parameterNames; }
	public void setParameterNames(List<String> parameterNames) { this.parameterNames = parameterNames; }
	

	/** return type */
	private Type returnType;
	@Override public Type getReturnType() { return returnType; }
	public void setReturnType(Type returnType) { this.returnType = returnType; }

	private final LinkedHashMap<String, Type> types = new LinkedHashMap<String, Type>();
	@Override public Collection<Type> getDefinedTypes() { return types.values(); }
	@Override public boolean isTypeDefined(String v) {
		return types.get(v) != null;
	}
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

	@Override public String getName() {
		StringBuilder name = new StringBuilder();
		if (isFunction()) {
			name.append(" (");
			name.append(Utils.join(getParameterTypes(), " "));
			name.append(" ->");
		}
		name.append(" ");
		if (getReturnType().equals(this)) {
			name.append("self");
		}
		else {
			name.append(getReturnType());
		}
		if (isFunction()) {
			name.append(")");
		}
		return name.toString();
	}


	@Override public String toString() {
		return getName();
	}

	@Override public boolean equals(Object o) {
		if (o instanceof UnresolvedType) {
			return o.equals(this);
		}
		return super.equals(o);
	}

	// @Override public boolean equals(Object o) {
	// 	if ( this == o ) return true;

	// 	if ( !(o instanceof AnonymousType) ) return false;
	// 	boolean entryEqualling = equalling;
	// 	AnonymousType that = (AnonymousType) o;
	// 	equalling = true;
	// 	boolean result = true 
	// 	    && (entryEqualling || EqualsUtil.equal(this.parent, that.parent))
	// 	    && (entryEqualling || EqualsUtil.equal(this.returnType, that.returnType))
	// 	    && EqualsUtil.equal(this.isFunction, that.isFunction)
	// 	    && (entryEqualling || EqualsUtil.equal(this.parameterTypes, that.parameterTypes))
	// 	    && EqualsUtil.equal(this.parameterNames, that.parameterNames)
	// 	    && EqualsUtil.equal(this.instruction, that.instruction)
	// 	    && EqualsUtil.equal(this.isStatic, that.isStatic)
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
		
	// 	if (parent != null && !parent.equals(this)) {
	// 		result = HashUtil.hash(result, parent);
	// 	}
	// 	if (!returnType.equals(this)) {
	// 		result = HashUtil.hash(result, returnType);
	// 	}
	// 	result = HashUtil.hash(result, isFunction);
	// 	for (Type t: parameterTypes) {
	// 		if (!t.equals(this)) {
	// 			result = HashUtil.hash(result, t);
	// 		}
	// 	}
	// 	result = HashUtil.hash(result, parameterNames);
	// 	result = HashUtil.hash(result, instruction);
	// 	result = HashUtil.hash(result, isStatic);
	// 	hashing = false;
	// 	return result;
	// }	
}