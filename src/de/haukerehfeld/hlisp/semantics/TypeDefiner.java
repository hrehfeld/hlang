package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import java.util.*;

/**
 * Walk the AST and do nothing but defining types
 */
public class TypeDefiner implements HLispParserVisitor {
	private final Deque<Type> structs = new ArrayDeque<Type>();

	public TypeDefiner(Type root) {
		structs.add(root);
	}

	/**
	 * Define a Type
	 */
	public Object visit(AstDefine node, Object data) throws SemanticException {
		Type currentScope = structs.peekLast();

		Type defined = read(node, currentScope);

		currentScope.defineType(defined);

		structs.offerLast(defined);

		UnresolvedBody body = (UnresolvedBody) defined.getBody();
		for (Node n: body.getBodyNode()) {
			if (n instanceof AstDefine) {
				visit((AstDefine) n, data);
			}
		}
		
		structs.pollLast();

		return null;
	}

	private List<Parameter> getParameters(Node node) throws SemanticException {
		AstList parameterNode = SemanticsUtils.castNode(node, AstList.class,
		                                  "start of parameterlist expected to be %1$s,"
		                                  + " but %2$s given!");
		
		List<Parameter> parameters = new ArrayList<Parameter>(parameterNode.jjtGetNumChildren());
		for (Node parameter: parameterNode) {
			Iterator<Node> pair = SemanticsUtils.castNode(parameter,
			                                AstList.class,
			                                "parameter needs to be a %1$s, but %2$s given!")
			    .iterator();

			//type
			if (!pair.hasNext()) {
				throw new SemanticException("Parameter Type Expected, but nothing found");
			}
			String type = SemanticsUtils.castNode(pair.next(), AstIdentifier.class,
			                        "(Type-)%s expected, but %s given!")
			    .getName();

			//name
			if (!pair.hasNext()) {
				throw new SemanticException("Parameter Name Expected, but nothing found");
			}
			String name = SemanticsUtils.castNode(pair.next(), AstIdentifier.class,
			                        "(Name-)%s expected, but %s given!")
			    .getName();

			parameters.add(new Parameter(new UnresolvedType(type), name));
		}
		return parameters;
	}

	/**
	 * Define a Type
	 *
	 * @return function body list
	 */
	public Type read(AstDefine node, Type currentScope) throws SemanticException {
		
		Iterator<Node> children = node.iterator();
		
		String identifier = SemanticsUtils.castNode(children.next(), AstIdentifier.class,
			                        "(Type-)%s expected, but %s given!")
			    .getName();

		if (currentScope.isTypeDefined(identifier)) {
			throw new SemanticException("Type already defined: " + identifier);
		}

		List<Parameter> parameters = getParameters(children.next());

		System.out.println("Defining new Type " + identifier);

		AstBody unresolvedBody = SemanticsUtils.castNode(children.next(), AstBody.class,
		                                           "body (e.g. a %s) expected, but %2$s given!");
		Type t = new HashType(currentScope,
		                      identifier,
		                      parameters,
		                      new UnresolvedBody(unresolvedBody),
		                      new UnresolvedType("Void"));
		return t;
	}
	
	public Object visit(AstBody body, Object data) throws SemanticException {
		throw new RuntimeException("Should never happen!");
	}

	
	public Object visit(AstRoot node, Object data) throws SemanticException {
		for (Node n: node) {
			n.jjtAccept(this, data);
		}

		return null;
	}
	
	public Object visit(SimpleNode node, Object data)  throws SemanticException {
		System.out.println("Simplenode:" + node.getClass());
		return null;
	}
	public Object visit(AstDontEval node, Object data)  throws SemanticException {
		visit((SimpleNode) node, data); return null;
	}
	
	public Object visit(AstIdentifier node, Object data)  throws SemanticException {
		visit((SimpleNode) node, data); return null;
	}
	
	public Object visit(AstInstantiate node, Object data) throws SemanticException {
		visit((SimpleNode) node, data);
		return null;
	}
	

	public Object visit(AstFloat node, Object data) throws SemanticException {
		visit((SimpleNode) node, data);
		return null;
	}
	
	public Object visit(AstString node, Object data) throws SemanticException {
		visit((SimpleNode) node, data);
		return null;
	}
	
	public Object visit(AstInteger node, Object data) throws SemanticException {
		visit((SimpleNode) node, data);
		return null;
	}

	public Object visit(AstList node, Object data) throws SemanticException {
		System.out.println("List: " + node.jjtGetNumChildren());
		for (Node n: node) {
			n.jjtAccept(this, data);
		}

		return null;
	}
}
