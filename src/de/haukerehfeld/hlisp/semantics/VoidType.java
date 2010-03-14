package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class VoidType implements Type {
	public static final String NAME = "void";

	private VoidType(Type parent) {
		this.parent = parent;
	}


	/**
	 * Factory method to get a voidtype reference
	 */
	public static UnresolvedSignature create() { return new UnresolvedSignature(NAME); }

	public static VoidType create(RootType root) { return new VoidType(root); }

	public Instruction getInstruction() { return new VoidInstruction(); }

	public boolean isFunction() { return true; }
	@Override public boolean isStatic() { return true; }
	@Override public boolean isResolved() { return true; }
	
	@Override public boolean isPublic() { return true; }
	
	/** Parent type */
	private final Type parent;
	@Override public Type getParent() { return parent; }

	@Override public List<Signature> getParameterTypes() { return Collections.<Signature>emptyList(); }
	@Override public void setParameterTypes(List<Signature> t) {
		if (!t.isEmpty()) { throw new UnsupportedOperationException();}
	}

	public List<String> getParameterNames() { return Collections.<String>emptyList(); }


	@Override public void setReturnType(Signature t) {
		if (!t.equals(this)) {
			throw new UnsupportedOperationException();
		}
	}
	@Override public Signature getReturnType() { return this; }

	@Override public Collection<Type> getDefinedTypes() { return Collections.<Type>emptyList(); }
	@Override public boolean isTypeDefined(String v) { return false; }
	@Override public void defineType(Type t) { }
	@Override public Type getDefinedType(String name) { return null; }
	@Override public <T> T runOnHierarchy(Type.TypeMethod<T> method) { return parent.runOnHierarchy(method); }
	@Override public boolean isTypeDefinedRecursive(final String name) { return parent.isTypeDefinedRecursive(name); }
	@Override public Type getDefinedTypeRecursive(final String name) { return parent.getDefinedTypeRecursive(name); }

	@Override public void setInstruction(Instruction i) {}

	@Override public boolean hasName() { return true; }
	@Override public String getName() {
		return NAME;
	}

	@Override public boolean isCompatible(Signature o) {
		if (o instanceof UnresolvedSignature) { return o.equals(this); }
		return o instanceof VoidType;
	}

	@Override public String toString() {
		return getName();
	}
	
	
}
