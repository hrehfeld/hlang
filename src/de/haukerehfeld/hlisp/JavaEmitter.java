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
	private final static String run = "run";
	private final static String create = "create";
	private final static String DONTCARE = "DONTCARE";
	private final static String NATIVE = "native";
	private final static String ANONYMOUSTYPEPREFIX = "type";
	private final static String VOIDTYPE = "VoidType";

	private final static boolean TRACE = false;


	private final static String[] templateParameters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n" };
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
				,"    new Root()." + prefix + psep + create + "(args)."
				    + prefix + psep + run + "();"
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

		if (!(v instanceof RootType)) {
			emitCreateFunction(v);
		}

		r.append("public ");
		// if (!(s instanceof RootType)) {
		// 	r.append("static ");
		// }
		r.append("class " + escapeIdentifier(v.getName()));
		
		if (!(v instanceof RootType)) {
			r.append(" implements " + getAnonymousType(v));
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
					String type = getName(t, false);
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
			r.append("public " + retName + " "
			         + prefix + psep + create + "(");
			List<String> params = getParameterStrings(s, " ");
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
					r.append(retName + "." + prefix + psep + create + "("
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
						r.append("." + prefix + psep + create + "(");
						int i = n.getParameters().size() - 1;
						for (Instruction param: n.getParameters()) {
							emit(scope, param, false, false);
							if (i > 0) {
								r.append(", ");
								i--;
							}
						}
						r.append(")." + prefix + psep + run + "()");
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
				String className = getAnonymousType(lambda);

				if (lastStatement) {
					r.append("return ");
				}

				r.append("new " + className + "()");
				         

				//r.append("." + prefix + psep + create + "()");
				r.append(" {", true);
				r.indentMore();

				emitConstructor(lambda);
				
				r.append("@Override public " + getName(instr.getReturnType().getReturnType())
				         + " " + prefix + psep + run + "() {", true);
				r.indentMore();
				emit(scope, lambda.getInstruction(), true, true);
				r.indentLess();
				r.append("}", true);
				r.indentLess();
				r.append("}");
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
		r.append("public " + getName(f.getReturnType())
		         + " " + prefix + psep + run + "(");
		r.append(")");
		if (_abstract) { r.append(";", true); }
		else {
			r.append(" {", true);
			
			r.indentMore();
			{

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

	private String getAnonymousTypeName(Signature t) {
		StringBuilder name = new StringBuilder();
		name.append(prefix + psep + ANONYMOUSTYPEPREFIX);
		if (t.isFunction()) { name.append(psep + "function"); }

		List<String> params = new ArrayList<String>();
		for (Signature s: t.getParameterTypes()) {
			params.add(getName(s, true));
		}
		if (!params.isEmpty()) {
			name.append(psep + Utils.join(params, psep));
		}
		name.append(psep + getName(t.getReturnType(), true));
		return name.toString();
	}

	private void emitAnonymousTypes() {
		Set<String> emittedInterfaces = new HashSet<String>();
		for (Signature t: anonymousTypes) {
			String name = getAnonymousTypeName(t);
			if (emittedInterfaces.contains(name)) {
				continue;
			}
			r.append("interface " + name);
			emittedInterfaces.add(name);

			r.append(" {", true);
			r.indentMore();

			List<String> params = new ArrayList<String>();
			for (int i = 0; i < t.getParameterTypes().size(); ++i) {
				params.add(anonymousParameters[i]);
			}
			//r.append("/* " + t + "*/", true);
			Type type = new NamedType(name, null, t, params);
			emitConstructor(type, true);
			emitAction(type, true);
			
			r.indentLess();
			r.append("}", true);
			r.append("", true);
			
		}
	}

	private String getAnonymousType(Signature t) {
		anonymousTypes.add(t);
		return getAnonymousTypeName(t);
	}
	private String getName(Signature t) {
		return getName(t, false);
	}

	private String getName(Signature t, boolean partialName) {
		if (t instanceof UnresolvedSignature) {
			t = ((UnresolvedSignature) t).getResolved();
		}

		if (t instanceof DontCareSignature) {
			return partialName ? prefix + psep + DONTCARE : "java.lang.Object";
		}
		else if (t instanceof NativeSignature) {
			String n = ((NativeSignature) t).getName();
			return partialName ?
			    (NATIVE + psep + escapeIdentifier(n, partialName))
			    : n;
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