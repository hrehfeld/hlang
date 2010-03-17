package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import de.haukerehfeld.hlisp.IndentStringBuilder;
import java.util.*;

/**
 * Walk the value structure and link unresolved values
 */
public class Resolver {
	private List<UnresolvedSignature> unresolvableTypes = new ArrayList<UnresolvedSignature>();
	private List<UnresolvedInstruction> unresolvableValues = new ArrayList<UnresolvedInstruction>();
	private Set<Signature> checkedTypes = new LinkedHashSet<Signature>();
	private Set<Type> checkedInstructionTypes = new LinkedHashSet<Type>();

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

	// private Type getType(String name, Type scope) {
	// 	if (name.equals(Type.SELF)) {
	// 		return scope;
	// 	}
		
	// 	else if (scope.isTypeDefinedRecursive(name)) {
	// 		return scope.getDefinedTypeRecursive(name);
	// 	}
	// 	return null;
	// }

	private Signature solve(Signature t, Type scope) throws SemanticException {
		if (checkedTypes.contains(t) || t instanceof SelfType) {
			return  t;
		}
		checkedTypes.add(t);

		r.print("Checking Signature " + t + " in " + scope + "... ");

		Signature result;
		Type newScope = scope;
		if (t.isResolved()) {
			r.print("already resolved.", true);
			result = t;
			if (t instanceof Type) {
				newScope = (Type) t;
			}
		}
		else {
			UnresolvedSignature u = (UnresolvedSignature) t;
			//special case
			if (u.getName().equals(Type.SELF)) {
				result = scope;
				r.print("resolved to " + result + ".", true);
			}
			else if (u.getName().equals(Type.DONTCARE)) {
				Signature res = new DontCareSignature();
				r.print("resolved to " + res + ".", true);
				return res;
			}
			else if (scope.isTypeDefinedRecursive(u.getNames().get(0))) {
				Type resolved = scope.getDefinedTypeRecursive(u.getNames().get(0));

				for (int i = 1; i < u.getNames().size(); ++i) {
					Type next = resolved.getDefinedTypeRecursive(u.getNames().get(i));
					if (next == null) {
						r.print("resolve failed", true);
						unresolvableTypes.add(u);
						return null;
					}
					resolved = next;
				}
				
				r.print("resolved to " + resolved + ".", true);
				result = resolved;
				newScope = resolved;
			}
			else {
				r.print(" failed.", true);
				unresolvableTypes.add(u);
				return null;
			}
		}

		r.indentMore();
		if (result.isFunction()) {
			result.setParameterTypes(solve(result.getParameterTypes(), newScope));
			
			if (result instanceof Type) {
				solve(Collections.list(Collections.enumeration(((Type) result).getDefinedTypes())), newScope);
			}
		}
		result.setReturnType(solve(result.getReturnType(), newScope));

		r.indentLess();
		return result;
	}

	private List<Signature> solve(List<? extends Signature> types, Type scope) throws SemanticException {
		List<Signature> result = new ArrayList<Signature>();
		for (Signature t: types) {
			result.add(solve(t, scope));
		}
		return result;
	}

	private void solveInstructions(Type t) throws SemanticException {
		//don't check again, selftypes, or anonymous types
		if (checkedInstructionTypes.contains(t) || t instanceof SelfType ) {
			return;
		}
		r.print("Instruction " + t.getInstruction() + " in " + t + " needs solving.", true);
		checkedInstructionTypes.add(t);
		Instruction instr = solve(t.getInstruction(), t);
		if (!instr.getReturnType().isCompatible(t.getReturnType())) {
			throw new SemanticException("Type " + t + " with Returntype " + t.getReturnType()
			                            + " has instruction with Returntype "
			                            + instr.getReturnType() + ".");
		}
		r.print("Solved to " + instr, true);
		t.setInstruction(instr);

		r.indentMore();
		for (Signature child: collectTypes(t)) {
			if (child instanceof Type) {
				solveInstructions((Type) child);
			}
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

			//resolve contained types
			t = (Type) solve(t, scope);
			//any unresolvedinstruction we encounter here is single,
			//so we don't need to duplicate the scope into the
			//function call
			return new FunctionCallInstruction(t);
		}
		else if (instr instanceof LambdaInstruction) {
			r.print("#####Solving LambdaInstruction " + instr);
			LambdaInstruction linstr = (LambdaInstruction) instr;
			Type n = (Type) solve(linstr.getFunction(), scope);
			r.print(linstr.getFunction() + "resolved to " + n.toString(), true);
			linstr.setFunction(n);
			solveInstructions(linstr.getFunction());
		}
		else if (instr instanceof FunctionCallInstruction) {
			FunctionCallInstruction f = (FunctionCallInstruction) instr;
			Type fun = f.getFunction();
			
			r.indentMore();
			f.setFunction((Type) solve(fun, scope));
			
			if (!f.isStatic()) {
				solve(f.getScope(), scope);
			}
			
			r.indentLess();
		}
		else if (instr instanceof NativeInstruction) {
			r.indentMore();
			((NativeInstruction) instr).setReturnType(solve(instr.getReturnType(), scope));
			r.indentLess();
		}
		r.indentLess();
		return instr;
	}

	private Instruction solve(ListInstruction list, Type scope) throws SemanticException {
		Instruction instr = new ListInstructionResolver(new IndentStringBuilder()).solve(list, scope, this);
		unresolvableValues.addAll(unresolvableValues);
		return instr;
	}



	private List<Signature> collectTypes(Type scope) {
		List<Signature> types = new ArrayList<Signature>();
		//add return and parameter
		types.addAll(scope.getParameterTypes());
		types.add(scope.getReturnType());
		types.addAll(scope.getDefinedTypes());

		//filter self
		Iterator<Signature> it = types.iterator();
		while (it.hasNext()) {
			if (it.next().equals(scope)) {
				it.remove();
			}
		}
		return types;
	}
}
