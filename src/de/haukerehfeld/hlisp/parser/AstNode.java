package de.haukerehfeld.hlisp.parser;

import java.util.Iterator;

public abstract class AstNode extends SimpleNode implements Iterable<Node>  {
	public AstNode(int i) {
		super(i);
	}

	public AstNode(HLispParser p, int i) {
		super(p, i);
	}

	public Iterator<Node> iterator() {
		return new AstNodeIterator();
	}

	class AstNodeIterator implements Iterator<Node> {
			private int i = 0;
			
			public boolean hasNext() { return i < jjtGetNumChildren(); }
			public Node next() { return jjtGetChild(i++); }
			public void remove() { throw new java.lang.UnsupportedOperationException(); }
		}

	@Override public abstract Object jjtAccept(HLispParserVisitor v, Object data) throws
	    de.haukerehfeld.hlisp.semantics.SemanticException;
	
}