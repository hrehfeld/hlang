package de.haukerehfeld.hlisp.semantics;

import java.util.*;

import de.haukerehfeld.hlisp.Utils;

public abstract class AbstractCallInstruction implements Instruction {
	private List<Instruction> parameters;
	public List<Instruction> getParameters() { return parameters; }
	public void addParameter(Instruction parameter) { parameters.add(parameter); }
	
	public AbstractCallInstruction() {
		new ArrayList<Instruction>();
	}
	public AbstractCallInstruction(List<Instruction> parameters) {
		this.parameters = parameters;
	}
}