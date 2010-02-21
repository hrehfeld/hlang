package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class StringNativeType extends AbstractNativeType {
	public StringNativeType(Type parent) {
		super(parent, null, "String", "String");
		setReturnType(this);
	}

	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		return emitter.emit(this);
	}
}