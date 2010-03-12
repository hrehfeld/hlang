package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;


public class NamedType extends AnonymousType {
	private String name;

	public NamedType(String name, Type parent, Type returnType) {
		super(parent, returnType, false);
		setName(name);
	}

	public NamedType(String name, Type parent, Type returnType, boolean isFunction) {
		super(parent, returnType, isFunction);
		setName(name);
	}
	
	
	public NamedType(String name, Type parent, Type returnType, boolean isFunction,
	                 List<Type> parameterTypes, List<String> parameterNames) {
		super(parent, returnType, isFunction, parameterTypes, parameterNames);
		setName(name);
	}

	public NamedType(String name, Type parent, Type returnType,
	                 List<Type> parameterTypes, List<String> parameterNames) {
		super(parent, returnType, parameterTypes, parameterNames);
		setName(name);
	}

	@Override public boolean hasName() { return true; }
	private void setName(String name) { this.name = name; }
	public String getName() { return name; }

	@Override public String toString() { return getName() ; }

	// @Override public boolean equals(Object o) {
	// 	if ( this == o ) return true;

	// 	if ( !(o instanceof NamedType) ) return false;
		
	// 	NamedType that = (NamedType) o;

	// 	return super.equals(that) &&  EqualsUtil.equal(this.name, that.name);
	// }

	// @Override public int hashCode() {
	// 	int result = super.hashCode();
	// 	result = HashUtil.hash(result, name);
	// 	return result;
	// }	
	
}