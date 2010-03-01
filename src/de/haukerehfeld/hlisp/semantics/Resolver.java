package de.haukerehfeld.hlisp.semantics;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.Utils;
import java.util.*;

/**
 * Walk the value structure and link unresolved values
 */
public class Resolver {
	public static final String PARENTTYPESYMBOL = "this";

	private List<UnresolvedType> unresolvableTypes = new ArrayList<UnresolvedType>();
	private Set<Value> checkedValues = new HashSet<Value>();

	private boolean runAgain = false;

	public void solve(Root root) throws UnresolvedTypeException {
		do {
			unresolvableTypes.clear();
			System.out.println("---------- starting resolve iteration ----------");
			runAgain = false;
			solve((Value) root);
		} while (runAgain);


		if (!unresolvableTypes.isEmpty()) {
			throw new UnresolvedTypeException(unresolvableTypes.get(0));
		}
		System.out.println("Everything resolved...");
	}

	private void solve(Value scope) {
		if (checkedValues.contains(scope)) {
			return;
		}
		if (scope instanceof UnresolvedIdentifierValue) {
			UnresolvedIdentifierValue identifier = (UnresolvedIdentifierValue) scope;
			String id = identifier.getIdentifier();
			if (id.equals(Resolver.PARENTTYPESYMBOL)) {
				identifier.setResolved(identifier.getScope());
			}
			else if (identifier.getScope().isMemberDefinedRecursive(id)) {
				identifier.setResolved(identifier.getScope().getDefinedMemberRecursive(id));
			}
			else {
				runAgain = true;
				System.out.println("Couldnt' resolve Identifier " + identifier);
				return;
			}
		}

		checkedValues.add(scope);


		

		if (scope instanceof EvaluateValue) {
			for (Value v: ((EvaluateValue) scope).getValues()) {
				System.out.println("Solving value in an evaluate (" + v + ").");
				solve(v);
			}
		}
		else {
			if (scope.getType() == null) {
				System.out.println("ERROR: nulltype!" + scope);
				//runAgain = true;
				return;
			}

			System.out.println("Value of type '" + scope.getType() + "'.");


			solve(scope.getType(), scope);
		}
		
		for (Map.Entry<String,Value> child: scope.getDefinedMembers().entrySet()) {
			System.out.println("Solving '" + child.getKey() + "' (" + child.getValue() + ").");
			solve(child.getValue());
		}
	}


	private void solve(Type type, Value scope) {
		List<Type> types = new ArrayList<Type>();
		types.add(type);

		//drop self and resolved types
		for (int i = 0; i < types.size(); ++i) {
			Type t = types.get(i);

			System.out.println("  Checking " + t + ". List: (" + Utils.join(types, ", ") + ").");


			if (t instanceof UnresolvedType) {
				UnresolvedType u = (UnresolvedType) t;
				if (!u.isResolved()) {
					//special case
					if (u.getName().equals(PARENTTYPESYMBOL)) {
						u.setResolvedType(type);
					}

					//System.out.println("      trying to resolve " + u);
					if (resolve(u, scope)) {
						addType(types, u.getResolvedType());
					}
					else {
						System.out.println("      Couldn't resolve " + u);
						unresolvableTypes.add(u);
						continue;
					}
				}
			}
			else {
				addType(types, t);
			}
		}
		
	}

	private boolean resolve(UnresolvedType u, Value scope) {
		String uName = u.getName();

		if (scope.isMemberDefinedRecursive(uName)) {
			Type resolved = scope.getDefinedMemberRecursive(uName).getType();

			System.out.println("      Resolved " + u  + " to " + resolved);
			u.setResolvedType(resolved);
			return true;
		}
		return false;
	}

	private List<Type> collectTypes(Type scope) {
		if (scope.getDefinedTypes() == null) {
			System.out.println("!!!!!!null members!" + scope);
		}
		List<Type> types = new ArrayList<Type>();
		//add return and parameter
		types.addAll(scope.getParameterTypes());
		types.add(scope.getReturnType());
		return types;
	}

	private void addType(List<Type> types, Type type) {
		if (!types.contains(type)) { types.add(type); }

		for (Type child: collectTypes(type)) {
			if (!types.contains(child)) { types.add(child); }
		}
	}

	
}
