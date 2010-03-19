package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class AnonymousSignature implements Signature {
	public AnonymousSignature(Signature returnType) {
		this(returnType, false);
	}

	public AnonymousSignature(Signature returnType, boolean isFunction) {
		this(returnType, isFunction, Collections.<Signature>emptyList());
	}
	
	public AnonymousSignature(Signature returnType,
	                          boolean isFunction,
	                          List<Signature> parameterTypes) {
		this.returnType = returnType;
		this.isFunction = isFunction;
		this.parameterTypes = parameterTypes;
	}

	@Override public boolean hasName() { return false; }
	@Override public String getName() {
		StringBuilder name = new StringBuilder();
		List<String> parameters = new ArrayList<String>();
		for (Signature t: getParameterTypes()) {
			parameters.add(t.toString());
		}
		if (isFunction()) {
			name.append("(");
			name.append(Utils.join(parameters, " "));
			name.append(" ->");
		}
		name.append(" ");
		if (getReturnType().equals(this)) {
			name.append("self");
		}
		else {
			name.append(getReturnType().toString());
		}
		if (isFunction()) {
			name.append(")");
		}
		return name.toString();
	}

	@Override public boolean isResolved() { return true; }

	private boolean isFunction;
	@Override public boolean isFunction() { return isFunction; }
	public void setIsFunction(boolean isFunction) { this.isFunction = isFunction; }

	/** params */
	private List<Signature> parameterTypes;
	@Override public List<Signature> getParameterTypes() { return parameterTypes; }
	@Override public void setParameterTypes(List<Signature> parameterTypes) { this.parameterTypes = parameterTypes; }

	/** return type */
	private Signature returnType;
	@Override public Signature getReturnType() { return returnType; }
	@Override public void setReturnType(Signature returnType) { this.returnType = returnType; }

	@Override public String toString() {
		return getName();
	}

	@Override public boolean isCompatible(Signature that) {
		if (this == that) return true;

		boolean parametersEqual = true;
		int i = 0;
		List<Signature> otherPs = that.getParameterTypes();
		for (Signature p : getParameterTypes()) {
			if (i >= otherPs.size()) {
				parametersEqual = false;
				break;
			}
			parametersEqual = parametersEqual
			    && (EqualsUtil.equal(p, otherPs.get(i)));
			i++;
		}
		// System.out.println("parameters " + (parametersEqual ? "equal" : "not equal"));
		// System.out.println("function " + EqualsUtil.equal(this.isFunction, that.isFunction));
		// System.out.println("returntype " + EqualsUtil.equal(this.returnType, that.returnType));
		// System.out.println(this.returnType + " vs. " + that.returnType);
		
		boolean result = true 
		    && EqualsUtil.equal(this.isFunction, that.isFunction())
		    && (EqualsUtil.equal(this.returnType, that.getReturnType()))
		    && parametersEqual
		    ;

		return result;
	}

	@Override public boolean equals(Object o) {
		return o instanceof Signature && this.isCompatible((Signature) o);
	}

	@Override public int hashCode() {
		int result = HashUtil.SEED;
		
		result = HashUtil.hash(result, isFunction);
		if (!returnType.equals(this)) {
			result = HashUtil.hash(result, returnType);
		}
		result = HashUtil.hash(result, isFunction);
		for (Signature t: parameterTypes) {
			if (!t.equals(this)) {
				result = HashUtil.hash(result, t);
			}
		}
		return result;
	}
}