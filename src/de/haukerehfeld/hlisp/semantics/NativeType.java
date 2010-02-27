package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class NativeType extends AnonymousType {
	String name;
	
	public NativeType(String name) {
		super(null, VoidType.create(), false);
		this.name = name;
	}

	public String getName() {
		return name;
	}


	@Override public String toString() {
		return getClass().getSimpleName() + "(" + name + ")";
	}
}