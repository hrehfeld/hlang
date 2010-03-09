package de.haukerehfeld.hlisp;

import java.util.*;

public class IndentStringBuilder {
	private final static String INDENT = "  ";

	private int indent = 0;
	private StringBuilder b = new StringBuilder();
	private StringBuilder line = new StringBuilder();

	private boolean needsIndentation = false;

	public void append(String s) {
		append(s, false);
	}
		
	public void appendln(String s) {
		append(s, true);
	}
	
	public void append(String s, boolean newline) {
		line.append(s);
		
		if (newline) {
			if (needsIndentation) {
				indent();
			}
			b.append(line.toString());
			b.append("\n");

			line = new StringBuilder();

			needsIndentation = true;
		}
	}

	private void indent() {
		StringBuilder ind = new StringBuilder();
		for (int i = 0; i < indent; ++i) {
			ind.append(INDENT);
		}
		b.append(ind.toString());
	}

	public void indentMore() {
		indent++;
	}
	public void indentLess() {
		indent--;
	}

	public void print(String s) {
		print(s, false);
	}
	public void print(String s, boolean newline) {
		append(s, newline);
		appendRestOfLine();
		System.out.print(b.toString());
		line = new StringBuilder();
		b = new StringBuilder();
	}

	private void appendRestOfLine() {
		if (!line.toString().equals("")) {
			indent();
			b.append(line.toString());
		}
	}

	@Override public String toString() {
		appendRestOfLine();
		line = new StringBuilder();
		return b.toString();
	}
}