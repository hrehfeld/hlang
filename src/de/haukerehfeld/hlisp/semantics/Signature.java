package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.JavaEmitter;

public interface Signature {
	public String getName();
	public boolean hasName();
	
	public boolean isResolved();
	public boolean isFunction();

	/**
	 * get parameters
	 */
	public List<Signature> getParameterTypes();
	public void setParameterTypes(List<Signature> t);

	public Signature getReturnType();
	public void setReturnType(Signature t);

	public boolean isCompatible(Signature other);
}

