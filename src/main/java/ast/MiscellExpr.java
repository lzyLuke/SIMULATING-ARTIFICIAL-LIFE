package ast;

import java.util.Random;

import interpret.Interpreter;
import interpret.InterpreterImpl;

/**
 * A representation of a variable expression: Variable_Name[Expression]
 *
 */
public class MiscellExpr implements Expr {

	String prefix;
	Expr expression;
	boolean isCommand = false;
	boolean memInUpdate = false;

	/**
	 * 
	 * @param prefix
	 *            the string prefix of this MiscellousExpression. Eg: NEARBY, MEM,
	 *            AHEAD....
	 * @param expression
	 *            the expression in NEARBY[expr], AHEAD[expr].... Note that SMELL
	 *            does not have a expr.
	 */
	public MiscellExpr(String prefix, Expr expression) {
		this.prefix = prefix;
		this.expression = expression;
	}

	/**
	 * Tests if the expression is a {@code Command} object.
	 */
	public void isCommand()
	{
		isCommand=true;
	}
	public void memInUpdate() {
		memInUpdate = true;
	}

	public Expr getExpr() {
		return expression;
	}

	@Override
	public Node nodeAt(int index)
	{
		index = index % size();
		assert (index >= 0 && index < size());
		if (index == 0)
			return this;
		index--;
		assert expression != null;
		return expression.nodeAt(index);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb)
	{
	
			sb.append(prefix.toLowerCase());
			if (expression != null) {
				sb.append("[");
				expression.prettyPrint(sb);
				sb.append("]");
			}
			return sb;

	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	@Override
	public int size()
	{
		if (expression != null) return 1 + expression.size();
		return 1;
	}

	@Override
	public String getType() {
		return "MiscellExpr";
	}

	@Override
	public int numChild() {
		if (expression != null)
			return 1;
		return 0;
	}

	@Override
	public Node getChild(int n) {

		return expression;

	}

	@Override
	public void setSubNode(int n, Node c) {
		assert n >= 0 && n < size();
		n--;
		if (n == 0) {
			if (c instanceof Expr) {
				expression = (Expr) c;
			}
			return;
		}
		expression.setSubNode(n, c);
	}

	@Override
	public Node copy() {

		if (expression instanceof Expr) {

			String newPrefix = new String(prefix);
			Expr newE = (Expr) expression.copy();
			MiscellExpr newMisc = new MiscellExpr(newPrefix, newE);
			if (isCommand)
				newMisc.isCommand();
			if (memInUpdate)
				newMisc.memInUpdate();
			return newMisc;
		}
		MiscellExpr newMisc = new MiscellExpr("SMELL", null);
		if (isCommand)
			newMisc.isCommand();
		if (memInUpdate)
			newMisc.memInUpdate();
		return newMisc;
	}

	@Override
	public boolean equals(Node n) {

		if (!(n instanceof MiscellExpr))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof MiscellExpr)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		if (c instanceof Expr)
			expression = (Expr) c;
	}

	@Override
	public void replacedBy(Node n) {
		assert n instanceof MiscellExpr;
		MiscellExpr temp = (MiscellExpr) n;
		if (memInUpdate)
			return;
		if (temp.expression != null && expression == null)
			return;
		if (temp.expression == null && expression != null)
			return;
		if (!temp.prefix.equals(prefix)) {
			prefix = temp.prefix;
		}
	}

	@Override
	/**
	 * randomly shuffle this node to another miscellousExpression
	 */
	public Expr shuffle() {
		if (isCommand == true)
			return this;

		Random rand = new Random();
		ast.Number newNum = new ast.Number(0);

		switch (rand.nextInt(6)) {
		case 0:
			prefix = "MEM";
			expression = newNum.shuffle();
			return this;

		case 1:
			prefix = "NEARBY";
			expression = newNum.shuffle();
			return this;

		case 2:
			prefix = "AHEAD";
			expression = newNum.shuffle();
			return this;

		case 3:
			prefix = "RANDOM";
			expression = newNum.shuffle();
			return this;

		case 4:
			prefix = "SMELL";
			expression = null;
			return this;

		case 5:
			return newNum.shuffle();

		default:
			return this;
		}
	}

	/**
	 * Randomly add a prefix to this expression
	 */
	public boolean insertExpr(Node n) {
		if (expression != null && expression.equals(n)) {
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
			Expr copyed = (Expr) (this.copy());
			expression = new MiscellExpr(prefix, copyed);
			return true;
		}
		return false;
	}

	public int accept(Interpreter i)
	{
		int value=0;
		int index=0;
		if(prefix.equals("SMELL"))
			value= i.getCritter().getSmell();
		
		else if(prefix.equals("MEM")) {
			index=i.eval(expression);
			value=i.getCritter().getMem(index);
		}
		else if(prefix.equals("NEARBY")){
			index=i.eval(expression);
			value=i.getCritter().getNearby(index);
			
		}
		else if(prefix.equals("AHEAD")){
			index=i.eval(expression);
			value=i.getCritter().getAhead(index);
			
		}
		else if(prefix.equals("RANDOM")){
			index=i.eval(expression);
			value=i.getCritter().getRandom(index);
		}

		return value;
	}
	}
