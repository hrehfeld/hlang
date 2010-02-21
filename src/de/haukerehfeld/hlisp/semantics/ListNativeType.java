package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class ListNativeType extends AbstractNativeType {
	public ListNativeType(Type parent) {
		super(parent, null, "List", "List");
		setReturnType(this);

		final NativeType add = new AbstractNativeType(this, this, "+=", "add");
		defineType(add);
		
	}
	
	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		return emitter.emit(this);
	}
}