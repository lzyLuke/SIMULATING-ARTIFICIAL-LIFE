package ast;

import java.util.LinkedList;
import java.util.Random;

/**
 * This class implements the interface Mutation.
 */
public class MutationImpl implements Mutation {

	private int type;
	private int index;
	private static Random rand = new Random();
	private Node n;
	private boolean nodeSet = false;

	/**
	 * Passes the integer {@code type} to the constructor. Randomly generates
	 * numbers determining the parameters of the mutation.
	 * 
	 * @param type:
	 *            an integer ranging from 1 to 6, representing the type of the
	 *            mutation. This value is passed to the constructor through the
	 *            factory class MutationFactory.
	 */
	public MutationImpl(int type) {
		if (type <= 0 || type >= 7) {
			System.out.println(Integer.toString(type) + " is an illegal mutation type number.");
		}
		this.type = type;
		index = Math.abs(rand.nextInt());
	}

	@Override
	public Program getRuleMutation(Program p) {
		assert p != null;
		Program mutated = (Program) p.copy();
		// get a random node in p based on the size of p
		int size = mutated.size();
		if (!nodeSet)
			index = rand.nextInt(size);
		n = mutated.nodeAt(index);
		// set nodeSet to false so that the same MutationImpl object can be used
		// for both mutate() and mutate(int index, Mutation m)
		nodeSet = false;
		assert n != null;
		Node child1, child2;
		switch (type) {
		case 1: // remove
			if (n instanceof ProgramImpl)
				return mutated;
			if (n instanceof Rule) {
				mutated.setSubNode(index, null);
				return mutated;
			}
			if (n instanceof Update) {
				if (!((Update) n).onlyNode) {
					mutated.setSubNode(index, null);
					checkOnlyNode(mutated);
					return mutated;
				}
			}
			int numChild = n.numChild();
			if (numChild == 0)
				return mutated;
			Node child = findChildToReplace(n);
			if (child != n) {
				mutated.setSubNode(index, child);
			}
			break;
		case 2: // swap
			if (n instanceof Update)
				return mutated;
			numChild = n.numChild();
			if (numChild < 2)
				return mutated;
			if (numChild == 2) {
				child1 = n.getChild(0);
				child2 = n.getChild(1);
				if (!(child2 instanceof Action)) {
					n.setChild(0, child2);
					n.setChild(1, child1);
				}
			} else {
				int index1 = rand.nextInt(numChild);
				int index2 = rand.nextInt(numChild);
				while (index1 == index2) {
					index1 = rand.nextInt(numChild);
					index2 = rand.nextInt(numChild);
				}
				child1 = n.getChild(index1);
				child2 = n.getChild(index2);
				if (child1 instanceof Action || child2 instanceof Action) {
					return mutated;
				}
				n.setChild(index1, child2);
				n.setChild(index2, child1);
			}
			break;
		case 3: // replace
			n = n.shuffle();
			break;
		case 4: // transform
			if (n instanceof ProgramImpl || n instanceof Rule)
				return mutated;
			if (n instanceof Update || n instanceof Command)
				return mutated;
			if (n instanceof Number) {
				n.replacedBy(n);
				return mutated;
			}
			LinkedList<Node> list = new LinkedList<Node>();
			Node temp;
			for (int i = 0; i < mutated.size(); i++) {
				temp = mutated.nodeAt(i);
				if (temp != n && temp.getType().equals(n.getType())) {
					list.add(temp);
				}
			}
			if (list.size() == 0)
				return mutated;
			temp = list.get(rand.nextInt(list.size()));
			n.replacedBy(temp);
			break;
		case 5: // insert
			try {
				findParentNodeAndInsert(index, mutated);
			} catch (Exception e) {
				System.exit(1);
			}
			break;
		case 6: // duplicate
			if (n instanceof ProgramImpl) {
				list = new LinkedList<Node>();
				for (int i = 0; i < mutated.size(); i++) {
					temp = mutated.nodeAt(i);
					if (temp instanceof Rule) {
						list.add(temp);
					}
				}
				if (list.size() > 0) {
					temp = list.get(rand.nextInt(list.size())).copy();
					assert temp instanceof Rule;
					((ProgramImpl) n).ruleList.add(temp);
				}
			} else if (n instanceof Command) {
				if (((Command) n).listNode.get(((Command) n).listNode.size() - 1) instanceof Update) {
					list = new LinkedList<Node>();
					for (int i = 0; i < mutated.size(); i++) {
						temp = mutated.nodeAt(i);
						if (temp instanceof Update || temp instanceof Action) {
							list.add(temp);
						}
					}
					if (list.size() > 0) {
						temp = list.get(rand.nextInt(list.size())).copy();
						assert (temp instanceof Update || temp instanceof Action);
						((Command) n).listNode.add(temp);
					}
				}
			}
			break;
		}
		return mutated;
	}

	/**
	 * The setter that sets the instance variable {@code n} to the index given. Used
	 * by the method mutate(int index, Mutation m) given by the Program interface.
	 * 
	 * @param p:
	 *            the program to be mutated
	 * @param index:
	 *            the index of the node to be mutated
	 */
	public void setNode(Program p, int index) {
		this.index = index;
		nodeSet = true;
	}

	@Override
	public boolean equals(Mutation m) {
		if (m instanceof MutationImpl) {
			MutationImpl temp = (MutationImpl) m;
			if (temp.type == type && temp.index == index) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Replaces a node with one of its children of the same type if there's any.
	 * 
	 * @param n
	 *            The node to be replaced by one of its children of the same type.
	 * @param numChild
	 *            The number of children this node has.
	 * @return The child node replacing the node, or the node itself if there's no
	 *         child node to replace it.
	 */
	private Node findChildToReplace(Node n) {
		Node[] children = new Node[n.numChild()];
		int count = 0;
		for (int i = 0; i < n.numChild(); i++) {
			children[i] = n.getChild(i);
			if (children[i] != null && !n.canBeReplacedBy(children[i])) {
				children[i] = null;
			} else
				count++;
		}
		if (count == 0)
			return n;
		assert count > 0;
		int index = rand.nextInt(count);
		for (int i = 0; i < n.numChild(); i++) {
			if (children[i] != null) {
				if (index == 0)
					return children[i];
				index--;
			}
		}
		return n;
	}

	/**
	 * Checks if each {@code Command} element in {@code mutated} has only one child
	 * left after deleting an {@code Update} object. If a {@code Command} object
	 * only has one child that is not null, set the child's instance variable
	 * {@code onlNode} to be true. Used by mutation 1.
	 * 
	 * @param mutated
	 *            the input program
	 */
	private void checkOnlyNode(Program mutated) {
		for (int i = 0; i < mutated.size(); i++) {
			Node temp = mutated.nodeAt(i);
			if (temp instanceof Command) {
				int count = 0;
				for (int j = 0; j < ((Command) temp).listNode.size(); j++) {
					if (((Command) temp).listNode.get(j) != null)
						count++;
				}
				if (count < 2) {
					for (int j = 0; j < ((Command) temp).listNode.size(); j++) {
						if (((Command) temp).listNode.get(j) != null) {
							if (((Command) temp).listNode.get(j) instanceof Update) {
								((Update) ((Command) temp).listNode.get(j)).onlyNode = true;
							} else if (((Command) temp).listNode.get(j) instanceof Action) {
								((Action) ((Command) temp).listNode.get(j)).onlyNode = true;
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * receive an index {@code index } of a node and the root node {@code prog}, if
	 * this node can be inserted a parent node then a parent node will be inserted.
	 * 
	 * @param index
	 *            the index of the node that will be inserted a parent node.
	 * @param prog
	 *            the root node of the ast tree.
	 * @return true if successful insert, false if not
	 */
	private boolean findParentNodeAndInsert(int index, Program prog) {
		Node n = prog.nodeAt(index);

		if (!(n instanceof MiscellExpr || n instanceof BinaryExpression || n instanceof ast.Number
				|| n instanceof UnaryExpr))
			return false; // Only these nodes can be inserted a parent node

		for (int i = index - 1; i >= 0; i--) {

			Node parent = prog.nodeAt(i);
			if (parent instanceof MiscellExpr) {
				if (((MiscellExpr) parent).insertExpr(n))
					return true;
			} else if (parent instanceof BinaryExpression) {
				if (((BinaryExpression) parent).insertExpr(n))
					return true;
			} else if (parent instanceof Relation) {
				if (((Relation) parent).insertExpr(n))
					return true;
			} else if (parent instanceof Update) {
				if (((Update) parent).insertExpr(n))
					return true;
			}

			else if (parent instanceof Action) {
				if (((Action) parent).insertExpr(n))
					return true;
			}

		}

		return false;
	}
}
