package de.haukerehfeld.hlisp.parser;

public class AstString extends AstNode {
	public AstString(int id) {
		super(id);
	}
	
	public AstString(HLispParser p, int id) {
		super(p, id);
	}

	public void parse(String t) {
		setValue(t);
	}

	/**
	 * get value
	 */
	public String getValue() { return (String) value; }
    
/**
 * set value
 */
	public void setValue(String value) { this.value = value; }

	@Override public Object jjtAccept(HLispParserVisitor v, Object data) throws
		de.haukerehfeld.hlisp.semantics.SemanticException {
		return v.visit(this, data);
	}
}
