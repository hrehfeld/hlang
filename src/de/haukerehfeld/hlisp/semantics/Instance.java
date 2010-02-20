package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class Instance<T extends Type> {
	private final T value;
	
	public Instance(T value) {
		this.value = value;
	}

	/**
	 * get value
	 */
	public T getValue() { return value; }
}