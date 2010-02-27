package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class Root extends LambdaFunction {
	private final static String RUNBODY
	= "Root.List argList = List();\n"
	    + "\n"
	    + "System.out.println(\"ROOT\");"
	    + "for (java.lang.String arg: args) {\n"
	    + "    argList._hlisp_escape_plusequal(String(arg))._hlisp_run();\n"
	    + "}\n"
	    + "return null;";
	
	public Root() {
		super(new RootType(),
		      null,
		      new ArrayList<String>() {{ add("args"); }});
		add(new NativeValue(this, VoidType.create(), RUNBODY));
	}
}