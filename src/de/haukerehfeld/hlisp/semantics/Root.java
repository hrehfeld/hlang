package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class Root extends LambdaFunction {
	private final static String[] PARAMETERS = { "args" };
	private final static String RUNBODY
	= "Root.List argList = List();\n"
	    + "\n"
	    + "System.out.println(\"ROOT\");"
	    + "for (java.lang.String arg: args) {\n"
	    + "    argList._hlisp_escape_plusequal(String(arg))._hlisp_run();\n"
	    + "}\n"
	    + "return null;";
	
	public Root() throws SemanticException {
		super(new RootType(),
		      null,
		      Arrays.asList(PARAMETERS));
		add(new NativeValue(this, VoidType.create(), RUNBODY));
		add(new UnresolvedIdentifierValue(this, "this"));

		Type void_ = VoidType.create(this);
		defineMember(VoidType.NAME, new NativeValue(this, void_, "return new Void();"));

	}
}