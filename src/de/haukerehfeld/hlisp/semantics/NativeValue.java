package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class NativeValue extends AbstractValue {
	final String nativeCode;
	
	public NativeValue(Value scope, Type type, String nativeCode) {
		super(type, scope);
		this.nativeCode = nativeCode;
	}

	public String getNativeCode() { return nativeCode; }

	@Override public String toString() { return super.toString() + "('" + getNativeCode().substring(0, (int) Math.min(getNativeCode().length(), 20)).replace("\n", "") + "...')"; }
}