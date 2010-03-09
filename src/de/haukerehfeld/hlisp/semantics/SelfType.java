package de.haukerehfeld.hlisp.semantics;

import java.util.*;
import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class SelfType implements Type {
	public SelfType(Type parent) {
		this.parent = parent;
	}

	public Instruction getInstruction() { return new NativeInstruction(this.parent, "this"); }

	public boolean isFunction() { return this.parent.isFunction(); }
	@Override public boolean isStatic() { return this.parent.isStatic(); }
	@Override public boolean isResolved() { return this.parent.isResolved(); }
	
	@Override public boolean isPublic() { return this.parent.isPublic(); }
	
	/** Parent type */
	private final Type parent;
	@Override public Type getParent() { return this.parent.getParent(); }

	@Override public List<Type> getParameterTypes() { return this.parent.getParameterTypes(); }

	public List<String> getParameterNames() { return this.parent.getParameterNames(); }

	@Override public Type getReturnType() { return this.parent.getReturnType(); }

	@Override public Collection<Type> getDefinedTypes() { return this.parent.getDefinedTypes(); }
	@Override public boolean isTypeDefined(String v) { return this.parent.isTypeDefined(v); }
	@Override public void defineType(Type t) { this.parent.defineType(t); }
	@Override public Type getDefinedType(String name) { return this.parent.getDefinedType(name); }
	@Override public <T> T runOnHierarchy(Type.TypeMethod<T> method) { return this.parent.runOnHierarchy(method); }
	@Override public boolean isTypeDefinedRecursive(final String name) { return this.parent.isTypeDefinedRecursive(name); }
	@Override public Type getDefinedTypeRecursive(final String name) { return this.parent.getDefinedTypeRecursive(name); }

	@Override public void setInstruction(Instruction i) { this.parent.setInstruction(i); }

	@Override public String getName() {
		return this.parent.getName() + " " + Type.SELF;
	}


	@Override public String toString() {
		return "(SelfType[" + this.parent.getName() + "])";
	}
}