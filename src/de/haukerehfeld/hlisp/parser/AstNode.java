package de.haukerehfeld.hlisp.parser;

import java.util.Iterator;

public abstract class AstNode extends SimpleNode implements Iterable<AstNode>  {
	public int beginLine, endLine, beginColumn, endColumn;

	public AstNode(int i) {
		super(i);
	}

	public AstNode(HLispParser p, int i) {
		super(p, i);
	}

	public Iterator<AstNode> iterator() {
		return new AstNodeIterator();
	}

	class AstNodeIterator implements Iterator<AstNode> {
			private int i = 0;
			
			public boolean hasNext() { return i < jjtGetNumChildren(); }
			public AstNode next() { return (AstNode) jjtGetChild(i++); }
			public void remove() { throw new java.lang.UnsupportedOperationException(); }
		}

	@Override public abstract Object jjtAccept(HLispParserVisitor v, de.haukerehfeld.hlisp.semantics.Value data) throws
	    de.haukerehfeld.hlisp.semantics.SemanticException;


	public boolean isEmpty() {
		return jjtGetNumChildren() < 1;
	}

	@Override public String toString() {
		return getClass().getSimpleName();
	}

	public void setPosition(Token t) {
		this.beginLine = t.beginLine;
		this.endLine = t.endLine;
		this.beginColumn = t.beginColumn;
		this.endColumn = t.endColumn;
	}
}