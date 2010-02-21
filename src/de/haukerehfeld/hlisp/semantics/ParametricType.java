package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class ParametricType extends LanguageType {
	private final List<Parameter> params;

	public ParametricType(Type parent, Body body, Type returnType) {
		this(parent, body, returnType, new ArrayList<Parameter>());
	}

	public ParametricType(Type parent,
	                      Body body,
	                      Type returnType,
	                      List<Parameter> params) {
		super(parent, body, returnType);
		this.params = params;
	}
	
	/**
	 * get parameters
	 */
	public List<Parameter> getParameters() { return params; }
}