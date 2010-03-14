package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class NativeSignature implements Signature {
	String name;
	
	public NativeSignature(String name) {
		this.name = name.trim();
	}

	@Override public boolean hasName() { return true; }

	@Override public String getName() {
		return name;
	}

	@Override public boolean isCompatible(Signature o) {
		if (!(o instanceof NativeSignature)) { return false; }
		return EqualsUtil.equal(getName(), ((NativeSignature) o).getName());
	}

	@Override public String toString() {
		return "_{" + getName() + "}_";
	}

	@Override public boolean isResolved() { return true; }
	
	public boolean isFunction() { return false; }
	@Override public List<Signature> getParameterTypes() { return Collections.<Signature>emptyList(); }
	@Override public void setParameterTypes(List<Signature> t) { throw new UnsupportedOperationException();}

	@Override public Signature getReturnType() { return this; }
	@Override public void setReturnType(Signature t) { if (t != this) { throw new UnsupportedOperationException();} }
}