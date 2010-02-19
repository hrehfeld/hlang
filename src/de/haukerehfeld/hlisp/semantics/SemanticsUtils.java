package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;

public class SemanticsUtils {
	public static <T extends Node> T castNode(Node node, Class<T> c, String msg) throws
		SemanticException {
		if (!c.isInstance(node)) {
			String error = String.format(msg,
			                             c.getSimpleName(),
			                             node.getClass().getSimpleName());
			throw new SemanticException(error);
		}
		return c.cast(node);
	}
}
