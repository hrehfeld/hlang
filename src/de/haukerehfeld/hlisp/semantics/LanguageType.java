package de.haukerehfeld.hlisp.semantics;

import java.util.*;
import de.haukerehfeld.hlisp.EqualsUtil;
import de.haukerehfeld.hlisp.HashUtil;

public class LanguageType extends AbstractType {
	private Body body;

	public LanguageType(Type parent, Body body, Type returnType) {
		super(parent, returnType);
		this.body = body;
	}

	/**
	 * get body
	 */
	@Override public Body getBody() { return body; }
    
/**
 * set body
 */
	public void setBody(Body body) { this.body = body; }

	
	@Override public String emit(de.haukerehfeld.hlisp.JavaEmitter emitter) { return emitter.emit(this); }

	@Override public boolean equals(Object o) {
		if (this == o) { return true; }
		if (!(o instanceof LanguageType)) { return false; }

		LanguageType e = (LanguageType) o;
		return super.equals(o)
		    && EqualsUtil.equal(body, e.body);
	}

	@Override public int hashCode() {
		return HashUtil.hash(super.hashCode(), body);
	}
}