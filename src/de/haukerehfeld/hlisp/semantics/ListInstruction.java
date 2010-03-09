package de.haukerehfeld.hlisp.semantics;

import java.util.*;
import de.haukerehfeld.hlisp.Utils;


public class ListInstruction implements Instruction  {
	public ListInstruction() {
		this(Collections.<Instruction>emptyList());
	}

	public ListInstruction(List<Instruction> instructions) {
		this.instructions = instructions;
	}

	private List<Instruction> instructions = new ArrayList<Instruction>();
	public void add(Instruction v) {
		instructions.add(v);
	}
	public List<Instruction> getInstructions() { return instructions; }
	public void setInstructions(List<Instruction> instructions) { this.instructions = instructions; }

	@Override public Type getReturnType() { return instructions.get(instructions.size() - 1).getReturnType(); }
	

	@Override public String toString() {
		return "List[" + Utils.join(instructions, ", ") + "]";
	}


	// public void finish() throws SemanticException {
	// 	if (instructions.isEmpty()) {
	// 		System.err.println("Empty evaluate instruction." + this);
	// 		setType(VoidType.create());
	// 		return;
	// 	}
	// 	int last = instructions.size() - 1;
	// 	Instruction lastInstruction = instructions.get(last);
	// 	Type lastInstructionType = lastInstruction.getType();
	// 	if (getType() != null && !getType().equals(lastInstructionType)) {
	// 		String e = "Expected type " + getType() + ", but last instruction (" + lastInstruction + ") is of type "
	// 		    + lastInstructionType + ".";
	// 		//throw new SemanticException(e);
	// 		System.err.println(e);
	// 	}
	// 	setType(lastInstructionType);

	// 	for (Instruction v: instructions) {
	// 		if (v instanceof EvaluateInstruction) {
	// 			((EvaluateInstruction) v).finish();
	// 		}
	// 	}

	// }

	// @Override public String toString() {
	// 	String result = super.toString() + " (";
	// 	for (Instruction v: instructions) {
	// 		boolean skip = false;
	// 		Instruction t = v;

	// 		while (true) {
	// 			if (t == this) {
	// 				//System.out.println("ERROR, evaluate references itself!");
	// 				result += "self, ";
	// 				skip = true;
	// 				break;
	// 			}

	// 			if (!(t instanceof UnresolvedIdentifierInstruction) || !((UnresolvedIdentifierInstruction) v).isResolved()) {
	// 				break;
	// 			}
	// 			t = ((UnresolvedIdentifierInstruction) v).getResolved();
	// 		}
			
	// 		if (skip) { continue; }

	// 		result += v + ", ";
	// 	}
		
	// 	return result + ")";
	// }
	
}