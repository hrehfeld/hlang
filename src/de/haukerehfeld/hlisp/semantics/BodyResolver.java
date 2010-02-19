package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;


/**
 * Walk through the types und resolve bodies
 */
public class BodyResolver {
	public void resolve(Type type) {
	}

	private Body parseBody(AstBody body, Object data, Type currentScope) throws SemanticException {
		for (Node n: body) {
			if (n instanceof AstDefine) {
				//ignore
			}
			else if (n instanceof AstInstantiate) {
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

		
		if (children.hasNext()) {
			
		}
		
		ResolvedBody body = new ResolvedBody();
		body.add(new Variable(type, name));
	}

	public void parseBodyLine(AstList line, Type currentScope) {
	}
}