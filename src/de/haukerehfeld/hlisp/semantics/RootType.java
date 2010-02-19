package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class RootType extends HashType {
	public RootType() {
		super(null,
		      "Root", new ArrayList<Parameter>() {{
		            add(new Parameter(new UnresolvedType("String[]"), "args"));
		        }},
		      new ResolvedBody(),
		    new UnresolvedType("Void"));

		VoidType voidType = new VoidType(this);
		defineType(voidType);

		defineType(new HashType(this, "List",
		                        new ArrayList<Parameter>() {{
		                            }},
		                        new ResolvedBody(),
		                        voidType));
		                        
		                        
	}
	
	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		return emitter.emit(this);
	}
}