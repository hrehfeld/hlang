package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class RootType extends NamedType {
	private final static String[] PARAMETERS = { "args" };

	private final static String RUNBODY
	= "List argList = new List();\n"
	    + "\n"
	    + "for (java.lang.String arg: args) {\n"
	    + "    argList._hlisp_escape_plusequal().create(String().create(arg)).call();\n"
	    + "}\n"
	    + "main().create(argList).call();";
	
	public RootType() {
		super("Root",
		      null,
		      VoidType.create(),
		      new ArrayList<Signature>() {{
		              add(new NativeSignature("java.lang.String[]"));
		          }},
		      Arrays.asList(PARAMETERS));
		final Type void_ = VoidType.create(this);
		defineType(void_);

		setInstruction(new ListInstruction(new ArrayList<Instruction>() {{
		                add(new NativeInstruction(void_, RUNBODY));
		                add(new VoidInstruction());
		            }}));
	}
}