package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

/**
 * Walk the type structure and link unresolved types
 */
public class TypeResolver {
	private List<UnresolvedType> unresolvables = new ArrayList<UnresolvedType>();
	private List<Type> resolvedTypes = new ArrayList<Type>();

	/**
	 * Types that still need to be checked
	 */
	private List<Type> solveTypes = new ArrayList<Type>();

	public void solve(RootType root) throws UnresolvedTypeException {
		solveTypes.add(root);
		while (!solveTypes.isEmpty()) {
			//System.out.println("Still need to check:\n - "
			//                   + Utils.join(solveTypes, ",\n - "));
			Type t = solveTypes.iterator().next();
			solveTypes.remove(t);
			solve(t);
		}

		if (!unresolvables.isEmpty()) {
			throw new UnresolvedTypeException(unresolvables.iterator().next());
		}
		//System.out.println("Everything resolved...");
	}

	private List<Type> collectTypes(Type scope) {
		List<Type> types = new ArrayList<Type>(scope.getDefinedTypes());
		//add return and parameter
		{
			Type r = scope.getReturnType();
			if (r != null) {
				types.add(r);
			}
			for (Parameter p: scope.getParameters()) {
				Type pt = p.getType();
				////System.out.println("      Parameter: " + pt);
				types.add(pt);
			}
		}
		return types;
	}

	private boolean solve(Type scope) {
		//System.out.println("\nScope " + scope);

		List<Type> types = collectTypes(scope);
		// //System.out.println("\n      collected Types:\n"
		//                    + "        - " + Utils.join(types, ",\n      - "));

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
			//System.out.println("      trying to resolve " + u);
			if (resolve(u, scope)) {
				worked = true;
				Type r = u.getResolvedType();
				if (!resolvedTypes.contains(r)) {
					solveTypes.add(r);
				}
			}
			else {
				//System.out.println("      Couldn't resolve " + u);
				unresolvables.add(u);
				continue;
			}
		}

		resolvedTypes.add(scope);

		return worked;
	}

	private boolean resolve(Type t, Type scope) {
		UnresolvedType u = (UnresolvedType) t;
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
