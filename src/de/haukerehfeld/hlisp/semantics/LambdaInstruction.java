package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;

public class LambdaInstruction implements Instruction {
	private Type function;
	public Type getFunction() { return function; }
	public void setFunction(Type function) { this.function = function; }

	public LambdaInstruction(Type function) {
		this.function = function;
	}

	@Override public Type getReturnType() { return function; }

	@Override public String toString() {
		return "lambda__ " + function;
	}
	
}