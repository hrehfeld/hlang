package de.haukerehfeld.hlisp.semantics;

import java.util.*;
import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.HashUtil;
import de.haukerehfeld.hlisp.EqualsUtil;

public class UnresolvedSignature implements Signature {
	private final static boolean transparent = false;
	
	private List<String> names;

	private Signature type;

	public UnresolvedSignature(String names) {
		this.names = new ArrayList<String>();
		this.names.add(names);
	}
	public UnresolvedSignature(List<String> names) {
		this.names = names;
	}

	public void setResolved(Signature t) {
		this.type = t;
	}

	/**
	 * get resolvedType
	 */
	public Signature getResolved() { resolvedOrException(); return type; }

	public boolean isResolved() {
		return type != null;
	}

	public List<String> getNames() { return names; }
	public void setNames(List<String> names) { this.names = names; }


	@Override public boolean hasName() { return true; }
	@Override public String getName() {
		if (isResolved()) {
			return type.getName();
		}
		return Utils.join(names, ", ");
	}

	private void resolvedOrException() {
		if (!isResolved()) {
			throw new UnresolvedTypeException(this);
		}
	}

	@Override public boolean isFunction() {
		resolvedOrException();
		return type.isFunction();
	}

	@Override public List<Signature> getParameterTypes() {
		resolvedOrException();
		return type.getParameterTypes();
	}
	@Override public void setParameterTypes(List<Signature> t) { getResolved().setParameterTypes(t); }
	@Override public Signature getReturnType() {
		resolvedOrException();
		return type.getReturnType();
	}
	@Override public void setReturnType(Signature t) {
		getResolved().setReturnType(t);
	}


	@Override public String toString() {
		if (!transparent) {
			return getClass().getSimpleName() + "(" + getName() + ", " + (isResolved() ? "resolved": "unresolved") + ")";
		}
		else {
			return type.toString();
		}
	}

	@Override public boolean isCompatible(Signature o) {
		return
		    (isResolved() && type.isCompatible(o))
		    || (EqualsUtil.equal(isResolved(), ((UnresolvedSignature) o).isResolved())
		        && (isResolved() ?
		            EqualsUtil.equal(type, ((UnresolvedSignature) o).getResolved())
                      : EqualsUtil.equal(((UnresolvedSignature) o).getName(), getName()))
		    );
	}

	@Override public int hashCode() {
		return isResolved() ? type.hashCode() : 435435 + names.hashCode();
	}
}
