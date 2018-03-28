package ast;

import interpret.Interpreter;

public class UnaryExpr implements Expr {

	protected String op;
	protected Expr e;

	/**
	 * 
	 * @param op
	 *            the UnaryExpr's operator, and it is always a "-"
	 * @param e
	 *            the expression of UnaryExpr
	 */
	public UnaryExpr(String op, Expr e) {
		this.op = op;
		this.e = e;
	}

	public Node nodeAt(int index) {
		index = index % size();
		assert (index >= 0 && index < size());
		if (index == 0)
			return this;
		index--;
		return e.nodeAt(index);
	}

	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(" " + op);
		return e.prettyPrint(sb);
	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	public int size() {
		return 1 + e.size();
	}

	@Override
	public String getType() {
		return "UnaryExpr";
	}

	@Override
	public int numChild() {
		return 1;
	}

	@Override
	public Node getChild(int n) {
		return e;
	}

	@Override
	public void setSubNode(int n, Node c) {
		assert n >= 0 && n < size();
		n--;
		if (n == 0) {
			if (c instanceof Expr) {
				e = (Expr) c;
			}
			return;
		}
		e.setSubNode(n, c);
	}

	@Override
	public Node copy() {
		if (e instanceof Expr) {
			String newOp = new String(op);
			Expr newE = (Expr) e.copy();
			return new UnaryExpr(newOp, newE);
		}
		return null;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof UnaryExpr))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof UnaryExpr || n instanceof Number)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		if (c instanceof Expr)
			e = (Expr) c;
	}

	@Override
	/**
	 * just return the expr part of the UnaryExpr, delete one "-".
	 */
	public Expr shuffle() {
		return e;
	}

	@Override
	public void replacedBy(Node n) {
		assert n instanceof UnaryExpr;
		if (!((UnaryExpr) n).op.equals(op)) {
			op = ((UnaryExpr) n).op;
		}
	}

	public int accept(Interpreter i)
	{
		int num= i.eval(e);
		return -num;
	}
}
