package de.haukerehfeld.hlisp.parser;

public class AstFloat extends AstNode implements AstValue<Float> {
	public AstFloat(int id) {
		super(id);
	}
	
	public AstFloat(HLispParser p, int id) {
		super(p, id);
	}

	public void parse(String t) {
		setValue(Float.valueOf(t));
	}

	/**
	 * get value
	 */
	public Float getValue() { return (Float) value; }
    
/**
 * set value
 */
	public void setValue(Float value) { this.value = value; }

	@Override public Object jjtAccept(HLispParserVisitor v, Object data) throws
		de.haukerehfeld.hlisp.semantics.SemanticException {
		return v.visit(this, data);
	}
}
