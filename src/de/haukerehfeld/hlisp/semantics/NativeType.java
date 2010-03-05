package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class NativeType extends AnonymousType {
	String name;
	
	public NativeType(Type parent, String name) {
		super(parent, VoidType.create());
		setReturnType(this);
		this.name = name.trim();
	}

	public String getName() {
		return name;
	}


	@Override public boolean equals(Object o) {
		if (!(o instanceof NativeType)) { return false; }
		return EqualsUtil.equal(getName(), ((NativeType) o).getName());
	}

	@Override public int hashCode() {
		int result = HashUtil.SEED;
		return HashUtil.hash(result, getName());
	}
		

	@Override public String toString() {
		return "_{" + name + "}_";
	}
}