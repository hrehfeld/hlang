package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class DontCareType implements Type {
	public DontCareType(Type parent) {
		this.parent = parent;
	}
	public Instruction getInstruction() { return new VoidInstruction(); }

	public boolean isFunction() { return false; }
	@Override public boolean isStatic() { return true; }
	@Override public boolean isResolved() { return false; }
	
	@Override public boolean isPublic() { return true; }
	
	/** Parent type */
	private final Type parent;
	@Override public Type getParent() { return parent; }

	@Override public List<Type> getParameterTypes() { return Collections.<Type>emptyList(); }

	public List<String> getParameterNames() { return Collections.<String>emptyList(); }

	@Override public Type getReturnType() { return this; }

	@Override public Collection<Type> getDefinedTypes() { return Collections.<Type>emptyList(); }
	@Override public boolean isTypeDefined(String v) { return false; }
	@Override public void defineType(Type t) { }
	@Override public Type getDefinedType(String name) { return null; }
	@Override public <T> T runOnHierarchy(Type.TypeMethod<T> method) { return null; }
	@Override public boolean isTypeDefinedRecursive(final String name) { return false; }
	@Override public Type getDefinedTypeRecursive(final String name) { return null; }

	@Override public void setInstruction(Instruction i) {}

	@Override public boolean hasName() { return false; }	
	@Override public String getName() {
		return Type.DONTCARE;
	}

	@Override public boolean equals(Object o) {
		if (o instanceof Type) {
			return true;
		}
		return false;
	}

	@Override public String toString() {
		return getName();
	}
	
	
}
