package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public interface Body {
	public List<Variable> getDefinedVariables();

	/**
	 * get instructions
	 */
	public String getInstructions();
}