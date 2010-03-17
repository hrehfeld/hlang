package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

public class TypePrinter {
	private Set<Type> printedTypes = new HashSet<Type>();
	private int indent = 0;
	
	public void print(RootType root) {
		print((Type) root);
	}

	private String indent() {
		String ind = "";
		for (int i = 0; i < indent; ++i) {
			ind += "  ";
		}
		return ind;
	}

	public void print(Type scope) {
		if (printedTypes.contains(scope)) {
			System.out.println(indent() +"Recursion.");
			return;
		}
		printedTypes.add(scope);

		System.out.print(indent() + scope);
		System.out.print(" (" + Utils.join(scope.getParameterTypes(), " ")
		                 + (scope.isFunction() ? " -> " : "")
		                 + scope.getReturnType());
		System.out.println(") " + scope.getInstruction());


		
		if (scope instanceof SelfType) {
			return;
		}
		
		indent++;
		for (String p: scope.getParameterNames()) {
			Type t = scope.getDefinedType(p);
			if (t.equals(scope)) {
				continue;
			}
			print(t);
		}

		if (scope instanceof AnonymousType) {
			for (Map.Entry<String,Type> e: ((AnonymousType) scope).getDefinedTypesInternal().entrySet()) {
				Type t = e.getValue();
				String name = e.getKey();
				if (t.equals(scope)) {
					continue;
				}

				//System.out.println(indent() + name);
				print(t);
			}
		}
		else {
			for (Type t: scope.getDefinedTypes()) {
				if (t.equals(scope)) {
					continue;
				}

				print(t);
			}
			
		}
		indent--;
	}
}
