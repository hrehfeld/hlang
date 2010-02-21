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

		AstBody body = ((UnresolvedBody) defined.getBody()).getBodyNode();
		Iterator<AstNode> it = body.iterator();
		AstNode b;
		if (it.hasNext() && (b = it.next()) instanceof AstList) {
			for (AstNode n: b) {
				if (n instanceof AstDefine) {
					visit((AstDefine) n, data);
				}
			}
		}
		
		structs.pollLast();

		return null;
	}

	private List<Parameter> getParameters(AstNode node) throws SemanticException {
		AstParameterList parameterNode = SemanticsUtils.castNode(node, AstParameterList.class,
		                                  "start of parameterlist expected to be %1$s,"
		                                  + " but %2$s given!");
		
		List<Parameter> parameters = new ArrayList<Parameter>(parameterNode.jjtGetNumChildren());
		for (AstNode parameter: parameterNode) {
			Iterator<AstNode> pair = SemanticsUtils.castNode(parameter,
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
		Iterator<AstNode> children = node.iterator();
		
		String identifier = SemanticsUtils.castNode(children.next(), AstIdentifier.class,
			                        "(Type-)%s expected, but %s given!")
			    .getName();

		if (currentScope.isTypeDefined(identifier)) {
			throw new SemanticException("Type already defined: " + identifier);
		}

		List<Parameter> parameters = getParameters(children.next());

		AstReturnType returnNode = SemanticsUtils.castNode(children.next(),
		                                                   AstReturnType.class,
		                                                   "(Returntype-)%s expected,"
		                                                   + " but %s given!");
		
		
		
		Type returnType = parseReturnType(returnNode, currentScope, identifier);
		
		
		System.out.println("Defining new Type " + identifier);

		AstBody unresolvedBody = SemanticsUtils.castNode(children.next(),
		                                                 AstBody.class,
		                                                 "(Body)%s expected, but %s given!");
		
		Type t = new FullType(currentScope,
		                      new UnresolvedBody(unresolvedBody),
		                      returnType,
		                      parameters,
		                      identifier
		    );
		return t;
	}

	private Type parseReturnType(final AstReturnType r, final Type scope, final String identifier)
		throws SemanticException {
		if (r.isEmpty()) {
			throw new RuntimeException("no type inference yet!");
		}
		else {
			String returnTypeName;
			AstNode n = (AstNode) r.jjtGetChild(0);
			if (n instanceof AstIdentifier) {
				returnTypeName = SemanticsUtils.castNode(n, AstIdentifier.class,
				                                                "(Returntype-)%s expected,"
				                                                + " but %s given!")
				    .getName();
				if (returnTypeName.equals("=")) {
					returnTypeName = identifier;
				}
			}
			else if (n instanceof AstList) {
				returnTypeName = "";
				for (AstNode c: n) {
					
					returnTypeName += SemanticsUtils.castNode(c, AstIdentifier.class,
					                                          "(partial Returntype-)%s expected,"
					                                          + " but %s given!")
					    .getName();
				}
			}
			else {
				throw new SemanticException("ReturnType expected!");
			}
			return new UnresolvedType(returnTypeName);
		}
	}


	public Object visit(AstBody body, Object data) throws SemanticException {
		throw new RuntimeException("Should never happen!");
	}

	
	public Object visit(AstRoot node, Object data) throws SemanticException {
		for (AstNode n: node) {
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
		for (AstNode n: node) {
			n.jjtAccept(this, data);
		}

		return null;
	}
	@Override public Object visit(AstParameterList node, Object data) throws SemanticException {
		throw new RuntimeException("Parameterlist should never be visited!");
	}
	@Override public Object visit(AstReturnType node, Object data) throws SemanticException {
		throw new RuntimeException("Returntype should never be visited!");
	}

}
