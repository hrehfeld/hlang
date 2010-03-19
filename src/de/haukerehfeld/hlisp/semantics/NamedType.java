package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;


public class NamedType extends AnonymousType {
	private String name;

	public NamedType(String name, Type parent, Signature returnType) {
		super(parent, returnType, false);
		setName(name);
	}

	public NamedType(String name, Type parent, Signature returnType, boolean isFunction) {
		super(parent, returnType, isFunction);
		setName(name);
	}
	
	
	public NamedType(String name, Type parent, Signature returnType, boolean isFunction,
	                 List<Signature> parameterTypes, List<String> parameterNames) {
		super(parent, returnType, isFunction, parameterTypes, parameterNames);
		setName(name);
	}

	public NamedType(String name, Type parent, Signature returnType,
	                 List<Signature> parameterTypes, List<String> parameterNames) {
		super(parent, returnType, parameterTypes, parameterNames);
		setName(name);
	}

	public NamedType(String name, Type parent, Signature signature, List<String> parameterNames) {
		super(parent, signature, parameterNames);
		setName(name);
	}

	@Override public boolean hasName() { return true; }
	private void setName(String name) { this.name = name; }
	public String getName() { return name; }

	@Override public String toString() { return getName(); }
}