package de.haukerehfeld.hlisp;

import java.util.*;
import java.io.*;


import de.haukerehfeld.hlisp.semantics.*;

public class JavaEmitter {

	private final static String prefix = "_hlisp";
	private final static String escapePrefix = "_escape";
	private final static String reservedPrefix = "_reserved";
	private final static String nativePrefix = "_native";
	private final static String run = "_run";
	private final static String ANONYMOUSTYPEPREFIX = "type";
	private final static String VOIDTYPE = "VoidType";

	private final static String[] templateParameters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n" };
	
	private final static HashMap<String, String> illegal = new HashMap<String, String>() {{
			put("+", "plus");
			put("-", "minus");
			put("<", "smallerthan");
			put(">", "greaterthan");
			put("=", "equal");
			put("*", "star");
			put("/", "slash");
			put("!", "exclamationmark");
			//put("(", "paranthesisopen");
			//put(")", "paranthesisclose");
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
		r.append("private " + escapeIdentifier(getName(s.getReturnType()))
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

			List<String> rootDefs = null;
			File f = new File("../native/java/Root.java");
			try {
				rootDefs = Utils.getLines(f);
			}
			catch (FileNotFoundException e) {
				System.err.println("Couldn't load RootType Definitions from " + f);
			}

			
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

		r.append(Utils.join(getParameterStrings(s, " "), ", "));
		r.append(") {", true);

		r.indentMore();
		r.append("return new " + name + "(");
		r.append(Utils.join(s.getParameterNames(), ", "));
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
			r.append(" implements Function<");
			r.append(getName(v.getReturnType()));
			r.append(">");
		}
		r.append(" {", true);
	}

	private void emitClassEnd() {
		r.append("}", true);
		r.append("", true);
	}

	private void emitConstructor(Type s) {
		if (!s.getParameterTypes().isEmpty()) {
			List<String> paramNames = s.getParameterNames();
			for (String p: getParameterStrings(s, " ")) { r.append("private " + p + ";", true); }
			r.append("", true);

			r.append("public " + getName(s) + "(");
			r.append(Utils.join(getParameterStrings(s, " "), ", "));
			r.append(")");
			r.append(" {", true);
			r.indentMore();
			for (String p: s.getParameterNames()) {
				String e = escapeIdentifier(p);
				r.append("this." + e + " = " + e + ";", true);
			}
			r.indentLess();
			r.append("}", true);
			r.append("", true);
		}
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
		r.append("/* " + instr + " */", completeStatement);
		if (instr instanceof ListInstruction) {
			ListInstruction l = (ListInstruction) instr;
			for (int i = 0; i < l.getInstructions().size(); ++i) {
				Instruction child = l.getInstructions().get(i);
				boolean last = lastStatement && i >= l.getInstructions().size() - 1;
				emit(scope, child, last, completeStatement, checkedInstructions);
			}
		}
		else {
			if (instr instanceof VoidInstruction) {
				if (lastStatement) {
					r.append("return ");
				}
				r.append("null", true);
				if (completeStatement) {
					r.append(";", true);
				}
			}
			else if (instr instanceof NativeInstruction) {
				NativeInstruction n = (NativeInstruction) instr;
				String[] lines = n.getNativeCode().split("\n");

				for (String l: lines) {
					r.append(l, true);
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
					r.append(escapeIdentifier(fun.getName()));
					r.append(".this");
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
						r.append(escapeIdentifier(fun.getName()));					
						r.append("(");
						for (Instruction param: n.getParameters()) {
							emit(scope, param, false, false);
						}
						r.append(")._hlisp_run()");
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
				if (lastStatement) {
					r.append("return ");
				}
				r.append("new " + getName(instr.getReturnType()) + "() {", true);
				r.indentMore();
				r.append("@Override public " + getName(instr.getReturnType().getReturnType())
				         + " " + prefix + run + "() {", true);
				r.indentMore();
				emit(scope, ((LambdaInstruction)instr).getFunction().getInstruction(), false, true);
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
		r.append("public " + getName(f.getReturnType())
		         + " " + prefix + run + "() {", true);
		r.indentMore();
		emit(f, f.getInstruction());
		r.indentLess();
		r.append("}", true);
		r.append("", true);
	}

	private String escapeIdentifier(Signature id) {
		return escapeIdentifier(id.getName());
	}

	private String escapeIdentifier(String id) {
		int lastPos = 0;
		String escaped = id;
		for (Map.Entry<String, String> e: illegal.entrySet()) {
			escaped = escaped.replace(e.getKey(), e.getValue());
		}
		boolean changed = false;
		if (!escaped.equals(id)) {
			escaped = escapePrefix + "_" + escaped;
			changed = true;
		}

		for (String r: reserved) {
			if (escaped.equals(r)) {
				escaped = reservedPrefix + "_" + escaped;
				changed = true;
			}
		}
		if (changed) {
			escaped = prefix + escaped;
		}
		return escaped;
	}

	private void emitAnonymousTypes() {
		// for (Signature t: anonymousTypes) {
		// 	r.append("interface Function_" + t.getParameterTypes().size() + "<");
		// 	int i = 0;
		// 	for (Signature p: t.getParameterTypes()) {
		// 		r.append(templateParameters[i] + ", ");
		// 		i++;
		// 	}
		// 	r.append("RETURNTYPE");
		// 	r.append("> {", true);
		// 	r.append("}", true);
			
		// }
	}

	private String getAnonymousType(Signature t) {
		StringBuilder name = new StringBuilder();
		name.append("/* " + t + " */");
		if (t.isFunction()) {
			name.append("Function<");
		}
		// name.append(prefix + "_" + ANONYMOUSTYPEPREFIX + "_");
		// if (t.isFunction()) {
		// 	name.append("function_");
		// 	if (t.getParameterTypes().isEmpty()) {
		// 		name.append(VOIDTYPE + "_");
		// 	}
		// 	else {
		// 		for (Type p: t.getParameterTypes()) {
		// 			name.append(getName(p) + "_");
		// 		}
		// 	}

		// 	name.append("to_");
		// }
		if (t.getReturnType().equals(t) && !t.getReturnType().hasName()) {
			name.append("self");
		}
		else {
			if (t.getReturnType() instanceof UnresolvedType) {
				r.append(t.toString(), true);
				r.append(t.getReturnType().toString(), true);
			}
			name.append(getName(t.getReturnType()));
			
		}
		name.append(">");
		return name.toString();
	}

	private String getName(Signature t) {
		if (t instanceof UnresolvedSignature) {
			t = ((UnresolvedSignature) t).getResolved();
		}
		if (t instanceof DontCareSignature) {
			return "java.lang.Object";
		}
		else if (t instanceof NativeSignature) {
			return ((NativeSignature) t).getName();
		}
		else if (!t.hasName()) {
			anonymousTypes.add(t);
			return getAnonymousType(t);
		}
		if (!t.isFunction()) {
			
		}
		return escapeIdentifier(t.getName());
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