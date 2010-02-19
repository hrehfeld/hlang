package de.haukerehfeld.hlisp.parser;

public class AstIdentifier extends AstNode {
	private String name;
	
	public AstIdentifier(int id) {
		super(id);
	}

	public AstIdentifier(HLispParser p, int id) {
		super(p, id);
	}

	/**
	 * get name
	 */
	public String getName() { return name; }
    
    /**
     * set name
     */
	public void setName(String name) { this.name = name; }
	

/** Accept the visitor. **/
	public Object jjtAccept(HLispParserVisitor visitor, Object data)
		throws de.haukerehfeld.hlisp.semantics.SemanticException {
		return visitor.visit(this, data);
	}
}
