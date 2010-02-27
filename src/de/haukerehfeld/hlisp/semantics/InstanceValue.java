package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class InstanceValue extends AbstractValue {
	public InstanceValue(Value scope, Type type) {
		super(type, scope);
	}
}