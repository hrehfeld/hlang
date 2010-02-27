package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.JavaEmitter;

public interface Type {
	public Type getParent();

	public boolean isFunction();

	/** Can the type be instantiated? */
	public boolean isStatic();

	/**
	 * get parameters
	 */
	public List<Type> getParameterTypes();

	public Type getReturnType();

	/** access types directly defined in this type */
	public Map<String,Type> getDefinedTypes();
	public Type getDefinedType(String name);
	public boolean isTypeDefined(String v);
	public void defineType(String name, Type v);

}