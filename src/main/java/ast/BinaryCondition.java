package ast;

import interpret.Interpreter;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */
public class BinaryCondition implements Condition {

	Condition l;
	Operator op;
	Condition r;

	/**
	 * Create an AST representation of l op r.
	 * 
	 * @param l
	 * @param op
	 * @param r
	 */
	public BinaryCondition(Condition l, Operator op, Condition r) {
		this.l = l;
		this.op = op;
		this.r = r;
	}

	@Override
	public int size() {
		if (r == null)
			return 1 + l.size();
		return 1 + l.size() + r.size();
	}

	@Override
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

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (op != null && r != null) {
			l.prettyPrint(sb);
			if (op.equals(BinaryCondition.Operator.OR))
				sb.append(" or ");
			else if (op.equals(BinaryCondition.Operator.AND))
				sb.append(" and ");
			return r.prettyPrint(sb);
		} else {
			return l.prettyPrint(sb);
		}
	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	/**
	 * An enumeration of all possible binary condition operators.
	 */
	public enum Operator {
		OR, AND;

		public Operator copy() {
			if (this.equals(OR))
				return OR;
			if (this.equals(AND))
				return AND;
			return null;
		}
	}

	@Override
	public String getType() {
		return "BinaryCondition";
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
				if (c instanceof Condition) {
					l = (Condition) c;
				}
				return;
			}
			l.setSubNode(n, c);
		} else {
			n -= l.size();
			if (n == 0) {
				if (c instanceof Condition) {
					r = (Condition) c;
				}
				return;
			}
			r.setSubNode(n, c);
		}
	}

	@Override
	public Node copy() {
		Operator newOp = op.copy();
		if (l instanceof Condition && r instanceof Condition) {
			Condition newL = (Condition) l.copy();
			Condition newR = (Condition) r.copy();
			return new BinaryCondition(newL, newOp, newR);
		}
		return null;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof BinaryCondition))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Relation || n instanceof BinaryCondition)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		assert n >= 0;
		n %= 2;
		if (n % 2 == 0 && c instanceof Condition)
			l = (Condition) c;
		else if (n % 2 == 1 && c instanceof Condition)
			r = (Condition) c;
	}

	@Override // BinaryCondition can not be shuffled.
	public Expr shuffle() {
		return this;
	}

	@Override
	public void replacedBy(Node n) {
		assert n instanceof BinaryCondition;
		BinaryCondition temp = (BinaryCondition) n;
		if (temp.op != null && op != null && (!temp.op.equals(op))) {
			op = temp.op;
		}
	}
	
	public boolean accept(Interpreter i)
	{
		switch(op)
		{
		case OR:	  return (l.accept(i) || r.accept(i));	
		case AND: return (l.accept(i) && r.accept(i));
		default : return l.accept(i);
		
		}
		
	}

}
