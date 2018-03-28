package ast;

/**
 * A representation of a critter rule.
 */
public class Rule implements Expr {
	Condition condition;
	Command command;
	String category = "Rule";

	public Rule(Condition condition, Command command) {
		this.condition = condition;
		this.command = command;

	}

	public Command getCommand()
	{
		return command;
	}
	
	public Condition getCondition()
	{
		return condition;
	}
	
	@Override
	public int size() {
		return 1 + condition.size() + command.size();
	}

	@Override
	public Node nodeAt(int index) {
		index = index % size();
		assert (index >= 0 && index < size());
		if (index == 0) {
			return this;
		}
		index--;
		if (index < condition.size())
			return condition.nodeAt(index);
		index -= condition.size();
		return command.nodeAt(index);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		condition.prettyPrint(sb);
		sb.append(" --> ");
		command.prettyPrint(sb);
		return sb.append(";");
	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	@Override
	public String getType() {
		return "Rule";
	}

	@Override
	public int numChild() {
		return 2;
	}

	@Override
	public Node getChild(int n) {
		if (n % 2 == 0)
			return condition;
		return command;
	}

	@Override
	public void setSubNode(int n, Node c) {
		assert n > 0 && n < size();
		n--;
		if (n < condition.size()) {
			if (n == 0) {
				if (c instanceof Condition) {
					condition = (Condition) c;
				}
				return;
			}
			condition.setSubNode(n, c);
		} else {
			n -= condition.size();
			if (n == 0) {
				if (c instanceof Command) {
					command = (Command) c;
				}
				return;
			}
			command.setSubNode(n, c);
		}
	}

	@Override
	public Node copy() {
		if (condition instanceof Condition && command instanceof Command) {
			Condition newCond = (Condition) condition.copy();
			Command newCmd = (Command) command.copy();
			return new Rule(newCond, newCmd);
		}
		return null;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof Rule))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Rule)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		assert n >= 0;
		n %= 2;
		if (n % 2 == 0 && c instanceof Condition)
			condition = (Condition) c;
		else if (n % 2 == 1 && c instanceof Command)
			command = (Command) c;
	}

	@Override
	public void replacedBy(Node n) {
		System.out.println("Cannot transform a Rule node.");
	}

	public Expr shuffle() {

		return this;
	}

}
