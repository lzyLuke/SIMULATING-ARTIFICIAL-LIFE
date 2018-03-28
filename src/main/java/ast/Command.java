package ast;
import java.util.LinkedList;

import interpret.Interpreter;
import interpret.Outcome;
import interpret.OutcomeImpl;
public class Command<E> implements Expr{
	String category = "Command";
	LinkedList<Expr> listNode; // A list to store update node and action node
	
	public Command()
	{
		listNode=new LinkedList<Expr>();
	}
	/**
	 * add a update node to this command
	 * @param update the update node that will be added.
	 */
	public void addUpdate(Update update)
	{
		listNode.add(update);
		int count = 0;
		for (int i=0; i<listNode.size(); i++) {
			if (listNode.get(i) != null) count++;
		}
		if (count > 1) notOnlyNode();
	}
	/**
	 * add a action node to this command
	 * @param action the action node that will be added.
	 */
	public void addAction(Action action)
	{
		listNode.add(action);
		notOnlyNode();
	}
	
	
	public Node nodeAt(int index)
	{
		index = index % size();
		assert (index >= 0 && index < size());
		if (index == 0) return this;
		index--;
		for (Node n : listNode) {
			if (n != null) {
				if (index < n.size()) return n.nodeAt(index);
				index -= n.size();
			}
		}
		return null;
	}
	
	public StringBuilder prettyPrint(StringBuilder sb)
	{
		if (listNode.size() == 0) return sb;
		for (int i=0; i<listNode.size(); i++) {
			if (listNode.get(i) != null) {
				listNode.get(i).prettyPrint(sb);
				sb.append(" ");
			}
		}
		return new StringBuilder(sb.toString().trim());
	}
	
	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}
	
	public int size()
	{
		int s = 1;
		for (int i=0; i<listNode.size(); i++) {
			if (listNode.get(i) != null) {
				s += listNode.get(i).size();
			}
		}
		return s;
	}

	@Override
	public String getType() {
		return "Command";
	}
	
	@Override
	public int numChild() {
		return listNode.size();
	}

	@Override
	public Node getChild(int n) {
		int index = n % listNode.size();
		return listNode.get(index);
	}

	@Override
	public void setSubNode(int n, Node c) {
		assert n >= 0 && n < size();
		n--;
		for (int i=0; i<listNode.size(); i++) {
			if (listNode.get(i) != null) {
				if (n < listNode.get(i).size()) {
					if (n == 0) {
						if (c instanceof Update) {
							listNode.set(i, (Update) c);
						} else if (c instanceof Action) {
							listNode.set(i, (Action) c);
						} else if (c == null) {
							listNode.remove(i);
						}
						return;
					}
					listNode.get(i).setSubNode(n, c);
				} else n -= listNode.get(i).size();
			}
		}
	}

	@Override
	public Node copy() {
		LinkedList<Expr> newList = new LinkedList<Expr>();
		Expr node;
		for (int i=0; i<listNode.size(); i++) {
			assert listNode.get(i) instanceof Expr;
			node = (Expr) listNode.get(i).copy();
			newList.add(node);
		}
		Command cmd = new Command();
		cmd.listNode = newList;
		return cmd;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof Command)) return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Update || n instanceof Action) return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		assert n >= 0;
		if (n < listNode.size() && c instanceof Expr) {
			listNode.set(n, (Expr) c);
		}
	}

	
	@Override
	/**
	 *  add an random update to the head of the list.
	 */
	public Expr shuffle() {
		ast.Number newNumA = new ast.Number(0);
		ast.Number newNumB = new ast.Number(0);
		newNumA.shuffle();
		newNumB.shuffle();
		MiscellExpr newMiscell = new MiscellExpr("MEM",newNumA);
		newMiscell.isCommand();
		Update RandUpdate = new Update(newMiscell,newNumB);
		listNode.add(0, RandUpdate);
		
		return this;
	}

	@Override
	public void replacedBy(Node n) {
		System.out.println("Cannot transform a Command node.");

	}
	
	/**
	 * Sets the boolean variable onlyNode in each child to be false. 
	 * Called when {@code this} has more than one child.
	 * Used by mutation 1 to tell if it can delete an {@code Update} node in {@code this}.
	 */
	private void notOnlyNode() {
		for (int i=0; i<listNode.size(); i++) {
			if (listNode.get(i) instanceof Update) {
				((Update) listNode.get(i)).onlyNode = false;
			} else if (listNode.get(i) instanceof Action) {
				((Action) listNode.get(i)).onlyNode = false;
			}
		}
	}
	
	public Outcome accept(Interpreter i)
	{

		for(Expr e :listNode)
		{
			if(e instanceof Action)
				return ((Action)e).accept(i);
			else
				((Update)e).accept(i);
		}
		
		return new OutcomeImpl("",0);
	}
}
