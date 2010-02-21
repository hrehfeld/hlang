package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class Instance implements Value {
	private final Object value;
	
	public Instance(Object value) {
		this.value = value;
	}

	/**
	 * get value
	 */
	public Object getValue() { return value; }
}