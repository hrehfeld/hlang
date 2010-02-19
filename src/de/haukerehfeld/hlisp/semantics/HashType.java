package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class HashType implements Type {
	private LinkedHashMap<String, Type> members = new LinkedHashMap<String, Type>();

	private final Type parent;

	private final List<Parameter> params;
	private final String name;
	private final Type returnType;
	private Body body;

	public HashType(Type parent, String name, Body body, Type returnType) {
		this(parent, name, new ArrayList<Parameter>(), body, returnType);
	}

	public HashType(Type parent, String name, List<Parameter> params, Body body, Type returnType) {
		this.parent = parent;
		this.name = name;
		this.params = params;
		this.body = body;
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
			t = parent.getDefinedType(type);
			parent = parent.getParent();
		}

		return t;
	}

	@Override public List<Type> getDefinedTypes() {
		return new ArrayList(members.values());
	}


	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		return emitter.emit(this);
	}

	@Override public Type getParent() {
		return parent;
	}
	


	/**
	 * get name
	 */
	public String getName() { return name; }
    
	/**
	 * get parameters
	 */
	public List<Parameter> getParameters() { return params; }
    
	public Type getReturnType() {
		return this.returnType;
	}

	/**
	 * get body
	 */
	public Body getBody() { return body; }
    
/**
 * set body
 */
	public void setBody(Body body) { this.body = body; }
}