package de.haukerehfeld.hlisp.semantics;

import java.util.*;
import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.HashUtil;
import de.haukerehfeld.hlisp.EqualsUtil;

public class UnresolvedType extends UnresolvedSignature implements Type {
	public UnresolvedType(String names) {
		super(names);
	}
	public UnresolvedType(List<String> names) {
		super(names);
	}

	public void setResolved(Type t) { super.setResolved(t);}

	/**
	 * get resolvedType
	 */
	public Type getResolved() { return (Type) super.getResolved(); }


	@Override public boolean isStatic() {
		return getResolved().isStatic();
	}

	@Override public boolean isFunction() {
		return getResolved().isFunction();
	}

	@Override public boolean isPublic() {
		return getResolved().isPublic();
	}


	@Override public Instruction getInstruction() {
		return getResolved().getInstruction();
	}

	@Override public void setInstruction(Instruction i) {
		getResolved().setInstruction(i);
	}



	@Override public Type getParent() {
		return getResolved().getParent();
	}

	@Override public List<String> getParameterNames() {
		return getResolved().getParameterNames();
	}
	@Override public Type getDefinedType(String type) {
		return this.getResolved().getDefinedType(type);
	}
	@Override public Type getDefinedTypeRecursive(String type) {
		return this.getResolved().getDefinedTypeRecursive(type);
	}

	@Override public Collection<Type> getDefinedTypes() {
		return getResolved().getDefinedTypes();
	}

	@Override public void defineType(Type t) {
		getResolved().defineType(t);
	}

	@Override public boolean isTypeDefined(String t) { return getResolved().isTypeDefined(t); }

	@Override public boolean isTypeDefinedRecursive(String t) {
		return getResolved().isTypeDefinedRecursive(t);
	}


	@Override public <T> T runOnHierarchy(Type.TypeMethod<T> method) {
		return getResolved().runOnHierarchy(method);
	}

}
