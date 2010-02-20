package de.haukerehfeld.hlisp;

import java.util.*;

import de.haukerehfeld.hlisp.semantics.*;

public class JavaEmitter {
	private final static String INDENT = "    ";

	private final static String prefix = "_hlisp";
	private final static String escapePrefix = "_escape";
	private final static String reservedPrefix = "_reserved";
	
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
		}};
	
	private int indent = 0;
	IndentStringBuilder r = new IndentStringBuilder();
	
	public String emit(Type s) {
		emitClassStart(s);
		indent++;

		emitConstructor(s);
		emitAction(s);
		emitMembers(s);

		indent--;
		emitClassEnd(s);
		return r.toString();
	}

	public String emit(RootType root) {
		r.append("package de.haukerehfeld.hlisp;", true);

		r.append("import de.haukerehfeld.hlisp.java.*;", true);		
		r.append("import java.io.*;", true);		
		r.append("", true);		
		
		emitClassStart(root);
		indent++;

		emitConstructor(root);
		emitAction(root);
		r.append("public static void main(String[] args) {", true);
		indent++;
		r.append("new Root(args)." + prefix + "_run();", true);
		indent--;
		r.append("}", true);		
		emitMembers(root);

		indent--;
		emitClassEnd(root);
		return r.toString();
	}

	private String getClassName(Type s) {
		return escapeIdentifier(s.getName());
	}

	
	private void emitClassStart(Type s) {
		r.append("/**", true);
		r.append(" * Type " + s.getName(), true);
		r.append(" */", true);
		String className = getClassName(s);

		r.append("public ");
		if (!(s instanceof RootType)) {
			r.append("static ");
		}
		r.append("class " + className);
		if (!(s instanceof RootType)) {
			r.append(" implements Function<");
			r.append(getClassName(s.getReturnType()));
			r.append(">");
		}
		r.append(" {", true);
	}

	private void emitClassEnd(Type s) {
		r.append("}", true);
		r.append("", true);
	}

	private void emitConstructor(Type s) {
		if (!s.getParameters().isEmpty()) {
			for (String p: getParameterStrings(s, " ")) {
				r.append("private final " + p + ";", true);
			}
			r.append("", true);

			r.append("public " + getClassName(s) + "(");
			r.append(Utils.join(getParameterStrings(s, " "), ", "));
			r.append(")");
			r.append(" {", true);
			indent++;
			for (String p: getParameterNames(s)) {
				r.append("this." + p + " = " + p + ";", true);
			}
			indent--;
			r.append("}", true);
			r.append("", true);
		}
	}

	private void emitAction(Type s) {
		if (!(s instanceof RootType)) {
			r.append("@Override ");
		}
		r.append("public " + getClassName(s.getReturnType())
		         + " " + prefix + "_run() {", true);
		indent++;
		r.append("/* <body here>; */", true);
		r.append("return null;", true);
		indent--;
		r.append("}", true);
		r.append("", true);
	}

	private void emitMembers(Type s) {
		for (Type member: s.getDefinedTypes()) {
			String name = getClassName(member);
			String parameters = Utils.join(getParameterStrings(member, " "), ", ");
			r.append("public " + name + " " + name + "(" + parameters + ") {", true);
			indent++;
			String paramsCall = Utils.join(getParameterNames(member), ", ");
			r.append("return new " + name + "(" + paramsCall + ");", true);
			indent--;
			r.append("}", true);

			member.emit(this);
		}
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

	public List<String> getParameterNames(Type t) {
		List<String> names = new ArrayList<String>();
		for (Parameter p: t.getParameters()) {
			names.add(p.getName());
		}
		return names;
	}

	public List<String> getParameterTypeNames(Type t) {
		List<String> names = new ArrayList<String>();
		for (Parameter p: t.getParameters()) {
			names.add(p.getType().getName());
		}
		return names;
	}

	public List<String> getParameterStrings(Type t, String join) {
		List<String> params = new ArrayList<String>();
		for (Parameter p: t.getParameters()) {
			params.add(p.getType().getName() + join + p.getName());
		}
		return params;
	}


	
}