package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

/**
 * Walk the value structure and link unresolved values
 */
public class Resolver {
	private List<UnresolvedType> unresolvableTypes = new ArrayList<UnresolvedType>();
	private List<UnresolvedInstruction> unresolvableValues = new ArrayList<UnresolvedInstruction>();
	private Set<Type> checkedTypes = new HashSet<Type>();
	private Set<Type> checkedInstructionTypes = new HashSet<Type>();

	private boolean runAgain = false;

	private int indent = 0;

	private String indent() {
		String ind = "";
		for (int i = 0; i < indent; ++i) {
			ind += "  ";
		}
		return ind;
	}

	public void solve(RootType root) throws UnresolvedTypeException, SemanticException {
		do {
			System.out.println("Unresolved types: \n    " + Utils.join(unresolvableTypes, ",\n    "));
			System.out.println("Unresolved values: \n    " + Utils.join(unresolvableValues, ",\n    "));

			unresolvableTypes.clear();
			checkedTypes.clear();
			System.out.println("---------- starting resolve iteration ----------");
			runAgain = false;
			solve((Type) root, root);
		} while (runAgain);

		System.out.println("Unresolved types: \n    " + Utils.join(unresolvableTypes, ",\n    "));
		System.out.println("Unresolved values: \n    " + Utils.join(unresolvableValues, ",\n    "));
		if (!unresolvableTypes.isEmpty()) {
			throw new UnresolvedTypeException(unresolvableTypes.get(0));
		}

		solveInstructions(root);
		
		System.out.println("Everything resolved...");
	}

	private Type getType(String name, Type scope) {
		if (name.equals(Type.SELF)) {
			return scope;
		}
		
		else if (scope.isTypeDefinedRecursive(name)) {
			return scope.getDefinedTypeRecursive(name);
		}
		return null;
	}

	private void solve(Type t, Type scope) throws SemanticException {
		if (checkedTypes.contains(t)) {
			return;
		}
		checkedTypes.add(t);

		System.out.print(indent() + "Checking Type " + t + "... ");

		if (!t.isResolved()) {
			System.out.print("\n" + indent() + "    ");
			UnresolvedType u = (UnresolvedType) t;
			//special case
			if (u.getName().equals(Type.SELF)) {
				System.out.println("resolved to " + scope + ".");
				u.setResolved(scope);
			}
			else {
				if (scope.isTypeDefinedRecursive(u.getNames().get(0))) {
					Type resolved = scope.getDefinedTypeRecursive(u.getNames().get(0));

					for (int i = 1; i < u.getNames().size(); ++i) {
						Type next = resolved.getDefinedTypeRecursive(u.getNames().get(i));
						if (next == null) {
							System.out.print(" failed");
							unresolvableTypes.add(u);
							return;
						}
						resolved = next;
					}
					
					System.out.print(" resolved  to " + resolved);
					u.setResolved(resolved);
				}
				else {
					System.out.println(" failed");
					unresolvableTypes.add(u);
				}
				System.out.println(".");
				return;
			}
		}
		else {
			System.out.println("already resolved.");
		}

		for (Type child: collectTypes(t)) {
			indent++;
			solve(child, t);
			indent--;
		}
		
	}

	void solveInstructions(Type t) throws SemanticException {
		if (checkedInstructionTypes.contains(t)) {
			System.out.println(indent() + "instruction in " + t + " already checked.");
			return;
		}
		System.out.println(indent() + "solving instruction in " + t + ".");
		checkedInstructionTypes.add(t);
		solve(t.getInstruction(), t);

		for (Type child: collectTypes(t)) {
			System.out.println(indent() + "solving instruction in child " + child + ".");
			indent++;
			solveInstructions(child);
			indent--;
		}
	}

	Instruction solve(Instruction instr, Type scope) throws SemanticException {
		if (instr instanceof ListInstruction) {
			return solve((ListInstruction)instr, scope);
		}
		else if (instr instanceof FunctionCallInstruction) {
			Type fun = ((FunctionCallInstruction) instr).getFunction();

			solve(fun, scope);
		}
		else if (instr instanceof NativeInstruction) {
			solve(instr.getReturnType(), scope);
		}
		return instr;
	}

	private Instruction solve(ListInstruction list, Type scope) throws SemanticException {
		Instruction instr = new ListInstructionResolver().solve(list, scope, this);
		unresolvableValues.addAll(unresolvableValues);
		return instr;
	}



	private List<Type> collectTypes(Type scope) {
		List<Type> types = new ArrayList<Type>();
		//add return and parameter
		types.addAll(scope.getParameterTypes());
		types.add(scope.getReturnType());
		types.addAll(scope.getDefinedTypes());

		//filter self
		Iterator<Type> it = types.iterator();
		while (it.hasNext()) {
			if (it.next().equals(scope)) {
				it.remove();
			}
		}
		return types;
	}
}
