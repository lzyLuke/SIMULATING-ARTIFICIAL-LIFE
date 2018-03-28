package ast;

import java.util.Random;
import interpret.*;
/**
 * A representation of an Action Node
 */
public class Action implements Expr {
	String act;
	Expr expr;
	Boolean onlyNode = true;

	/**
	 * 
	 * @param act
	 *            the string representation of the action prefix
	 * @param expr
	 *            especially for tag[expr] and serve[expr]
	 */
	public Action(String act, Expr expr) {
		this.act = act;
		this.expr = expr;

	}

	public Node nodeAt(int index) {
		index = index % size();
		assert (index >= 0 && index < size());
		if (index == 0)
			return this;
		index--;
		return expr.nodeAt(index);
	}

	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(act);
		if (expr != null) {
			sb.append("[");
			expr.prettyPrint(sb);
			sb.append("]");
		}
		return sb;
	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	@Override
	public int size() {
		if (expr != null)
			return 1 + expr.size();
		return 1;
	}

	@Override
	public String getType() {
		return "Action";
	}

	@Override
	public int numChild() {
		if (expr == null)
			return 0;
		return 1;
	}

	@Override
	public Node getChild(int n) {
		return expr;
	}

	@Override
	public void setSubNode(int n, Node c) {
		if (n < 0 || n >= size()) {
			System.out.println("");
		}
		assert n >= 0 && n < size();
		n--;
		if (n == 0) {
			if (c instanceof Expr) {
				expr = (Expr) c;
			}
			return;
		}
		if (expr != null)
			expr.setSubNode(n, c);
	}

	@Override
	public Node copy() {
		if (expr instanceof Expr) {
			Expr newE = (Expr) expr.copy();
			String newA = new String(act);
			Action a = new Action(newA, newE);
			if (!onlyNode)
				a.onlyNode = false;
			return a;
		} else if (expr == null) {
			String newA = new String(act);
			Action a = new Action(newA, null);
			if (!onlyNode)
				a.onlyNode = false;
			return a;
		}
		return null;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof Action))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Update || n instanceof Action)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		if (c instanceof Expr)
			expr = (Expr) c;
	}

	@Override // Action can be shuffled.
	public Expr shuffle() {
		Random rand = new Random();
		ast.Number num = new ast.Number(0);
		num.shuffle();
		int kind = rand.nextInt(12); // There are 12 kinds of action, we shuffle them with equal probability.
		switch (kind) {
		case 0:
			act = "wait";
			expr = null;
			return this;

		case 1:
			act = "forward";
			expr = null;
			return this;

		case 2:
			act = "backward";
			expr = null;
			return this;

		case 3:
			act = "left";
			expr = null;
			return this;

		case 4:
			act = "right";
			expr = null;
			return this;

		case 5:
			act = "eat";
			expr = null;
			return this;

		case 6:
			act = "attack";
			expr = null;
			return this;

		case 7:
			act = "grow";
			expr = null;
			return this;

		case 8:
			act = "bud";
			expr = null;
			return this;

		case 9:
			act = "mate";
			expr = null;
			return this;

		case 10:
			act = "tag";
			expr = num.shuffle();
			return this;

		case 11:
			act = "serve";
			expr = num.shuffle();
			return this;

		default:
			return this;
		}

	}

	@Override
	public void replacedBy(Node n) {
		assert n instanceof Action;
		Action temp = (Action) n;
		if (temp.expr != null && expr != null && (!temp.act.equals(act))) {
			act = temp.act;
		} else if (temp.expr == null && expr == null && (!temp.act.equals(act))) {
			act = temp.act;
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

		if (expr != null && expr.equals(n)) {
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
			expr = new MiscellExpr(prefix, expr);
			return true;
		}
		return false;

	}
	public Outcome accept(Interpreter i)
	{
		  if(expr==null)
		  return new OutcomeImpl(act,-1);
		  else
		return new OutcomeImpl(act,i.eval(expr));
		  
	}
	
	
	
	
}
