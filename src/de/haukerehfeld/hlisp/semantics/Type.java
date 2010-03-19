package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.JavaEmitter;

public interface Type extends Signature {
	public static final String SELFTYPE = "this";
	public static final String SELF = "this";

	public static final String DONTCARE = "_";
	
	public Type getParent();
	public void setParent(Type t);

	/** Can the type be instantiated? */
	public boolean isStatic();

	public boolean isPublic();


	public <T> T runOnHierarchy(TypeMethod<T> m);

	public List<String> getParameterNames();
	public void setParameterNames(List<String> names);
	
	/** access types directly defined in this type */
	public Collection<Type> getDefinedTypes();
	public Type getDefinedType(String name);
	public Type getDefinedTypeRecursive(String name);
	public boolean isTypeDefined(String name);
	public boolean isTypeDefinedRecursive(String name);
	public void defineType(Type v);

	public Instruction getInstruction();
	public void setInstruction(Instruction i);
	

	public interface TypeMethod<T> {
		public T run(Type v);
		public boolean success();
	}
	
}