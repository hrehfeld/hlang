package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class FullType extends ParametricType implements Type {
	private final String name;

	public FullType(Type parent, Body body, Type returnType, List<Parameter> params, String name) {
		super(parent, body, returnType, params);
		this.name = name;
	}
	
	/**
	 * get name
	 */
	@Override public String getName() { return name; }

	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) { return emitter.emit(this); }
}