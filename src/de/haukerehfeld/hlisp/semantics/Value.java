package de.haukerehfeld.hlisp.semantics;

import java.util.*;

public interface Value {
	/**
	 * get parent scope
	 */
	public Value getScope();

	public <T> T runOnScope(ValueMethod<T> m);

	public Type getType();

	/** access members directly defined in this type */
	public void defineMember(String name, Value v);
	public Map<String, Value> getDefinedMembers();
	public boolean isMemberDefined(String v);
	public boolean isMemberDefinedRecursive(String v);
	public Value getDefinedMember(String v);
	public Value getDefinedMemberRecursive(String v);


	public interface ValueMethod<T> {
		public T run(Value v);
		public boolean success();
	}
}