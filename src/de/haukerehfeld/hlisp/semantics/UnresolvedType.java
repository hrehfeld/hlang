package de.haukerehfeld.hlisp.semantics;

import java.util.*;
import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.HashUtil;
import de.haukerehfeld.hlisp.EqualsUtil;

public class UnresolvedType implements Type {
	private final static boolean transparent = true;
	
	private List<String> names;

	private Type type;

	public UnresolvedType(String names) {
		this.names = new ArrayList<String>();
		this.names.add(names);
	}
	public UnresolvedType(List<String> names) {
		this.names = names;
	}

	public void setResolved(Type t) {
		this.type = t;
	}

	/**
	 * get resolvedType
	 */
	public Type getResolved() { resolvedOrException(); return type; }

	public boolean isResolved() {
		return type != null;
	}

	public List<String> getNames() { return names; }
	public void setNames(List<String> names) { this.names = names; }


	@Override public String getName() { return Utils.join(names, ", "); }

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

	@Override public boolean isPublic() {
		return getResolved().isPublic();
	}
	

	@Override public Instruction getInstruction() {
		resolvedOrException();
		return type.getInstruction();
	}

	@Override public void setInstruction(Instruction i) {
		getResolved().setInstruction(i);
	}
	

	
	@Override public Type getParent() {
		resolvedOrException();
		return type.getParent();
	}
	
	@Override public List<Type> getParameterTypes() {
		resolvedOrException();
		return type.getParameterTypes();
	}
	@Override public List<String> getParameterNames() {
		resolvedOrException();
		return type.getParameterNames();
	}
	@Override public Type getDefinedType(String type) {
		resolvedOrException();
		return this.type.getDefinedType(type);
	}
	@Override public Type getDefinedTypeRecursive(String type) {
		resolvedOrException();
		return this.type.getDefinedTypeRecursive(type);
	}

	@Override public Collection<Type> getDefinedTypes() {
		resolvedOrException();
		return type.getDefinedTypes();
	}

	@Override public void defineType(Type t) {
		resolvedOrException();
		type.defineType(t);
	}

	@Override public boolean isTypeDefined(String t) {
		resolvedOrException();
		return type.isTypeDefined(t);
	}

	@Override public boolean isTypeDefinedRecursive(String t) {
		resolvedOrException();
		return type.isTypeDefinedRecursive(t);
	}
	

	@Override public <T> T runOnHierarchy(Type.TypeMethod<T> method) {
		return getResolved().runOnHierarchy(method);
	}

	@Override public Type getReturnType() {
		resolvedOrException();
		return type.getReturnType();
	}

	@Override public String toString() {
		if (!transparent || !isResolved()) {
			return getClass().getSimpleName() + "(" + getName() + ", " + (isResolved() ? "resolved": "unresolved") + ")";
		}
		else {
			return type.toString();
		}
	}

	@Override public boolean equals(Object o) {
		return
		    (isResolved() && type.equals(o))
		    || (o instanceof UnresolvedType
		        && EqualsUtil.equal(isResolved(), ((UnresolvedType) o).isResolved())
		        && (isResolved() ?
		            EqualsUtil.equal(type, ((UnresolvedType) o).getResolved())
                      : EqualsUtil.equal(((UnresolvedType) o).getName(), getName()))
		    );
	}

	@Override public int hashCode() {
		return isResolved() ? type.hashCode() : 435435 + names.hashCode();
	}
}
