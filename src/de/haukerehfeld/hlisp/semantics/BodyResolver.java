package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import java.util.*;


/**
 * Walk through the types und resolve bodies
 */
public class BodyResolver {
	
	public void resolve(Type type) throws SemanticException {
		Body b = type.getBody();
		if (!(b instanceof UnresolvedBody)) {
			return;
		}
		UnresolvedBody body = (UnresolvedBody) b;
		parseBody(body.getBodyNode(), type);
	}

	private Body parseBody(AstBody body, Type currentScope) throws SemanticException {
		for (Node n: body) {
			if (n instanceof AstInstantiate) {
				parseInstantiate((AstInstantiate) n, currentScope);
			}
			else if (n instanceof AstList) {
				parseBodyLine((AstList) n, currentScope);
			}
			else {
				
			}
		}
		return null;
	}

	public void parseInstantiate(AstInstantiate instantiate, Type currentScope) throws
		SemanticException {
		Iterator<Node> children = instantiate.iterator();
		String typeName = SemanticsUtils.castNode(children.next(), AstIdentifier.class,
		                                "(Type-)%s expected, %s given.")
		    .getName();
		if (!currentScope.isTypeDefined(typeName)) {
			throw new SemanticException("Type " + typeName + " not defined in this scope.");
		}
		Type type = currentScope.getDefinedType(typeName);
		
		String name = SemanticsUtils.castNode(children.next(), AstIdentifier.class,
		                                "(Name-)%s expected, %s given.")
		    .getName();


		Instance inst = null;
		if (children.hasNext()) {
			inst = parseValue(children.next(), currentScope);
		}
		
		ResolvedBody body = new ResolvedBody();
		body.add(new Variable(type, name, inst));
	}

	public void parseBodyLine(AstList line, Type currentScope) {
	}

	public <T extends Type> Instance<T> parseValue(Node v, Type scope) {
		return null;
	}
}