package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class NativeInstruction implements Instruction {
	private final String nativeCode;

	private Signature returnType;
	
	public NativeInstruction(Signature returnType, String nativeCode) {
		this.returnType = returnType;
		this.nativeCode = nativeCode;
	}

	@Override public Signature getReturnType() { return returnType; }
	public void setReturnType(Signature r) { this.returnType = r; }
	

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