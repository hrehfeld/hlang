package de.haukerehfeld.hlisp;

import java.util.*;
import java.io.*;


import de.haukerehfeld.hlisp.semantics.*;

public class JavaEmitter {

	/** name seperator */
	private final static String NSEP = "_";
	/** prefix seperator */
	private final static String psep = "_";

	private final static String prefix = "_hlisp";
	private final static String escapePrefix = "escape";
	private final static String reservedPrefix = "reserved";
	private final static String nativePrefix = "native";
	private final static String run = "call";
	private final static String create = "create";
	private final static String DONTCARE = "DONTCARE";
	private final static String NATIVE = "native";
	private final static String ANONYMOUSTYPEPREFIX = "Function";
	private final static String VOIDTYPE = "VoidType";

	private final static boolean TRACE = false;

	private final static String[] templateParameters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
	private final static int MAXPARAMETERNUMBER = templateParameters.length;

	private final static String[] anonymousParameters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n" };
	
	private final static HashMap<String, String> illegal = new HashMap<String, String>() {{

			put("=", "equal");
			put("<", "smallerthan");
			put(">", "greaterthan");

			put("+", "plus");
			put("-", "minus");
			put("*", "star");
			put("/", "slash");

			put(".", "dot");
			put("?", "questionmark");
			put("!", "exclamationmark");

			put("[", "bracketopen");
			put("]", "bracketclose");
			//put("(", "paranthesisopen");
			//put(")", "paranthesisclose");
		}};

	private String autoBox(String type) {
		String result = autoBox.get(type);
		if (result == null) { result = type; }
		return result;
	}

	private final static HashMap<String, String> autoBox = new HashMap<String, String>() {{
			put("int", "java.lang.Integer");
			put("long", "java.lang.Long");
			put("float", "java.lang.Float");
			put("double", "java.lang.Double");
			put("boolean", "java.lang.Boolean");
		}};	
	private final static List<String> reserved = new ArrayList<String>() {{
			add("while");
			add("Void");
			add("void");
			add("toString");
			//add("String");
		}};

	private int indent = 0;
	IndentStringBuilder r = new IndentStringBuilder();

	private List<Type> emittedTypes = new ArrayList<Type>();
	private Set<Signature> anonymousTypes = new LinkedHashSet<Signature>();
	private Set<String> emittedInterfaces = new HashSet<String>();

	public String emitFunction(Type f) {
		emitClassStart(f);
		r.indentMore();

		emitConstructor(f);
		emitAction(f);
		emitMembers(f);

		r.indentLess();
		emitClassEnd();
		return r.toString();
	}

	public String emit(Type s) {
		if (emittedTypes.contains(s)) {
			System.out.println("Skipping emit of " + s + ", already emitted.");
			return "";
		}
		if (s instanceof SelfType) {
			System.out.println("Skipping " + s + ".");
			return "";
		}
		emittedTypes.add(s);
		
		if (s.isFunction()) {
			return emitFunction(s);
		}

		r.append("/* variable " + s.getName() + " */");
		r.append("private " + getName(s.getReturnType())
		         + " " + escapeIdentifier(s) + " = ");
		emit(s, s.getInstruction(), false, true);
		r.append("", true);
		return r.toString();
	}

	public String emit(RootType root) {
		String name = "Root";
		try {
			r.append("package de.haukerehfeld.hlisp;", true);

			r.append("import de.haukerehfeld.hlisp.java.*;", true);
			r.append("import java.io.*;", true);
			r.append("", true);
			
			emitClassStart(root);
			r.indentMore();

			emitConstructor(root);
			emitAction(root);

			String[] rootDefs = {
				"public static void main(java.lang.String[] args) {"
				,"    new Root()." + create + "(args)."
				    + run + "();"
				,"}"
			};

			
			for (String line: rootDefs) {
				r.append(line, true);
			}
			emitMembers(root);

			emitAnonymousTypes();

			r.indentLess();
			emitClassEnd();
			return r.toString();
		}
		catch (RuntimeException e) {
			System.out.println(r.toString() + "\n------------------------");
			throw e;
		}
	}


	private void emitCreateFunction(Type s) {
		String name = escapeIdentifier(s.getName());
		r.append("public " + name + " " + name);
		r.append("(");

		//r.append(Utils.join(getParameterStrings(s, " "), ", "));
		r.append(") {", true);

		r.indentMore();
		r.append("return new " + name + "(");
		//r.append(Utils.join(s.getParameterNames(), ", "));
		    r.append(");", true);
		r.indentLess();
		r.append("}", true);
	}


	private void emitClassStart(Type v) {
		r.append("/**", true);
		r.append(" * Type " + v.getName(), true);
		r.append(" */", true);

		if (v.hasName() && !(v instanceof RootType)) {
			emitCreateFunction(v);
		}

		if (v.hasName()) {
			r.append("public ");
			// if (!(s instanceof RootType)) {
			// 	r.append("static ");
			// }
			r.append("class " + escapeIdentifier(v.getName()));
			r.indentMore();
			r.append(" implements ", true);
			r.indentMore();
			if (!(v instanceof RootType)) {
				r.append(getAnonymousTypeName(v) + ",", true);
			}
			r.append("java.util.concurrent.Callable<" + getName(v.getReturnType(), false, true) + ">", true);
			r.indentLess();
			r.indentLess();
		}
		else {
			r.append("new " + getAnonymousType(v) + "()");
		}

		r.append(" {", true);
	}

	private void emitClassEnd() {
		r.append("}", true);
		r.append("", true);
	}

	private void emitConstructor(Type s) {
		emitConstructor(s, false);
	}


	private void emitConstructor(Type s, boolean _abstract) {
		if (!s.getParameterTypes().isEmpty()) {
			if (!_abstract) {
				List<String> params = new ArrayList<String>();
				List<String> paramNames = s.getParameterNames();
				for (int i = 0; i < s.getParameterTypes().size(); ++i) {
					Signature t = s.getParameterTypes().get(i);
					String type = getName(t, false, false);
					String name = paramNames.get(i);
					r.append("private " + type + " " + escapeIdentifier(name) + ";", true);
				}
				r.append("", true);
			}
		}
		
		for (int i = s.getParameterTypes().size(); i <= s.getParameterTypes().size(); ++i) {
			boolean last = i == s.getParameterTypes().size();
			Signature ret = new AnonymousSignature(s.getReturnType(),
			                                       (!last || s.getReturnType()
			                                        .isFunction()),
			                                       s.getParameterTypes().subList(0, i));
			String retName = last ? getName(s) : getAnonymousType(ret);
			// if (!(s instanceof RootType)) {
			// 	r.append("@Override ");
			// }
			r.append("public " + autoBox(retName) + " "
			         + create + "(");
			List<String> params = new ArrayList<String>();
			for (int j = 0; j < s.getParameterTypes().size(); ++j) {
				String p = autoBox(getName(s.getParameterTypes().get(j)))
				    + " " + s.getParameterNames().get(j);
				params.add(p);
				
			}
			r.append(Utils.join(params.subList(0, i), ", "));
			r.append(")");
			if (_abstract) {
				r.append(";", true);
			}
			else {
				r.append(" {", true);


				r.indentMore();
				if (TRACE) {
					r.append("System.out.println(\"Calling constructor on " + s + "\");", true);
				}

				List<String> activeParameterNames = s.getParameterNames().subList(0, i);
				for (String p: activeParameterNames) {
					String e = escapeIdentifier(p);
					r.append("this." + e + " = " + e + ";", true);
				}

				if (!last) {
					r.append(retName + "." + create + "("
					         + Utils.join(activeParameterNames, ", ") + ");", true);
				}
				r.append("return this;", true);

				r.indentLess();
				r.append("}", true);
			}
		}
		
		r.append("", true);
	}

	private void emitMembers(Type v) {
		for (Type member: v.getDefinedTypes()) {
			emit(member);
		}
	}

	private void emit(Type scope, Instruction instr) {
		emit(scope, instr, true, true);
	}
	private void emit(Type scope,Instruction instr, boolean lastStatement, boolean completeStatement) {
		emit(scope, instr, lastStatement, completeStatement, new ArrayList<Instruction>());
	}
	
	private void emit(Type scope, Instruction instr,
	                  boolean lastStatement, boolean completeStatement,
	                  List<Instruction> checkedInstructions) {
		//r.append("/* " + instr + " */", completeStatement);
		if (instr instanceof ListInstruction) {
			ListInstruction l = (ListInstruction) instr;
			for (int i = 0; i < l.getInstructions().size(); ++i) {
				Instruction child = l.getInstructions().get(i);
				boolean last = lastStatement && i >= l.getInstructions().size() - 1;
				emit(scope, child, last, completeStatement, checkedInstructions);
				r.append("", true);
			}
		}
		else {
			if (instr instanceof VoidInstruction) {
				if (lastStatement) {
					r.append("return ");
				}
				r.append("null");
				if (completeStatement) {
					r.append(";", true);
				}
			}
			else if (instr instanceof NativeInstruction) {
				NativeInstruction n = (NativeInstruction) instr;
				String[] lines = n.getNativeCode().split("\n");

				boolean returnVoid = false;
				if (lastStatement
				    && lines.length <= 1) {
					if (!(n.getReturnType() instanceof VoidType)) {
						r.append("return ");
					}
					else if (completeStatement) {
						returnVoid = true;
					}
				}

				//emit last line with no newline
				int i = 0;
				for (String l: lines) {
					boolean newline = i < lines.length - 1;
					r.append(l, newline);
					++i;
				}
				if (returnVoid) {
					r.append("return null;", true);
				}
			}
			else if (instr instanceof FunctionCallInstruction) {
				FunctionCallInstruction n = (FunctionCallInstruction) instr;

				Type fun = n.getFunction();
				if (n.equals(scope)) {
					return;
				}
				
				if (fun instanceof UnresolvedType) {
					fun = ((UnresolvedType) fun).getResolved();
				}
				if (fun instanceof VoidType) {
					emit(scope, new VoidInstruction(), lastStatement, completeStatement, checkedInstructions);
					return;
				}

				if (lastStatement) {
					r.append("return ");
				}
				
				if (fun instanceof SelfType) {
					if (!fun.equals(scope)) {
						r.append(escapeIdentifier(fun.getName()) + ".");
					}
					r.append("this");
				}
				else {
					Instruction in = n.getScope();
					if (in != null
					    && !in.equals(n)) {
						checkedInstructions.add(n);
						emit(scope, in, false, false, checkedInstructions);
						r.append(".");
					}
					
					if (fun.isFunction()) {
						r.append(escapeIdentifier(fun.getName()) + "()");					
						r.append("." + create + "(");
						int i = n.getParameters().size() - 1;
						for (Instruction param: n.getParameters()) {
							emit(scope, param, false, false);
							if (i > 0) {
								r.append(", ");
								i--;
							}
						}
						r.append(")." + run + "()");
					}
					else {
						r.append(escapeIdentifier(fun.getName()));
					}
				}
				if (completeStatement) {
					r.append(";", true);
				}
			}
			else if (instr instanceof LambdaInstruction) {
				LambdaInstruction linstr = (LambdaInstruction) instr;
				Type lambda = linstr.getFunction();

				if (lastStatement) {
					r.append("return ");
				}

				emit(lambda);
				
				if (completeStatement) {
					r.append(";", true);
				}
				else {
					r.append("", true);
				}
			}
		}
	}

	private void emitAction(Type f) {
		emitAction(f, false);
	}
	private void emitAction(Type f, boolean _abstract) {
		// if (!(f instanceof RootType)) {
		// 	r.append("@Override ");
		// }
		r.append("@Override public " + autoBox(getName(f.getReturnType()))
		         + " " + run + "(");
		r.append(")");
		if (_abstract) { r.append(";", true); }
		else {
			r.append(" {", true);
			
			r.indentMore();
			{
				if (TRACE) {
					r.append("System.out.println(\"Calling " + run + " on " + f + "\");", true);
				}

				emit(f, f.getInstruction());
			}
			r.indentLess();
			
			r.append("}", true);
			r.append("", true);
		}
	}

	private String escapeIdentifier(Signature id) {
		return escapeIdentifier(id.getName());
	}

	
	private String escapeIdentifier(String id) {
		return escapeIdentifier(id, false);
	}

	private String escapeIdentifier(String id, boolean partial) {
		int lastPos = 0;
		String escaped = id;
		//replace illegal chars with their text representation
		for (Map.Entry<String, String> e: illegal.entrySet()) {
			escaped = escaped.replace(e.getKey(), e.getValue());
		}

		
		boolean changed = false;
		if (!escaped.equals(id)) {
			escaped = escapePrefix + psep + escaped;
			changed = true;
		}

		for (String r: reserved) {
			if (escaped.equals(r)) {
				escaped = reservedPrefix + psep + escaped;
				changed = true;
			}
		}
		if (!partial && changed) {
			escaped = prefix + psep + escaped;
		}
		return escaped;
	}

	private String getAnonymousTypeName(Signature v) {
		StringBuilder name = new StringBuilder();
		name.append(ANONYMOUSTYPEPREFIX + v.getParameterTypes().size());
		boolean noGenerics = false;
		for (Signature t: v.getParameterTypes()) {
			if (t instanceof DontCareSignature) { noGenerics = true; }
		}

		if (!noGenerics) {
			name.append("<");
			for (int i = 0; i < v.getParameterTypes().size(); ++i) {
				name.append(getName(v.getParameterTypes().get(i), false, true) + ", ");
			}
			name.append(getName(v.getReturnType(), false, true) + ">");
		}
		return name.toString();
	}

private void emitAnonymousSignature(Signature t) {
			// String name = getAnonymousTypeName(t);
			// if (emittedInterfaces.contains(name)) {
			// 	return;
			// }
			// emittedInterfaces.add(name);
			// r.append("interface " + name);

			// Signature dontcare = null;
			// if (extendDontcare) {
			// 	dontcare = new AnonymousSignature(new DontCareSignature(),
			// 	                                            t.isFunction(),
			// 	                                            Collections.<Signature>nCopies(t.getParameterTypes().size(),
			// 	                                                                           new DontCareSignature()));
			// 	r.append(" extends "
			// 	         + getAnonymousTypeName(dontcare));
			// }			         
			                                                  
			
			// r.append(" {", true);
			// r.indentMore();

			// List<String> params = new ArrayList<String>();
			// for (int i = 0; i < t.getParameterTypes().size(); ++i) {
			// 	params.add(anonymousParameters[i]);
			// }
			// //r.append("/* " + t + "*/", true);
			// Type type = new NamedType(name, null, t, params);
			// emitConstructor(type, true);
			// emitAction(type, true);
			
			// r.indentLess();
			// r.append("}", true);
			// r.append("", true);

			// if (extendDontcare) {
			// 	emitAnonymousSignature(dontcare, false);
			// }

}

	private void emitAnonymousTypes() {
		for (Signature t: anonymousTypes) {
			emitAnonymousSignature(t);
		}

		for (int i = 0; i < MAXPARAMETERNUMBER; ++i) {
			r.append("public interface " + ANONYMOUSTYPEPREFIX + i
			         + "<");
			for (int j = 0; j < i; ++j) {
				r.append(templateParameters[j] + ", ");
			}
			r.append("RETURN");
			r.append(">  {", true);

			r.indentMore();
			r.append("public " + ANONYMOUSTYPEPREFIX + i + " " + create + "(");
			for (int j = 0; j < i; ++j) {
				r.append(templateParameters[j] + " " + templateParameters[j]);
				if (!(j == i - 1)) {
					r.append(", ");
				}
			}
			r.append(");", true);
			r.append("public RETURN call();", true);
			r.indentLess();
			r.append("}", true);
			
		}
	}

	private String getAnonymousType(Signature t) {
		anonymousTypes.add(t);
		return getAnonymousTypeName(t);
	}
	private String getName(Signature t) {
		return getName(t, false, false);
	}

	private String getName(Signature t, boolean partialName, boolean asTypeParameter) {
		if (t instanceof UnresolvedSignature) {
			t = ((UnresolvedSignature) t).getResolved();
		}

		if (t instanceof DontCareSignature) {
			return partialName ? prefix + psep + DONTCARE :
			    asTypeParameter ? "java.lang.Object" : "java.lang.Object";
		}
		else if (t instanceof NativeSignature) {
			String n = ((NativeSignature) t).getName();
			return partialName ?
			    (NATIVE + psep + escapeIdentifier(n, partialName))
			    : asTypeParameter ? autoBox(n) : n;
		}
		else if (!t.hasName()) {
			return getAnonymousType(t);
		}
		else {
			Type type = (Type) t;
			List<String> names = new ArrayList<String>();
			Type parent = type;
			while (parent != null) {
				String name = partialName?
				    parent.getName():
				    escapeIdentifier(parent.getName(), partialName);
				names.add(name);
				parent = parent.getParent();
			}

			Collections.reverse(names);
			String n = Utils.join(names, (partialName ? NSEP : "."));

			return n;
		}
	}

	public List<String> getParameterStrings(Type v, String join) {
		List<String> params = new ArrayList<String>();
		int i = 0;
		List<String> paramNames = v.getParameterNames();
		for (Signature t: v.getParameterTypes()) {
			String p = paramNames.get(i);
			String name = getName(t);
			params.add(name + join + escapeIdentifier(p));
			++i;
		}
		return params;
	}


	
}