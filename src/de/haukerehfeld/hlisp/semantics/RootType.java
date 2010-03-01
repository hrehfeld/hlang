package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class RootType extends AnonymousType {
	private final static String RUNBODY
	= "Root.List argList = List();\n"
	    + "\n"
	    + "System.out.println(\"ROOT\");"
	    + "for (java.lang.String arg: args) {\n"
	    + "    argList._hlisp_escape_plusequal(String(arg))._hlisp_run();\n"
	    + "}\n"
	    + "return null;";
	
	public RootType() {
		super(null,
		      VoidType.create(),
		      true,
		      new ArrayList<Type>() {{
		              add(new NativeType("java.lang.String[]"));
		          }}
		    );
		setReturnType(this);
	}
}