package de.haukerehfeld.hlisp;

import de.haukerehfeld.hlisp.parser.*;
import de.haukerehfeld.hlisp.semantics.SemanticException;

class PrintAstVisitor implements HLispParserVisitor {
	public Object visit(SimpleNode n, Object l) {
		int level = 0;
		if (l instanceof Integer) {
			level = (Integer) l;
		}

		
		for (int j = 0; j < level; ++j)
		{
			System.out.print("_ ");
		}
			
		System.out.print(n.getClass() + " (" + n + ")");
		if (n instanceof SimpleNode) {
			Object v = ((SimpleNode) n).jjtGetValue();
			if (v != null) {
				System.out.print(": " + v);
			}
		}
		System.out.print("\n");

		try {
			n.childrenAccept(this, level + 1);
		}
		catch (SemanticException e) {}
		return null;
	}
	public Object visit(AstRoot node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstDontEval node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstIdentifier node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstDefine node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstBody node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstInstantiate node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstFloat node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstString node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstInteger node, Object data) { visit((SimpleNode) node, data); return null;}
	public Object visit(AstList node, Object data) { visit((SimpleNode) node, data); return null;}
}
