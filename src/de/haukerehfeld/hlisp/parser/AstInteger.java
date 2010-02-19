package de.haukerehfeld.hlisp.parser;

public class AstInteger extends AstNode {
	public AstInteger(int id) {
		super(id);
	}
	
	public AstInteger(HLispParser p, int id) {
		super(p, id);
	}

	public void parse(String t) {
		setValue(Integer.valueOf(t));
	}

	/**
	 * get value
	 */
	public Integer getValue() { return (Integer) value; }
    
/**
 * set value
 */
	public void setValue(Integer value) { this.value = value; }

	@Override public Object jjtAccept(HLispParserVisitor v, Object data) throws
		de.haukerehfeld.hlisp.semantics.SemanticException {
		return v.visit(this, data);
	}
}
