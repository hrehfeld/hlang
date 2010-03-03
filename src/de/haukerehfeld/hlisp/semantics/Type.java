package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.JavaEmitter;

public interface Type {
	public String getName();
	public Type getParent();

	public boolean isFunction();

	/** Can the type be instantiated? */
	public boolean isStatic();

	public boolean isPublic();

	public <T> T runOnHierarchy(TypeMethod<T> m);
	
	/**
	 * get parameters
	 */
	public List<Type> getParameterTypes();
	public List<String> getParameterNames();

	public Type getReturnType();

	/** access types directly defined in this type */
	public List<Type> getDefinedTypes();
	public Type getDefinedType(String name);
	public Type getDefinedTypeRecursive(String name);
	public boolean isTypeDefined(String name);
	public boolean isTypeDefinedRecursive(String name);
	public void defineType(Type v);

	public interface TypeMethod<T> {
		public T run(Type v);
		public boolean success();
	}
	
}