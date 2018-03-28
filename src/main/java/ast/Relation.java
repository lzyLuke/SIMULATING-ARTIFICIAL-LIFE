package ast;

import java.util.Random;

import interpret.Interpreter;

/**
 * A representation of an Relation Node
 */
public class Relation implements Condition {
	Expr left;
	String op;
	Expr right;

	/**
	 * 
	 * @param left
	 *            the left expression
	 * @param op
	 *            the operator
	 * @param right
	 *            the right expression
	 */
	public Relation(Expr left, String op, Expr right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}

	/**
	 * 
	 * @param condition
	 *            the only condition of this relation.
	 */
	public Relation(Expr condition) {
		this.left = condition;
	}

	@Override
	public int size() {
		if (right == null)
			return 1 + left.size();
		return 1 + left.size() + right.size();
	}

	@Override
	public Node nodeAt(int index) {
		index = index % size();
		assert (index >= 0 && index < size());
		if (index == 0)
			return this;
		index--;
		if (index < left.size())
			return left.nodeAt(index);
		index -= left.size();
		return right.nodeAt(index);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (right != null) {
			left.prettyPrint(sb);
			sb.append(" " + op + " ");
			return right.prettyPrint(sb);
		}
		sb.append("{");
		left.prettyPrint(sb);
		return sb.append("}");
	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	public enum Operator {
		BiggerEqual, SmallerEqual, Equal, Bigger, Smaller, Notequal;
	}

	@Override
	public String getType() {
		return "Relation";
	}

	@Override
	public int numChild() {
		if (right == null)
			return 1;
		return 2;
	}

	@Override
	public Node getChild(int n) {
		if (n % 2 == 0)
			return left;
		return right;
	}

	@Override
	public void setSubNode(int n, Node c) {
		assert n >= 0 && n < size();
		n--;
		if (n < left.size()) {
			if (n == 0) {
				if (c instanceof Expr) {
					left = (Expr) c;
				}
				return;
			}
			left.setSubNode(n, c);
		} else {
			n -= left.size();
			if (n == 0) {
				if (c instanceof Expr) {
					right = (Expr) c;
				}
				return;
			}
			right.setSubNode(n, c);
		}
	}

	@Override
	public Node copy() {
		if (left != null && right != null) {
			if (left instanceof Expr && right instanceof Expr) {
				String newOp = new String(op);
				Expr newL = (Expr) left.copy();
				Expr newR = (Expr) right.copy();
				return new Relation(newL, newOp, newR);
			}
		}

		if (left != null && right == null) {
			Expr newL = (Expr) left.copy();
			return new Relation(newL);
		}

		return null;
	}

	@Override
	public boolean equals(Node n) {
		try {
			if (!(n instanceof Relation))
				return false;
			return this.toString().equals(n.toString());
		} catch (NullPointerException e) {
			StringBuilder sb = new StringBuilder();
			n.prettyPrint(sb);
			System.out.println(sb);
			System.out.println("Null pointer" + op);
			System.out.println(right == null);
			System.out.println(((Relation) n).right == null);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Condition)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		assert n >= 0;
		n %= 2;
		if (n % 2 == 0 && c instanceof Expr)
			left = (Expr) c;
		else if (n % 2 == 1 && c instanceof Expr)
			right = (Expr) c;
	}

	@Override

	public Expr shuffle() {
		Random rand = new Random();
		if (left != null)
			left = left.shuffle();
		if (right != null)
			right = right.shuffle();
		switch (rand.nextInt(6)) {
		case 0:
			op = "<";
			break;
		case 1:
			op = "<=";
			break;
		case 2:
			op = "=";
			break;
		case 3:
			op = ">=";
			break;
		case 4:
			op = ">";
			break;
		case 5:
			op = "!=";
			break;

		}
		return this;
	}

	public void replacedBy(Node n) {
		assert n instanceof Relation;
		if (op == null || ((Relation) n).op == null)
			return;
		if (!((Relation) n).op.equals(op)) {
			op = ((Relation) n).op;
		}
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
		if (left.equals(n)) {
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
			left = new MiscellExpr(prefix, left);
			return true;
		} else if (right != null && right.equals(n)) {
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
			right = new MiscellExpr(prefix, right);
			return true;
		}
		return false;

	}

	@Override
	public boolean accept(Interpreter i)
	{
		if(left instanceof Condition)
			return ((Condition) left).accept(i);
		
		
		int leftNum = i.eval(left);
		int rightNum= i.eval(right);
		
		if(op.equals("<"))
		{
			return (leftNum<rightNum);
		}
		else if(op.equals("<="))
		{
			return (leftNum<=rightNum);
		}
		else if(op.equals("="))
		{
			return (leftNum==rightNum);
		}
		else if(op.equals(">="))
		{
			return (leftNum>=rightNum);
		}
		else if(op.equals(">"))
		{
			return (leftNum>rightNum);
		}
		else if(op.equals("!="))
		{
			return (leftNum!=rightNum);
		}
		else
		{
			return true;
		}
		
	}
}
