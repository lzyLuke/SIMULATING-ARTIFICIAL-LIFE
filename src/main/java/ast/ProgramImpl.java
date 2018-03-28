package ast;

import java.util.LinkedList;
import java.util.Random;

/**
 * A data structure representing a critter program.
 *
 */
public class ProgramImpl implements Program {

	public LinkedList<Node> ruleList;
	public int type;

	public ProgramImpl() {
		ruleList = new LinkedList<Node>();
	}
	
	public LinkedList<Node> getRuleList()
	{
		return ruleList;
	}
	
	public void addRules(Rule rule) {

		ruleList.add(rule);
	}

	public void setRuleList(LinkedList<Node> rl)
	{
		ruleList=rl;
	}
	
	@Override
	public int size() {
		int size = 1;
		for (int i = 0; i < ruleList.size(); i++) {
			if (ruleList.get(i) != null) {
				size += ruleList.get(i).size();
			}
		}
		return size;
	}

	@Override
	public Node nodeAt(int index) {
		while (index < 0)
			index += this.size();
		index = index % this.size();
		if (index == 0)
			return this;
		index = index - 1;
		if (index < 0 || index > size()) {
			System.out.println();
		}
		assert (index >= 0 && index < size());
		for (Node node : ruleList) {
			if (index >= node.size())
				index = index - node.size();
			else
				return node.nodeAt(index);
		}
		return null;

	}

	@Override
	public Program mutate() {
		StringBuilder sb = this.prettyPrint(new StringBuilder());
		if (sb.toString().trim().equals("")) {
			System.out.println("Cannot mutate an empty program.");
			return this;
		}
		Random rand = new Random();
		Mutation m = null;
		Program mutated = null;
		/*
		 * for some programs, the getMutation() method may not change the program loop
		 * until first change occurs
		 */
		while (mutated == null || mutated.equals(this)) {
			type = rand.nextInt(6) + 1;
			assert type > 0 && type < 7;
			switch (type) {
			case 1:
				m = MutationFactory.getRemove();
				break;
			case 2:
				m = MutationFactory.getSwap();
				break;
			case 3:
				m = MutationFactory.getReplace();
				break;
			case 4:
				m = MutationFactory.getTransform();
				break;
			case 5:
				m = MutationFactory.getInsert();
				break;
			case 6:
				m = MutationFactory.getDuplicate();
				break;
			default:
				break;
			}
			assert m != null;
			mutated = m.getRuleMutation(this);
		}
		return mutated;
	}

	@Override
	public Program mutate(int index, Mutation m) {
		StringBuilder sb = this.prettyPrint(new StringBuilder());
		if (sb.toString().trim().equals("")) {
			System.out.println("Cannot mutate an empty program.");
			return null;
		}
		assert (m instanceof MutationImpl);
		MutationImpl temp = (MutationImpl) m;
		temp.setNode(this, index);
		Program mutated = temp.getRuleMutation(this);
		// for some programs, the getMutation() method may not change the program
		// loop until first change occurs
		int count = 0;
		// try at least 100 times to guarantee a higher success rate of mutation
		while (mutated.equals(this) && count < 100) {
			// reset node in case that the pointer or index is changed by the previous
			// mutation attempt
			temp.setNode(this, index);
			mutated = temp.getRuleMutation(this);
			count++;
		}
		if (mutated.equals(this))
			return null;
		return mutated;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (ruleList.size() == 0)
			return sb;
		for (int i = 0; i < ruleList.size(); i++) {
			if (ruleList.get(i) != null) {
				ruleList.get(i).prettyPrint(sb);
				sb.append("\n");
			}
		}
		return new StringBuilder(sb.toString().trim());
	}

	@Override
	public String toString() {
		return this.prettyPrint(new StringBuilder()).toString();
	}

	@Override
	public String getType() {
		return "ProgramImpl";
	}

	@Override
	public int numChild() {
		int count = 0;
		for (int i=0; i<ruleList.size(); i++) {
			if (ruleList.get(i) != null) {
				count++;
			}
		}
		return count;
	}

	@Override
	public Node getChild(int n) {
		int index = n % numChild();
		return ruleList.get(index);
	}

	@Override
	public void setSubNode(int n, Node c) {
		if (n < 0 || n >= size()) {
			System.out.println("Index of node out of range.");
		}
		n--;
		for (int i = 0; i < ruleList.size(); i++) {
			if (ruleList.get(i) != null) {
				if (n < ruleList.get(i).size()) {
					if (n == 0) {
						if (c == null) ruleList.remove(i);
						else if (c instanceof Rule) ruleList.set(i, c);
						return;
					}
					ruleList.get(i).setSubNode(n, c);
					return;
				} else
					n -= ruleList.get(i).size();
			}
		}
	}

	@Override
	public Node copy() {
		LinkedList<Node> newList = new LinkedList<Node>();
		Node rule;
		for (int i = 0; i < ruleList.size(); i++) {
			rule = ruleList.get(i).copy();
			assert rule != null;
			newList.add(rule);
		}
		assert newList != ruleList;
		ProgramImpl newP = new ProgramImpl();
		newP.ruleList = newList;
		return newP;
	}

	@Override
	public boolean equals(Node n) {
		if (!(n instanceof ProgramImpl))
			return false;
		return this.toString().equals(n.toString());
	}

	@Override
	public boolean canBeReplacedBy(Node n) {
		if (n instanceof Program)
			return true;
		return false;
	}

	@Override
	public void setChild(int n, Node c) {
		assert n >= 0;
		if (n < ruleList.size() && c instanceof Rule) {
			ruleList.set(n, c);
		}
	}

	@Override

	public Expr shuffle() {
		return this;
	}

	@Override
	public void replacedBy(Node n) {
		System.out.println("Cannot transform a ProgramImpl node.");
	}

	@Override
	public int getMutationType() {
		return type;
	}
	

}
