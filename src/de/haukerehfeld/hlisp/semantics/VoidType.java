package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class VoidType extends HashType {
	public VoidType(Type parent) {
		super(parent,
		      "Void",
		      new ArrayList<Parameter>(),
		      new ResolvedBody(),
		      null);
	}
	
	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		return emitter.emit(this);
	}

	@Override public Type getReturnType() { return this; }
}