package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class RootType extends NamedType {
	private final static String[] PARAMETERS = { "args" };

	private final static String RUNBODY
	= "Root.List argList = List();\n"
	    + "\n"
	    + "System.out.println(\"ROOT\");"
	    + "for (java.lang.String arg: args) {\n"
	    + "    argList._hlisp_escape_plusequal(String(arg))._hlisp_run();\n"
	    + "}\n"
	    + "return null;";
	
	public RootType() {
		super("Root",
		      null,
		      VoidType.create());
		setParameterTypes(new ArrayList<Type>() {{
		            add(new NativeType(RootType.this, "java.lang.String[]"));
		        }});
		setParameterNames(Arrays.asList(PARAMETERS));

		Type void_ = VoidType.create(this);
		defineType(void_);

		setInstruction(new NativeInstruction(void_, RUNBODY));
	}
}