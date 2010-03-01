package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

public class ValuePrinter {
	private int indent = 0;
	
	public void print(Root root) {
		print("root", (Value) root);
	}

	private void print(String name, Value scope) {
		String ind = "";
		for (int i = 0; i < indent; ++i) {
			ind += "  ";
		}
		System.out.print(ind + name + " (");
		try {
			System.out.print(scope.getType());
		}
		catch (UnresolvedIdentifierException e) {
			System.out.print("unknown");
		}
		System.out.println("): " + scope);
		

		if (scope instanceof EvaluateValue) {
			indent++;
			for (Value v: ((EvaluateValue) scope).getValues()) {
				print("", v);
			}
			indent--;
		}

		indent++;
		try {
			for (Map.Entry<String,Value> v: scope.getDefinedMembers().entrySet()) {
				print(v.getKey(), v.getValue());
			}
		}
		catch (UnresolvedIdentifierException e) {
		}
		indent--;
	}
}
