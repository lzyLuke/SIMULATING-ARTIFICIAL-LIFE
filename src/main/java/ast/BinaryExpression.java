package ast;

import java.util.Random;

import interpret.Interpreter;

/**
 * A representation of a binary Expression consisting of term (| addop term |)*
 * It can also represent a term node like factor (| mulop factor |)*
 */
public class BinaryExpression implements Expr {
	Expr l;
	Expr r;
	String op;

	/**
	 * 
	 * @param l
	 *            the left node ( a term or a factor)
	 * @param op
	 *            the Operator, depending whether its a term node or a expr node
	 * @param r
	 *            the right node (a term or a factor, it is the same class as
	 *            {@code l} )
	 */
	public BinaryExpression(Expr l, String op, Expr r) {
		this.l = l;
		this.r = r;
		this.op = op;
	}

	public Node nodeAt(int index) {
		index = index % size();
		assert (index >= 0 && index < size());
		if (index == 0)
			return this;
		index--;
		if (index < l.size())
			return l.nodeAt(index);
		index -= l.size();
		return r.nodeAt(index);
	}

	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("(");
		if (op != null && r != null) {
			l.prettyPrint(sb);
			sb.append(" " + op + " ");
			return r.prettyPrint(sb).append(")");
		} else {
			return l.prettyPrint(sb).append(")");
		}

	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	public int size() {
		if (r == null)
			return 1 + l.size();
		if (l == null)
			return 1;
		return 1 + l.size() + r.size();
	}

	@Override
	public String getType() {
		return "BinaryExpression";
	}

	@Override
	public int numChild() {
		if (r == null)
			return 1;
		return 2;
	}

	@Override
	public Node getChild(int n) {
		if (n % 2 == 0)
			return l;
		return r;
	}

	@Override
	public void setSubNode(int n, Node c) {
		assert n >= 0 && n < size();
		n--;
		if (n < l.size()) {
			if (n == 0) {
				if (c instanceof Expr) {
					l = (Expr) c;
				}
				return;
			}
			l.setSubNode(n, c);
		} else {
			n -= l.size();
			if (n == 0) {
				if (c instanceof Expr) {
					r = (Expr) c;
				}
				return;
			}
			r.setSubNode(n, c);
		}
	}

	@Override
	public Node copy() {
		if (op == null) {
			if (l instanceof Expr) {
				Expr newL = (Expr) l.copy();
				return new BinaryExpression(newL, null, null);
			}
		} else {
			String newOp = new String(op);
			if (l instanceof Expr && r instanceof Expr) {
				Expr newL = (Expr) l.copy();
				Expr newR = (Expr) r.copy();
				return new BinaryExpression(newL, newOp, newR);
			}
		}
		return null;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof BinaryExpression))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof BinaryExpression || n instanceof Number)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		assert n >= 0;
		n %= 2;
		if (n % 2 == 0 && c instanceof Expr)
			l = (Expr) c;
		else if (n % 2 == 1 && c instanceof Expr)
			r = (Expr) c;
	}

	@Override
	/**
	 * shuffle the Operator and the {@code l} and {@code r} node.
	 * 
	 * @return the shuffled node
	 */
	public Expr shuffle() {

		Random rand = new Random();
		ast.Number newNum = new ast.Number(1);
		int two = rand.nextInt(2);
		int three = rand.nextInt(3);
		if (l != null)
			l = l.shuffle();
		else {
			l = newNum.shuffle();
			return this;
		}

		if (op.equals("+") || op.equals("-"))
			if (two == 0)
				op = "+";
			else if (two == 1)
				op = "-";

		if (op.equals("*") || op.equals("/") || op.equals("mod"))
			if (three == 0)
				op = "*";
			else if (three == 1)
				op = "/";
			else if (three == 2)
				op = "mod";

		if (r != null)
			r = r.shuffle();

		return this;

	}

	/**
	 * Randomly add a prefix for the expression of this node, return true if
	 * successful, false if not. The expression to be added must be the same as node
	 * {@code n}.
	 * 
	 * @param n
	 *            the node that will be added a prefix
	 * @return true if successful insert a prefix, false if not.
	 */
	public boolean insertExpr(Node n) {
		if (l.equals(n)) {
			Random rand = new Random();
			int num = rand.nextInt(4);
			String prefix;
			switch (num) {
			case 0:
				prefix = "MEM";
				break;
			case 1:
				prefix = "NEARBY";
				break;
			case 2:
				prefix = "AHEAD";
				break;
			case 3:
				prefix = "RANDOM";
				break;
			default:
				prefix = "MEM";
				break;
			}
			l = new MiscellExpr(prefix, l);
			return true;
		} else if (r.equals(n)) {
			Random rand = new Random();
			int num = rand.nextInt(4);
			String prefix;
			switch (num) {
			case 0:
				prefix = "MEM";
				break;
			case 1:
				prefix = "NEARBY";
				break;
			case 2:
				prefix = "AHEAD";
				break;
			case 3:
				prefix = "RANDOM";
				break;
			default:
				prefix = "MEM";
				break;
			}
			r = new MiscellExpr(prefix, r);
			return true;
		}
		return false;

	}

	@Override
	public void replacedBy(Node n) {
		assert n instanceof BinaryExpression;
		BinaryExpression temp = (BinaryExpression) n;
		if (temp.op != null && op != null && (!temp.op.equals(op))) {
			op = temp.op;
		}
	}

	public int accept(Interpreter i)
	{
		int leftNum = i.eval(l);
		int rightNum = i.eval(r);
		int value=0;
		if(op==null)
		{
			value=leftNum;
		}
		else if(op.equals("+"))
		{
			value=leftNum+rightNum;
		}
		else if(op.equals("-"))
		{
			value=leftNum-rightNum;
		}
		else if(op.equals("*"))
		{
			value=leftNum*rightNum;
		}
		else if(op.equals("/"))
		{
			if(rightNum!=0)
			value=leftNum/rightNum;
			else value=0;
		}
		else if(op.equals("mod"))
		{
			if(rightNum!=0)
				value=leftNum%rightNum;
			else 
				value=0;
		}
		
		return value;
	}

}
