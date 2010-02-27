package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class Variable<T extends Type> {
	private final T type;
	private final String name;
	private final Value value;
	
	public Variable(T type, String name) {
		this(type, name, null);
	}

	public Variable(T type, String name, Value value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}

	/**
	 * get name
	 */
	public String getName() { return name; }

	/**
	 * get type
	 */
	public T getType() { return type; }

	/**
	 * get value
	 */
	public Value getValue() { return value; }
}