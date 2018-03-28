package ast;

import java.util.Random;

import interpret.Interpreter;

/**
 * 
 * A representation of integer value.
 */
public class Number implements Expr {
	int value;
	
	/**
	 * Create an AST representation of integer v
	 * @param v integer value of the node.
	 */
	public Number(int v){
		this.value=v;
	}
	
	@Override
	public Node nodeAt(int index)
	{
		// end of ast
		assert index == 0;
		return this;
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb)
	{
		if (value < 0) {
			return sb.append(" -" + Integer.toString(-value));
		}
		return sb.append(Integer.toString(value));
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(value);
	}
	
	@Override
	public int size()
	{
		return 1;
	}


	@Override
	public String getType() {
		return "Number";
	}
	
	/**
	 * A getter that returns the value of the {@code Number}.
	 * Used by rule mutation type 4.
	 * 
	 * @return value of the {@code Number} object
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * A setter that sets the value of the {@code Number}.
	 * Used by rule mutation type 4.
	 * 
	 * @param i: the value to be set to
	 */
	public void setValue(int i) {
		this.value = i;
	}


	@Override
	public int numChild() {
		return 0;
	}


	@Override
	public Node getChild(int n) {
		return null;
	}
	
	@Override
	/**
	 * gererate a random number.
	 */
	public Expr shuffle()
	{
		Random rand = new Random();
		value=Integer.MAX_VALUE / rand.nextInt();
		if(value<0) value=-value;
		return this;
	}

	@Override
	public void setSubNode(int n, Node c) {
		// do nothing
	}


	@Override
	public Number copy() {
		int v = value;
		return new Number(v);
	}


	@Override
	public boolean equals(Node n) {
		if (n instanceof Number) {
			if (((Number) n).value == value) return true;
		}
		return false;
	}


	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Number) return true;
		return false;
	}


	@Override
	public void setChild(int n, Node c) {
		// do nothing
	}

	

	@Override
	public void replacedBy(Node n) {
		Random rand = new Random();
		int adjust = Integer.MAX_VALUE / rand.nextInt();
		// TODO check if adjustment is legal
		value += adjust;
	}

	public int accept(Interpreter i)
	{
		return value;
	}
}