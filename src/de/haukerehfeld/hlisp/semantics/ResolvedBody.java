package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public class ResolvedBody implements Body {

	private List<Variable> variables = new ArrayList<Variable>();
	private List<Instruction> instructions = new ArrayList<Instruction>();

	public void add(Variable v) {
		variables.add(v);
	}

	public void add(Instruction instr) {
		instructions.add(instr);
	}

	@Override public List<Variable> getDefinedVariables() { return variables; }
	@Override public List<Instruction> getInstructions() { return instructions; }
}