package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class DontCareSignature implements Signature {
	public boolean isFunction() { return false; }
	
	@Override public List<Signature> getParameterTypes() { return Collections.<Signature>emptyList(); }
	@Override public void setParameterTypes(List<Signature> t) { throw new UnsupportedOperationException();}

	@Override public Signature getReturnType() { return this; }
	@Override public void setReturnType(Signature t) { throw new UnsupportedOperationException();}

	@Override public boolean hasName() { return false; }	
	@Override public String getName() {
		return Type.DONTCARE;
	}

	@Override public boolean isResolved() { return true; }

	@Override public boolean isCompatible(Signature o) {
			return true;
	}

	@Override public String toString() {
		return getName();
	}
	
	
}
