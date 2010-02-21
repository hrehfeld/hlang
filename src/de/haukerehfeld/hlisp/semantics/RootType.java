package de.haukerehfeld.hlisp.semantics;

import java.util.*;


public class RootType extends ParametricType implements Type {
	public RootType() {
		super(null,
		      new ResolvedBody(),
		      new UnresolvedType("Void"),
		      new ArrayList<Parameter>() {{
		              add(new Parameter(new UnresolvedType("List"), "args"));
		          }}
		    );

		final AbstractNativeType objectType = new AbstractNativeType(this, null, "Object", "Object");
		objectType.setReturnType(objectType);

		final VoidType voidType = new VoidType(this);
		defineType(voidType);

		final StringNativeType stringType = new StringNativeType(this);
		defineType(stringType);

		final ListNativeType listType = new ListNativeType(this);
		defineType(listType);


		defineType(new FullType(this,
		                        new ResolvedBody(),
		                        voidType,
		                        new ArrayList<Parameter>() {{
		                                add(new Parameter(stringType, "value"));
		                            }},
		                        "String"
		               ));
		                        
		setReturnType(voidType);
		                        
	}

	@Override public String getName() { return "Root"; }
	
	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) {
		return emitter.emit(this);
	}
}