package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class NativeType implements Type {
	String name;
	
	public NativeType(Type parent, String name) {
		this.parent = parent;
		this.name = name.trim();
		setInstruction(new VoidInstruction());
	}

	@Override public String getName() {
		return name;
	}


	@Override public boolean equals(Object o) {
		if (!(o instanceof NativeType)) { return false; }
		return EqualsUtil.equal(getName(), ((NativeType) o).getName());
	}

	@Override public int hashCode() {
		int result = HashUtil.SEED;
		return HashUtil.hash(result, getName());
	}
		

	@Override public String toString() {
		return "_{" + getName() + "}_";
	}

	public Instruction getInstruction() { return new VoidInstruction(); }

	public boolean isFunction() { return false; }
	@Override public boolean isStatic() { return true; }
	@Override public boolean isResolved() { return true; }
	
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
	@Override public <T> T runOnHierarchy(Type.TypeMethod<T> method) { return parent.runOnHierarchy(method); }
	@Override public boolean isTypeDefinedRecursive(final String name) { return parent.isTypeDefinedRecursive(name); }
	@Override public Type getDefinedTypeRecursive(final String name) { return parent.getDefinedTypeRecursive(name); }

	@Override public void setInstruction(Instruction i) {}

	@Override public boolean hasName() { return true; }

}