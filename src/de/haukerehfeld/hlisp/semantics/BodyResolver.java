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

	private Body parseBody(AstNode body, Type currentScope) throws SemanticException {
		for (AstNode n: body) {
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
		Iterator<AstNode> children = instantiate.iterator();
		String typeName = SemanticsUtils.castNode(children.next(), AstIdentifier.class,
		                                "(Type-)%s expected, %s given.")
		    .getName();
		if (!currentScope.isTypeDefined(typeName)) {
			throw new SemanticException("Type " + typeName + " not defined in this scope.");
		}
		Type type = currentScope.getDefinedType(typeName);
		List<Parameter> params = type.getParameters();
		List<Type> expectedTypes = new ArrayList<Type>();
		for (Parameter param: params) {
			expectedTypes.add(param.getType());
		}
		
		
		String name = SemanticsUtils.castNode(children.next(), AstIdentifier.class,
		                                "(Name-)%s expected, %s given.")
		    .getName();


		Instance inst = null;
		if (children.hasNext()) {
			inst = parseValue(children.next(), currentScope, expectedTypes);
		}
		
		ResolvedBody body = new ResolvedBody();
		body.add(new Variable(type, name, inst));
	}

	public void parseBodyLine(AstList line, Type currentScope) {
	}

	public Instance parseValue(AstNode v, Type scope, List<Type> expectedTypes) throws
		SemanticException {
		//type with no constructor parameters 
		if (expectedTypes.isEmpty()) {
			if (!(v instanceof AstList) || v.jjtGetNumChildren() > 0) {
				throw new SemanticException("Expected empty list as ParameterValue.");
			}
		}
		//direct value
		if (v instanceof AstValue) {
			if (v instanceof AstIdentifier) {
			}
			else if (v instanceof AstInteger) {
			}
			else if (v instanceof AstFloat) {
			}
			else if (v instanceof AstString) {
			}
			else {
				throw new RuntimeException("Unknown Value Node.");
			}
		}
		else {
			for (AstNode n: v) {
				if (n instanceof AstInstantiate) {
					parseInstantiate((AstInstantiate) n, scope);
				}
				else if (n instanceof AstList) {
					parseBodyLine((AstList) n, scope);
				}
				else {
					
				}
			}
		}
		return null;
	}
}