package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public interface Function extends Value {
	public List<String> getParameterNames();
	public Value getValue();
	public List<Value> getValues();
}