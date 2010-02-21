package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class AbstractNativeType extends AbstractType implements NativeType {
	private final String nativeName;
	private final String name;
	
	public AbstractNativeType(Type parent, Type returnType, String name, String nativeName) {
		super(parent, returnType);
		this.nativeName = nativeName;
		this.name = name;
	}

	@Override public Body getBody() { throw new RuntimeException("Native type!") ; }


	/**
	 * get name
	 */
	@Override public String getName() { return name; }

	/**
	 * get nativeName
	 */
	@Override public String getNativeName() { return nativeName; }
}