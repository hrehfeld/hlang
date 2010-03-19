package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
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
	public Object visit(AstDefine node, Type scope) throws SemanticException {
		Iterator<AstNode> children = node.iterator();
		
		String identifier = parseName(children.next());

		if (scope.isTypeDefined(identifier)) {
			String e = "Type " + identifier + " already defined in " + scope + ".";
			throw new SemanticException(e);
		}


		AstNode b = children.next();
		if (!(b instanceof AstVariable || b instanceof AstLambdaExpression)) {
			//should hopefully never happen
			throw new SemanticException(SemanticsUtils.errorLocation(b)
			                            + "Define body isn't Variable or Lambda expression: " + b);
		}


		Type type; 
		if (b instanceof AstLambdaExpression) {
			type = parseLambda((AstLambdaExpression) b, identifier, scope);
		}
		else {
			Type body = (Type) b.jjtAccept(this, scope);

			type = new NamedType(identifier,
			                     scope,
			                     body.getReturnType(),
			                     body.isFunction(),
			                     body.getParameterTypes(), body.getParameterNames());
			for (Type t: body.getDefinedTypes()) {
				//System.out.println("copying " + t + " into " + type);
				if (t instanceof SelfType) {
					continue;
				}
				type.defineType(t);
				t.setParent(type);
			}
			type.setInstruction(body.getInstruction());
		}
		System.out.println("defining " + type + " in " + scope);
		scope.defineType(type);

		return null;
	}

	public String parseName(final AstNode n) throws SemanticException {
		return (String) SemanticsUtils.castNode(n, AstIdentifier.class, "%s expected, but %s given!")
		    .jjtGetValue();
	}

	public Instruction visit(AstBody body, Type scope) throws SemanticException {
		return parseList(body, scope);
	}

	
	public Object visit(AstRoot node, Type scope) throws SemanticException {
		for (AstNode n: node) {
			n.jjtAccept(this, scope);
		}
		return null;
	}
	
	public Object visit(SimpleNode node, Type scope)  throws SemanticException {
		throw new SemanticException("Simplenode:" + node.getClass());
	}
	public UnresolvedInstruction visit(AstIdentifier node, Type scope)  throws SemanticException {
		return new UnresolvedInstruction(node.jjtGetValue());
	}

	private FunctionCallInstruction constructor(final String type, final String value) {
		List<Instruction> v = new ArrayList<Instruction>() {{
				add(new NativeInstruction(new UnresolvedSignature(type), value));
			}};
		return new FunctionCallInstruction(new UnresolvedType(type), v);
	}

	public FunctionCallInstruction visit(AstString node, Type scope) throws SemanticException {
		return constructor("String", node.jjtGetValue());
	}

	public FunctionCallInstruction visit(AstFloat node, Type scope) throws SemanticException {
		return constructor("Float", Float.toString(Float.parseFloat((String) node.jjtGetValue())));
	}

	public FunctionCallInstruction visit(AstInteger node, Type scope) throws SemanticException {
		return constructor("Int", Integer.toString(Integer.parseInt((String) node.jjtGetValue())));
	}

	private Instruction parseList(AstNode node, Type scope) throws SemanticException {
		List<Instruction> instructions = new ArrayList<Instruction>();
		for (AstNode n: node) {
			Object result = n.jjtAccept(this, scope);
			if (result instanceof Instruction) {
				instructions.add((Instruction) result);
			}
			//defines return null
			else if (result == null) {
			}
			else if (result instanceof Type && !((Type) result).hasName()) {
				instructions.add(new LambdaInstruction((Type) result));
			}
			else {
				throw new SemanticException("Instruction expected, " + result + " given, from node "
				                            + n + ".");
			}
		}
		if (instructions.size() == 1) {
			return instructions.get(0);
		}
		else if (instructions.isEmpty()) {
			return new VoidInstruction();
		}
		else {
			return new ListInstruction(instructions);
		}
	}

	/** List */
	public Instruction visit(AstList node, Type scope) throws SemanticException {
		return parseList(node, scope);
	}

	@Override public NativeInstruction visit(AstNativeCodeBlock node, Type scope) throws
		SemanticException {
		Signature t = (Signature) node.jjtGetChild(0).jjtAccept(this, scope);
		
		return new NativeInstruction(t, (String) node.jjtGetValue());
	}

	@Override public NativeSignature visit(AstNativeType node, Type scope) throws SemanticException {
		return new NativeSignature((String) node.jjtGetValue());
	}

	private Type parseLambda(AstLambdaExpression b, String identifier, Type scope) throws
		SemanticException {
		Iterator<AstNode> children = b.iterator();
		AstNode typeNode = children.next();
		AnonymousSignature lambdaSignature = (AnonymousSignature) typeNode.jjtAccept(this, scope);

		AstNode paramNamesNode = children.next();
		List<String> parameterNames = (List<String>) paramNamesNode.jjtAccept(this, scope);

		if (parameterNames.size() != lambdaSignature.getParameterTypes().size()) {
			String e = lambdaSignature.getParameterTypes().size() + " Parameter types ("
			    + Utils.join(lambdaSignature.getParameterTypes(), ", ") + "), but "
			    + parameterNames.size() + " parameter identifiers ("
			    + Utils.join(parameterNames, ", ") + ") given.";
			throw new SemanticException(SemanticsUtils.errorLocation(paramNamesNode) + e);
		}


		Type lambdaType;
		if (identifier == null) {
			lambdaType = new AnonymousType(scope, lambdaSignature, parameterNames);
		}
		else {
			lambdaType = new NamedType(identifier, scope, lambdaSignature, parameterNames);
		}
			
		Instruction instr = (Instruction) children.next().jjtAccept(this, lambdaType);
		System.out.println("Defining instruction " + instr + " in " + lambdaType);
		lambdaType.setInstruction(instr);
		return lambdaType;
	}

	@Override public Type visit(AstLambdaExpression b, Type scope) throws
		SemanticException {
		return parseLambda(b, null, scope);
	}

	@Override public List<String> visit(AstFunctionParameters parameters, Type scope) throws SemanticException {
		List<String> parameterNames = new ArrayList<String>();
		for (AstNode node: parameters) {
			if (node instanceof AstFunctionSymbol) {
				break;
			}
			parameterNames.add(parseName(node));
		}
		return parameterNames;
	}


	@Override public Type visit(AstVariable node, Type scope) throws SemanticException {
		Iterator<AstNode> it = node.iterator();
		Signature type = (Signature) it.next().jjtAccept(this, scope);
		Instruction r = (Instruction) it.next().jjtAccept(this, scope);

		Type var = new AnonymousType(scope, type, false);
		var.setInstruction(r);
		return var;
	}

	@Override public Object visit(AstFunctionSymbol node, Type scope) throws SemanticException {
		throw new RuntimeException(node.getClass().getSimpleName() + " should never be visited!");
	}

	@Override public Signature visit(AstType node, Type scope) throws SemanticException {
		return (Signature) node.jjtGetChild(0).jjtAccept(this, scope);
	}
	@Override public Signature visit(AstSimpleType node, Type scope) throws SemanticException {
		AstNode type = node.iterator().next();
		if (type instanceof AstNativeType) {
			return (Signature)type.jjtAccept(this, scope);
		}
		else if (type instanceof AstIdentifier) {
			return new UnresolvedSignature(parseName(type));
		}
		throw new SemanticException(SemanticsUtils.errorLocation(type)
		                            + " NativeBlock or Identifier expected.");
	}
	@Override public AnonymousSignature visit(AstFunctionType typeNode, Type scope) throws
		SemanticException {
		List<Signature> types = new ArrayList<Signature>();
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
				types.add((Signature) tN.jjtAccept(this, scope));

				if (justEncounteredFunctionSymbol) {
					returnTypeSeen = true;
				}

				justEncounteredFunctionSymbol = false;
			}
		}
		final Signature returnType;
		// void returntype omitted
		if (!returnTypeSeen) {
			returnType = VoidType.create();
		}
		else {
			int last = types.size() - 1;
			returnType = types.remove(last);
		}
		
		return new AnonymousSignature(returnType, true, types);
	}
	@Override public UnresolvedSignature visit(AstQualifiedType node, Type scope) throws
		SemanticException {
		List<String> types = new ArrayList<String>();

		for (AstNode part: node) {
			
			types.add(parseName(part));
		}
		return new UnresolvedSignature(types);
	}
	
	
}
