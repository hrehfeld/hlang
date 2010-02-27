package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class Parameter {
	private String name;
	private Type type;

	public Parameter(Type type, String name) {
		this.type = type;
		this.name = name;
	}
	
	/**
	 * get name
	 */
	public String getName() { return name; }
    
/**
 * set name
 */
	public void setName(String name) { this.name = name; }

	/**
	 * get type
	 */
	public Type getType() { return type; }
    
/**
 * set type
 */
	public void setType(Type type) { this.type = type; }

	public static List<Parameter> emptyList() {
		return Collections.emptyList();
	}
}