package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

/**
 * Walk the value structure and link unresolved values
 */
public class Resolver {
	private static final String PARENTTYPESYMBOL = "=";
	private List<UnresolvedType> unresolvableTypes = new ArrayList<UnresolvedType>();
	private List<Value> resolvedValues = new ArrayList<Value>();
	private List<Type> resolvedTypes = new ArrayList<Type>();

	/**
	 * Values that still need to be checked
	 */
	private List<Value> solveValues = new ArrayList<Value>();
	private List<Type> solveTypes = new ArrayList<Type>();

	public void solve(Root root) throws UnresolvedTypeException {
		solveValues.add(root);
		solveTypes.add(root.getType());
		while (!solveValues.isEmpty()) {
			//System.out.println("Still need to check:\n - "
			//                   + Utils.join(solveValues, ",\n - "));
			Value t = solveValues.remove(0);
			solve(t);
		}

		if (!unresolvableTypes.isEmpty()) {
			throw new UnresolvedTypeException(unresolvableTypes.get(0));
		}
		System.out.println("Everything resolved...");
	}

	private List<Type> collectTypes(Type scope) {
		List<Type> types = new ArrayList<Type>(scope.getDefinedTypes().values());
		//add return and parameter
		types.add(scope.getReturnType());
		types.addAll(scope.getParameterTypes());
		return types;
	}

	private boolean solve(Value scope) {
		solve(scope.getType());

		if (scope instanceof EvaluateValue) {
			for (Value v: ((EvaluateValue) scope).getValues()) {
				solve(v);
			}
		}

		for (Map.Entry<String,Value> child: scope.getDefinedMembers().entrySet()) {
			solve(child.getValue());
		}
		return true;
	}

	private boolean solve(Type type) {
		List<Type> types = collectTypes(type);
		// //System.out.println("\n      collected Values:\n"
		//                    + "        - " + Utils.join(values, ",\n      - "));

		List<UnresolvedType> unresolved = new ArrayList<UnresolvedType>();

		//drop self and resolved types
		for (Type t: types) {
			if (t instanceof UnresolvedType) {
				UnresolvedType u = (UnresolvedType) t;
				if (!u.isResolved()) {
					unresolved.add(u);
				}
				else if (!resolvedTypes.contains(u.getResolvedType())) {
					solveTypes.add(u.getResolvedType());
				}
			}
			else {
				if (!resolvedTypes.contains(t)) {
					solveTypes.add(t);
				}
			}
		}
		
		//System.out.print("    unresolved Types:");
		if (unresolved.isEmpty()) {
			//System.out.println(" none.");
		}
		else {
			//System.out.println("\n      - " + Utils.join(unresolved, ",\n      - "));
		}

		boolean worked = false;
		for (UnresolvedType u: unresolved) {
			//special case
			if (u.getName().equals(PARENTTYPESYMBOL)) {
				u.setResolvedType(type);
			}

			//System.out.println("      trying to resolve " + u);
			if (resolve(u, type)) {
				worked = true;
				Type r = u.getResolvedType();
				if (!resolvedTypes.contains(r)) {
					solveTypes.add(r);
				}
			}
			else {
				//System.out.println("      Couldn't resolve " + u);
				unresolvableTypes.add(u);
				continue;
			}
		}

		resolvedTypes.add(type);

		return worked;
	}

	private boolean resolve(UnresolvedType u, Type scope) {
		String uName = u.getName();

		if (scope.isTypeDefined(uName)) {
			Type resolved = scope.getDefinedType(uName);

			//System.out.println("      Resolved " + u  + " to " + resolved);
			u.setResolvedType(resolved);
			return true;
		}
		return false;
	}
}
