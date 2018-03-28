package ast;

import java.util.Random;

import interpret.Interpreter;

public class Update implements Expr {
	Expr left;
	Expr right;
	boolean onlyNode = true;
	String category = "Update";

	public Update(Expr left, Expr right) {
		this.left = left;
		this.right = right;
		if (this.left instanceof MiscellExpr) {
			((MiscellExpr) left).memInUpdate();
		}
	}

	@Override
	public int size() {
		return 1 + this.left.size() + this.right.size();
	}

	@Override
	public Node nodeAt(int index) {
		if (index == 0)
			return this;
		index = index % this.size();
		assert (index >= 0 && index < size());
		if (index <= this.left.size())
			return this.left.nodeAt(index - 1);
		else
			return this.right.nodeAt(index - 1 - this.left.size());
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		left.prettyPrint(sb);
		sb.append(" := ");
		return right.prettyPrint(sb);
	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	public Node getChild(int index) {
		if (index % 2 == 0)
			return left;
		return right;
	}

	@Override
	public String getType() {
		return "Update";
	}

	@Override
	public int numChild() {
		return 2;
	}

	@Override
	public void setSubNode(int n, Node c) {
		assert n >= 0 && n < size();
		n--;
		if (n < left.size()) {
			if (n == 0) {
				if (c instanceof MiscellExpr) {
					if (((MiscellExpr) c).prefix.toLowerCase().equals("mem"))
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
		if (left instanceof Expr && right instanceof Expr) {
			Expr newLeft = (Expr) left.copy();
			Expr newRight = (Expr) right.copy();
			Update u = new Update(newLeft, newRight);
			if (!onlyNode)
				u.onlyNode = false;
			return u;
		}
		return null;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof Update))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Update)
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
	public void replacedBy(Node n) {
		System.out.println("Cannot transform an Update node.");
	}

	@Override
	public Expr shuffle() {
		left = left.shuffle();
		right = right.shuffle();
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

		if (right.equals(n)) {
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
	
	public void accept(Interpreter i)
	{
		int leftNum=i.eval( ((MiscellExpr)left).getExpr() );
		int rightNum=i.eval(right);
		i.getCritter().changeMem(leftNum, rightNum);
		
	}

}
