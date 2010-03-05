package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class VoidInstruction extends NativeInstruction {
	public VoidInstruction() {
		super(VoidType.create(), "null");
	}

	@Override public String toString() { return "void"; }
}