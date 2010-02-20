package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class Variable<T extends Type> {
	private final T type;
	private final String name;
	private final Instance<T> value;
	
	public Variable(T type, String name) {
		this(type, name, null);
	}

	public Variable(T type, String name, Instance<T> value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
}