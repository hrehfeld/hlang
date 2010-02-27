package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;

public class SemanticsUtils {
	public static <T extends Node> T castNode(AstNode node, Class<T> c, String msg) throws
		SemanticException {
		if (!c.isInstance(node)) {
			String error = String.format(errorLocation(node) + msg,
			                             c.getSimpleName(),
			                             node.getClass().getSimpleName());
			throw new SemanticException(error);
		}
		return c.cast(node);
	}

	public static String errorLocation(AstNode node) {
		Token t = node.jjtGetFirstToken();
		String pos = "Line " + t.beginLine + "(:" + t.beginColumn + ")";
		t = node.jjtGetLastToken();
		pos += "-" + t.endLine + "(:" + t.endColumn + "): ";
		return pos;
	}
}
