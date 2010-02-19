package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.parser.AstBody;

public class UnresolvedBody implements Body {
	private AstBody bodyNode;

	public UnresolvedBody(AstBody bodyNode) {
		this.bodyNode = bodyNode;
	}

	/**
	 * get bodyNode
	 */
	public AstBody getBodyNode() { return bodyNode; }

	@Override public List<Variable> getDefinedVariables() {
		throw new RuntimeException("Unresolved body.");
	}
	@Override public List<Instruction> getInstructions() {
		throw new RuntimeException("Unresolved body.");
	}
}