package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class RootType extends NamedType {
	private final static String[] PARAMETERS = { "args" };

	private final static String RUNBODY
	= "List argList = new List();\n"
	    + "\n"
	    + "for (java.lang.String arg: args) {\n"
	    + "    argList._hlisp_escape_plusequal(new String(arg))._hlisp_run();\n"
	    + "}";
	
	public RootType() {
		super("Root",
		      null,
		      VoidType.create());
		setParameterTypes(new ArrayList<Type>() {{
		            add(new NativeType(RootType.this, "java.lang.String[]"));
		        }});
		setParameterNames(Arrays.asList(PARAMETERS));

		final Type void_ = VoidType.create(this);
		defineType(void_);

		setInstruction(new ListInstruction(new ArrayList<Instruction>() {{
		                add(new NativeInstruction(void_, RUNBODY));
		                add(new VoidInstruction());
		            }}));
	}
}