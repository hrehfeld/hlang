package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

public class TypePrinter {
	private int indent = 0;
	
	public void print(RootType root) {
		print((Type) root);
	}

	private void print(Type scope) {
		String ind = "";
		for (int i = 0; i < indent; ++i) {
			ind += "    ";
		}
		System.out.println(ind + scope);

		indent++;
		for (Type t: scope.getDefinedTypes()) {
			if (t.equals(scope)) {
				continue;
			}
			print(t);
		}
		indent--;
	}
}
