package de.haukerehfeld.hlisp.semantics;

import java.util.*;
import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public abstract class AbstractType implements Type {
	private final LinkedHashMap<String, Type> members = new LinkedHashMap<String, Type>();

	private final Type parent;
	private Type returnType;

	public AbstractType(Type parent, Type returnType) {
		this.parent = parent;
		this.returnType = returnType;
	}

	@Override public void defineType(Type type) {
		members.put(type.getName(), type);
	}

	@Override public boolean isTypeDefined(String type) {
		return getDefinedType(type) != null;
	}

	@Override public Type getDefinedType(String type) {
		Type t = members.get(type);
		Type parent = getParent();
		while (t == null && parent != null) {
			//System.out.println("Searching " + type + " in " + parent);
			t = parent.getDefinedType(type);
			parent = parent.getParent();
		}

		return t;
	}

	@Override public List<Type> getDefinedTypes() {
		return new ArrayList(members.values());
	}

	@Override public Type getParent() {
		return parent;
	}
	
	@Override public Type getReturnType() {
		return this.returnType;
	}

	/**
	 * set returnType
	 */
	public void setReturnType(Type returnType) { this.returnType = returnType; }

	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) { return emitter.emit(this); }

	@Override public List<Parameter> getParameters() { return Collections.emptyList(); }

	@Override public String getName() { return "Anonymous(" + getReturnType().getName() + ")"; }

	@Override public String toString() {
		return getClass().getSimpleName() + " '" + getName() + "'";
	}
}