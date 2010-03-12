package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class NativeInstruction implements Instruction {
	private final String nativeCode;

	private final Type returnType;
	
	public NativeInstruction(Type returnType, String nativeCode) {
		this.returnType = returnType;
		this.nativeCode = nativeCode;
	}

	@Override public Type getReturnType() { return returnType; }

	public String getNativeCode() { return nativeCode; }

	@Override public String toString() {
		String s = getNativeCode();
		int maxlength = 30;
		if (s.length() > maxlength) {
			s = s.substring(0, maxlength) + "...";
		}
		String r = "-{ " + s.replace("\n", "") + " }-";
		if (returnType.hasName()) {
			r = r + " " + returnType.getName();
		}
		return r;
	}
}