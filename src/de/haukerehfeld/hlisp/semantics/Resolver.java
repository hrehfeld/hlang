package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.IndentStringBuilder;
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

	private IndentStringBuilder r = new IndentStringBuilder();

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

		new TypePrinter().print(root);

		r = new IndentStringBuilder();
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
		if (checkedTypes.contains(t) || t instanceof SelfType) {
			return;
		}
		checkedTypes.add(t);

		r.print("Checking Type " + t + " in " + scope + "... ");
		if (!t.isResolved()) {
			UnresolvedType u = (UnresolvedType) t;
			//special case
			if (u.getName().equals(Type.SELF)) {
				r.print("resolved to " + scope + ".", true);
				u.setResolved(scope);
			}
			else if (u.getName().equals(Type.DONTCARE)) {
				Type n = new DontCareType(scope);
				r.print("resolved to " + n + ".", true);
				u.setResolved(n);
			}
			else {
				if (scope.isTypeDefinedRecursive(u.getNames().get(0))) {
					Type resolved = scope.getDefinedTypeRecursive(u.getNames().get(0));

					for (int i = 1; i < u.getNames().size(); ++i) {
						Type next = resolved.getDefinedTypeRecursive(u.getNames().get(i));
						if (next == null) {
							r.print("resolve failed", true);
							unresolvableTypes.add(u);
							return;
						}
						resolved = next;
					}
					
					r.print("resolved to " + resolved + ".", true);
					u.setResolved(resolved);
				}
				else {
					r.print(" failed.", true);
					unresolvableTypes.add(u);
				}
				return;
			}
		}
		else {
			r.print("already resolved.", true);
		}

		for (Type child: collectTypes(t)) {
			r.indentMore();
			solve(child, t);
			r.indentLess();
		}
		
	}

	void solveInstructions(Type t) throws SemanticException {
		//don't check again, selftypes, or anonymous types
		if (checkedInstructionTypes.contains(t) || t instanceof SelfType || !t.hasName()) {
			return;
		}
		r.print("Instruction " + t.getInstruction() + " in " + t + " needs solving.", true);
		checkedInstructionTypes.add(t);
		Instruction instr = solve(t.getInstruction(), t);
		if (!(t instanceof NativeType) && !instr.getReturnType().equals(t.getReturnType())) {
			throw new SemanticException("Type " + t + " with Returntype " + t.getReturnType()
			                            + " has instruction with Returntype "
			                            + instr.getReturnType() + ".");
		}
		r.print("Solved to " + instr, true);
		t.setInstruction(instr);

		r.indentMore();
		for (Type child: collectTypes(t)) {
			solveInstructions(child);
		}
		r.indentLess();
	}

	Instruction solve(Instruction instr, Type scope) throws SemanticException {
		r.indentMore();
		if (instr instanceof ListInstruction) {
			return solve((ListInstruction)instr, scope);
		}
		else if (instr instanceof UnresolvedInstruction) {
			String id = ((UnresolvedInstruction) instr).getIdentifier();
			if (!scope.isTypeDefinedRecursive(id)) {
				new TypePrinter().print(scope);
				throw new SemanticException("Couldn't resolve identifier "
				                            + id + " in scope " + scope);
			}

			Type t = scope.getDefinedTypeRecursive(id);

			//first resolve return type
			solve(t, scope);

			return new FunctionCallInstruction(t, scope);
		}
		else if (instr instanceof FunctionCallInstruction) {
			FunctionCallInstruction f = (FunctionCallInstruction) instr;
			Type fun = f.getFunction();
			
			r.indentMore();
			solve(fun, scope);
			if (!f.isStatic()) {
				Type funScope = f.getScope();
				solve(funScope, scope);
			}
			r.indentLess();
		}
		else if (instr instanceof NativeInstruction) {
			r.indentMore();
			solve(instr.getReturnType(), scope);
			r.indentLess();
		}
		r.indentLess();
		return instr;
	}

	private Instruction solve(ListInstruction list, Type scope) throws SemanticException {
		Instruction instr = new ListInstructionResolver(r).solve(list, scope, this);
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
