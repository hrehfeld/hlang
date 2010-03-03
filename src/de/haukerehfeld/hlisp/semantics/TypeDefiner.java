package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

/**
 * Walk the AST and do nothing but defining types
 */
public class TypeDefiner implements HLispParserVisitor {
	private final Deque<Value> structs = new ArrayDeque<Value>();

	public TypeDefiner(Value root) {
		structs.add(root);
	}

	/**
	 * Define a Type
	 */
	public Object visit(AstDefine node, Value scope) throws SemanticException {
		Iterator<AstNode> children = node.iterator();
		
		String identifier = parseName(children.next());

		if (scope.getType().isTypeDefined(identifier)) {
			String e = "Type " + identifier + " already defined in " + scope.getType() + ".";
			throw new SemanticException(e);
		}
		if (scope.isMemberDefined(identifier)) {
			String e = "Member " + identifier + " already defined in " + scope + ".";
			throw new SemanticException(e);
		}

		AstNode b = children.next();
		if (!(b instanceof AstVariable || b instanceof AstLambdaExpression)) {
			//should hopefully never happen
			throw new SemanticException(SemanticsUtils.errorLocation(b)
			                            + "Define body isn't Variable or Lambda expression: " + b);
		}

		Value body = (Value) b.jjtAccept(this, scope);
		
		if (scope.isMemberDefined(identifier)) {
			String e = "Member " + identifier + " already defined in " + scope + ".";
			throw new SemanticException(e);
		}
		System.out.println("defining new member " + identifier + " = " + body);
		scope.defineMember(identifier, body);

		return null;
	}

	// private Value getMember(Value scope, String identifier) {
	// 	Value member =  scope.
	// }

	// private Value parseCallChain(AstList list, Value scope) {
	// 	Value lastScope = scope;
	// 	List<Value> lastValues = new Arra
	// 	for (AstNode id: list) {
	// 		if (id instanceof AstIdentifier) {
	// 			final String identifier = parseName(id);

	// 			Value member = getMember(lastScope, identifier);
	// 			if (member != null) {
					
	// 			}

	// 			return new InstanceValue(scope, new UnresolvedType(identifier));
	// 		}
	// 	}
	// 	EvaluateValue l = new EvaluateValue(null, scope);
	// 		l.finish();
	// 		return l;
	// }


	public String parseName(final AstNode n) throws SemanticException {
		return (String) SemanticsUtils.castNode(n, AstIdentifier.class, "%s expected, but %s given!")
		    .jjtGetValue();
	}

	public List<Value> visit(AstBody body, Value scope) throws SemanticException {
		List<Value> values = new ArrayList<Value>();
		for (AstNode n: body) {
			Object result = n.jjtAccept(this, scope);
			if (result instanceof Value) {
				values.add((Value) result);
			}
		}
		return values;
	}

	
	public Object visit(AstRoot node, Value scope) throws SemanticException {
		for (AstNode n: node) {
			n.jjtAccept(this, scope);
		}
		return null;
	}
	
	public Object visit(SimpleNode node, Value scope)  throws SemanticException {
		throw new SemanticException("Simplenode:" + node.getClass());
	}
	public Object visit(AstIdentifier node, Value scope)  throws SemanticException {
		return new UnresolvedIdentifierValue(scope, (String) node.jjtGetValue());
	}
	
	public Object visit(AstString node, Value scope) throws SemanticException {
		return new StringValue(scope, (String) node.jjtGetValue());
	}

	public Object visit(AstFloat node, Value scope) throws SemanticException {
		return new FloatValue(scope, Float.parseFloat((String) node.jjtGetValue()));

	}

	public Object visit(AstInteger node, Value scope) throws SemanticException {
		return new IntValue(scope, Integer.parseInt((String) node.jjtGetValue()));
	}

	/** List */
	public Object visit(AstList node, Value scope) throws SemanticException {
		//empty list
		if (node.isEmpty()) {
			return new InstanceValue(scope, new UnresolvedType("List"));
		}
		
		EvaluateValue l = new EvaluateValue(null, scope);
		for (AstNode n: node) {
			l.add((Value) n.jjtAccept(this, scope));
		}
		//l.finish();
		return l;
	}
	@Override public Object visit(AstNativeCodeBlock node, Value scope) throws SemanticException {
		return new NativeValue(scope, VoidType.create(),(String) node.jjtGetValue());
	}

	@Override public Object visit(AstLambdaExpression b, Value scope) throws SemanticException {
		Iterator<AstNode> children = b.iterator();
		AstNode typeNode = children.next();
		Type lambdaType = (Type) typeNode.jjtAccept(this, scope);
		Type returnType;
		List<Type> parameterTypes;
		boolean function;
		if (!(lambdaType instanceof UnresolvedType) && lambdaType.isFunction()) {
			function = true;
			returnType = lambdaType.getReturnType();
			parameterTypes = lambdaType.getParameterTypes();
		}
		else {
			function = false;
			returnType = lambdaType;
			parameterTypes = Collections.<Type>emptyList();
		}

		AstNode paramNamesNode = children.next();
		List<String> parameterNames = (List<String>) paramNamesNode.jjtAccept(this, scope);

		if (!parameterTypes.isEmpty()) {
			Type first = parameterTypes.get(0);
			if (first instanceof VoidType || first.equals(VoidType.create())) {
				parameterTypes.remove(0);
			}
		}
		
		if (parameterNames.size() != parameterTypes.size()) {
			String e = parameterTypes.size() + " Parameter types ("
			    + Utils.join(parameterTypes, ", ") + "), but "
			    + parameterNames.size() + " parameter identifiers ("
			    + Utils.join(parameterNames, ", ") + ") given.";
			throw new SemanticException(SemanticsUtils.errorLocation(paramNamesNode) + e);
		}

		AnonymousType type = new AnonymousType(scope.getType(), returnType, false, parameterTypes);
		EvaluateValue eval;
		if (function) {
			eval = new LambdaFunction(type, scope, parameterNames);
			type.setIsFunction(true);
		}
		else {
			eval = new EvaluateValue(type, scope);
		}
		List<Value> values = (List<Value>) children.next().jjtAccept(this, eval);
		eval.setValues(values);
		//eval.finish();
		return eval;
	}

	@Override public List<String> visit(AstFunctionParameters parameters, Value scope) throws SemanticException {
		List<String> parameterNames = new ArrayList<String>();
		for (AstNode node: parameters) {
			if (node instanceof AstFunctionSymbol) {
				break;
			}
			parameterNames.add(parseName(node));
		}
		return parameterNames;
	}


	@Override public Object visit(AstVariable node, Value scope) throws SemanticException {
		Iterator<AstNode> it = node.iterator();
		Type type = (Type) it.next().jjtAccept(this, scope);
		List<Value> value = (List<Value>) it.next().jjtAccept(this, scope);
		if (value.size() != 1) {
			throw new SemanticException(SemanticsUtils.errorLocation(node)
			                            + "Variable value must be a single expression.");
		}
		Value r = value.get(0);
		if (r instanceof EvaluateValue) {
			((EvaluateValue)r).setType(type);
		}
		return r;
	}

	@Override public Object visit(AstFunctionSymbol node, Value scope) throws SemanticException {
		throw new RuntimeException(node.getClass().getSimpleName() + " should never be visited!");
	}

	@Override public Type visit(AstType node, Value scope) throws SemanticException {
		return (Type) node.jjtGetChild(0).jjtAccept(this, scope);
	}
	@Override public Object visit(AstSimpleType node, Value scope) throws SemanticException {
		AstNode type = node.iterator().next();
		if (type instanceof AstNativeCodeBlock) {
			return new NativeType((String) type.jjtGetValue());
		}
		else if (type instanceof AstIdentifier) {
			return new UnresolvedType(parseName(type));
		}
		throw new SemanticException(SemanticsUtils.errorLocation(type)
		                            + " NativeBlock or Identifier expected.");
	}
	@Override public Object visit(AstFunctionType typeNode, Value scope) throws SemanticException {
		List<Type> types = new ArrayList<Type>();
		boolean returnTypeSeen = false;
		{
			boolean justEncounteredFunctionSymbol = false;
			for (AstNode tN: typeNode) {
				if (returnTypeSeen) {
					throw new SemanticException(SemanticsUtils.errorLocation(tN)
					                            + "More than one return type given.");
				}

				if (tN instanceof AstFunctionSymbol) {
					justEncounteredFunctionSymbol = true;
					continue;
				}
				Type t = (Type) tN.jjtAccept(this, scope);
				types.add(t);

				if (justEncounteredFunctionSymbol) {
					returnTypeSeen = true;
				}

				justEncounteredFunctionSymbol = false;
			}
		}
		final Type returnType;
		// void returntype omitted
		if (!returnTypeSeen) {
			returnType = VoidType.create();
		}
		else {
			int last = types.size() - 1;
			returnType = types.remove(last);
		}
		
		return new AnonymousType(scope.getType(), returnType, true, types);
	}
	@Override public Object visit(AstQualifiedType node, Value scope) throws SemanticException {
		String returnTypeName = "";
		for (AstNode part: node) {
			
			returnTypeName += parseName(part);
		}
		return new UnresolvedType(returnTypeName);
	}
	
	
}
