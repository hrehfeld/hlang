package de.haukerehfeld.hlisp;

import java.util.*;
import java.io.*;


import de.haukerehfeld.hlisp.semantics.*;

public class JavaEmitter {
	private final static String INDENT = "    ";

	private final static String prefix = "_hlisp";
	private final static String escapePrefix = "_escape";
	private final static String reservedPrefix = "_reserved";
	private final static String nativePrefix = "_native";
	private final static String run = "_run";
	
	private final static HashMap<String, String> illegal = new HashMap<String, String>() {{
			put("+", "plus");
			put("-", "minus");
			put("<", "smallerthan");
			put(">", "greaterthan");
			put("=", "equal");
			put("*", "star");
			put("/", "slash");
		}};
	private final static List<String> reserved = new ArrayList<String>() {{
			add("while");
			add("Void");
			add("void");

		}};

	private int indent = 0;
	IndentStringBuilder r = new IndentStringBuilder();

	private List<Type> emittedTypes = new ArrayList<Type>();

	public String emitFunction(Type f) {
		emitClassStart(f);
		indent++;

		emitConstructor(f);
		emitAction(f);
		emitMembers(f);

		indent--;
		emitClassEnd();
		return r.toString();
	}

	public String emit(Type s) {
		if (emittedTypes.contains(s)) {
			System.out.println("Skipping emit of " + s + ", already emitted.");
			return "";
		}
		emittedTypes.add(s);
		
		if (s.isFunction()) {
			return emitFunction(s);
		}
		r.append("private " + escapeIdentifier(s)
		         + " " + escapeIdentifier(s) + ";", true);
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
			indent++;

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

			indent--;
			emitClassEnd();
			return r.toString();
		}
		catch (RuntimeException e) {
			System.out.println(r.toString() + "\n------------------------");
			throw e;
		}
	}


	private void emitClassStart(Type v) {
		r.append("/**", true);
		r.append(" * Type " + v.getName(), true);
		r.append(" */", true);

		r.append("public ");
		// if (!(s instanceof RootType)) {
		// 	r.append("static ");
		// }
		r.append("class " + escapeIdentifier(v.getName()));
		
		if (!(v instanceof RootType)) {
			r.append(" implements Function<");
			r.append(escapeIdentifier(v.getReturnType()));
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

			r.append("public " + s.getName() + "(");
			r.append(Utils.join(getParameterStrings(s, " "), ", "));
			r.append(")");
			r.append(" {", true);
			indent++;
			for (String p: s.getParameterNames()) {
				String e = escapeIdentifier(p);
				r.append("this." + e + " = " + e + ";", true);
			}
			indent--;
			r.append("}", true);
			r.append("", true);
		}
	}

	private void emitMembers(Type v) {
		for (Type member: v.getDefinedTypes()) {
			emit(member);
		}
	}

	private void emit(Instruction i) {
	}

	private void emitAction(Type f) {
		r.append("public " + escapeIdentifier(f)
		         + " " + prefix + run + "() {", true);
		indent++;
		emit(f.getInstruction());
		indent--;
		r.append("}", true);
		r.append("", true);
	}

	private String escapeIdentifier(Type id) {
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

	private class IndentStringBuilder {
		private final StringBuilder b = new StringBuilder();
		private StringBuilder line = new StringBuilder();

		public void append(String s) {
			append(s, false);
		}
		
		public void append(String s, boolean newline) {
			line.append(s);

			if (newline) {
				StringBuilder ind = new StringBuilder();
				for (int i = 0; i < indent; ++i) {
					ind.append(INDENT);
				}
				b.append(ind.toString());
				b.append(line.toString());
				line = new StringBuilder();
				b.append("\n");
			}
		}

		@Override public String toString() {
			b.append(line.toString());
			return b.toString();
		}
	}

	public List<String> getParameterStrings(Type v, String join) {
		List<String> params = new ArrayList<String>();
		int i = 0;
		List<String> paramNames = v.getParameterNames();
		for (Type t: v.getParameterTypes()) {
			String p = paramNames.get(i);
			params.add(escapeIdentifier(t) + join + escapeIdentifier(p));
			++i;
		}
		return params;
	}


	
}