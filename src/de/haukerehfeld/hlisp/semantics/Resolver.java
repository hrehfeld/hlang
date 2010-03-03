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
	private List<UnresolvedIdentifierValue> unresolvableValues = new ArrayList<UnresolvedIdentifierValue>();
	private Set<Value> checkedValues = new HashSet<Value>();

	private boolean runAgain = false;

	public void solve(Root root) throws UnresolvedTypeException, SemanticException {
		do {
			System.out.println("Unresolved types: \n    " + Utils.join(unresolvableTypes, ",\n    "));
			System.out.println("Unresolved values: \n    " + Utils.join(unresolvableValues, ",\n    "));

			unresolvableTypes.clear();
			checkedValues.clear();
			System.out.println("---------- starting resolve iteration ----------");
			runAgain = false;
			solve((Value) root);
		} while (runAgain);


		if (!unresolvableTypes.isEmpty()) {
			throw new UnresolvedTypeException(unresolvableTypes.get(0));
		}
		System.out.println("Everything resolved...");
	}

	private void solve(EvaluateValue scope) throws SemanticException {
		Value last = null;
		int i = 0;
		
		boolean parameterExpected = false;
		Function lastFunction = null;
		int lastFunctionI = -1;
		Type lastFunctionType = null;
		CallValue lastFunctionCall = null;
		List<Type> lastFunctionParameterTypes = null;
		
		List<Value> newValues = new ArrayList<Value>();
		
		for (Value v: ((EvaluateValue) scope).getValues()) {
			System.out.println("Solving value in an evaluate (" + v + ").");
			if (v instanceof UnresolvedIdentifierValue
			    && !((UnresolvedIdentifierValue) v).isResolved()) {
				UnresolvedIdentifierValue identifier = (UnresolvedIdentifierValue) v;
				String id = identifier.getIdentifier();
				//try to bind as parameter to last function
				int parameterI = i - lastFunctionI;
				if (parameterExpected) {
					if (!lastFunctionParameterTypes.get(parameterI).equals(v.getType())) {
						throw new SemanticException("parameter of type " + lastFunctionParameterTypes.get(parameterI) + " expected, but " + v.getType() + "(" + v + ") given.");
					}
					lastFunctionCall.addParameter(v);

					System.out.println(v + " is parameter to " + lastFunction + ".");

					//parameters finished
					if (parameterI >= lastFunctionParameterTypes.size()) {
						parameterExpected = false;
						
						//the return value of the function is a function that expects params
						if (!(lastFunctionType.getReturnType().getParameterTypes().isEmpty())) {
							parameterExpected = true;
							lastFunctionI = i;
							lastFunction = lastFunctionCall;
							lastFunctionType = lastFunctionType.getReturnType();
							lastFunctionParameterTypes = lastFunctionType.getParameterTypes();
							lastFunctionCall = new CallValue(lastFunction);

							System.out.println("Next is a function " + lastFunction + ".");
							
						}
						else {
							last = lastFunctionCall;
						}
					}
				}
				else if (last != null && last.isMemberDefined(id)) {
					Value r = last.getDefinedMember(id);
					identifier.setResolved(r);
					System.out.println("Resolving " + id + " as " + r + " from " + last);

					if (r instanceof Function && !r.getType().getParameterTypes().isEmpty()) {
						lastFunctionI = i;
						lastFunction = (Function) r;
						lastFunctionType = r.getType();
						lastFunctionParameterTypes = lastFunctionType.getParameterTypes();
						lastFunctionCall = new CallValue(lastFunction);
					}
				}
				else if (id.equals(PARENTTYPESYMBOL)) {
					identifier.setResolved(scope);
					last = scope;
					System.out.println(PARENTTYPESYMBOL + " found, setting to " + scope + ".");
				}
				else if (scope.isMemberDefinedRecursive(id)) {
					last = scope.getDefinedMemberRecursive(id);
					identifier.setResolved(last);
					System.out.println(id + " found in current scope, setting to " + last);
				}
				else {
					//runAgain = true;
					unresolvableValues.add(identifier);
					System.out.println("Couldnt' resolve Identifier " + identifier
					                   + ", stopping evaluation of list.");
					break;
				}
			}

			last = v;
			

			solve(v);
			++i;
		}
	}

	private void solve(Value scope) throws SemanticException {
		if (checkedValues.contains(scope)) {
			return;
		}
		// if (scope instanceof UnresolvedIdentifierValue) {
		// 	UnresolvedIdentifierValue identifier = (UnresolvedIdentifierValue) scope;
		// 	String id = identifier.getIdentifier();
		// 	if (id.equals(Resolver.PARENTTYPESYMBOL)) {
		// 		identifier.setResolved(identifier.getScope());
		// 	}
		// 	else if (identifier.getScope().isMemberDefinedRecursive(id)) {
		// 		identifier.setResolved(identifier.getScope().getDefinedMemberRecursive(id));
		// 	}
		// 	else {
		// 		runAgain = true;
		// 		unresolvableValues.add(identifier);
		// 		System.out.println("Couldnt' resolve Identifier " + identifier);
		// 		return;
		// 	}
		// }

		checkedValues.add(scope);


		

		if (scope instanceof EvaluateValue) {
			solve((EvaluateValue) scope);
		}
		else {
			if (scope.getType() == null) {
				System.out.println("ERROR: nulltype!" + scope);
				//runAgain = true;
			}
			else {
				System.out.println("Value " + scope + " of type '" + scope.getType() + "'.");
				solve(scope.getType(), scope);
			}
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

			System.out.println("  Checking Type " + t + ".");


			if (t instanceof UnresolvedType) {
				UnresolvedType u = (UnresolvedType) t;
				if (!u.isResolved()) {
					//special case
					if (u.getName().equals(PARENTTYPESYMBOL)) {
						u.setResolvedType(scope.getType());
						continue;
					}

					System.out.println("      trying to resolve " + u);
					if (resolve(u, scope)) {
						addType(types, u.getResolvedType());
					}
					else {
						System.out.println("      Couldn't resolve type " + u
						                   + " parent scope: " + scope.getScope());
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

		System.out.println("Searching type " + u + " in " + scope + ".");

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
