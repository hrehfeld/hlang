package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.JavaEmitter;

public interface Type {
	/**
	 * Check if a type named <type> is defined
	 */
	public boolean isTypeDefined(String type);

	public Type getDefinedType(String type);

	public void defineType(Type type);

	public List<Type> getDefinedTypes();

	/**
	 * get parent
	 */
	public Type getParent();

	/**
	 * get parameters
	 */
	public List<Parameter> getParameters();

	public Body getBody();

	public Type getReturnType();

	/**
	 * Emit code
	 */
	public String emit(JavaEmitter e);

	/**
	 * get name
	 */
	public String getName();
}