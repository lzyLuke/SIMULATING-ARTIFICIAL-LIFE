package ast;

/**
 * A node in the abstract syntax tree of a program.
 */
public interface Node {

	/**
	 * The number of nodes in the AST rooted at this node, including this node
	 * 
	 * @return The size of the AST rooted at this node
	 */
	int size();

	/**
	 * Returns the node at {@code index} in the AST rooted at this node. Indices are
	 * defined such that:<br>
	 * 1. Indices are in the range {@code [0, size())}<br>
	 * 2. {@code this.nodeAt(0) == this} for all nodes in the AST <br>
	 * 3. All nodes in the AST rooted at {@code this} must be reachable by a call to
	 * {@code this.nodeAt(i)} with an appropriate index {@code i}
	 * 
	 * @param index
	 *            The index of the node to retrieve
	 * @return The node at {@code index}
	 * @throws IndexOutOfBoundsException
	 *             if {@code index} is not in the range of valid indices
	 */
	Node nodeAt(int index);

	/**
	 * Appends the program represented by this node prettily to the given
	 * StringBuilder.
	 * <p>
	 * The output of this method must be consistent with both the critter grammar
	 * and itself; that is:<br>
	 * 1. It must be possible to put the result of this method into a valid critter
	 * program<br>
	 * 2. Placing the result of this method into a valid critter program then
	 * parsing the program must yield an AST which contains a subtree identical to
	 * the one rooted at {@code this}
	 * 
	 * @param sb
	 *            The {@code StringBuilder} to which the program will be appended
	 * @return The {@code StringBuilder} to which this program was appended
	 */
	StringBuilder prettyPrint(StringBuilder sb);

	/**
	 * Returns the pretty-print of the abstract syntax subtree rooted at this node.
	 * <p>
	 * This method returns the same result as {@code prettyPrint(...).toString()}
	 * 
	 * @return The pretty-print of the AST rooted at this node.
	 */
	@Override
	String toString();

	/**
	 * Gets the type of a {@code Node}.
	 * 
	 * @return a string describing the type of the {@code Node}.
	 */
	String getType();

	/**
	 * Gets the number of children this {@code node} object has.
	 * 
	 * @return number of children
	 */
	int numChild();

	/**
	 * Gets the child based on the number n.
	 * 
	 * @param n:
	 *            the number that is used to locate the child
	 * @return the child at node n % numChildren
	 */
	Node getChild(int n);

	/**
	 * Sets the nth child of {@code this} to be {@code c}. Does nothing if the type
	 * of {@code c} isn't the same as the child's.
	 * 
	 * @param n
	 *            The index of the child. 0 <= n < numChild.
	 * @param c
	 *            The node to be set as the child at {@code n}.
	 */
	void setChild(int n, Node c);

	/**
	 * Sets the nth node in the subtree to be {@code c}. Does nothing if the type of
	 * {@code c} is different from the type of node number {@code n}. Used by remove
	 * mutation. Requires: 0 < n < this.size()
	 * 
	 * @param n:
	 *            the location of the node to be set in the subtree
	 * @param c:
	 *            the node to be set to
	 */
	void setSubNode(int n, Node c);

	/**
	 * Makes a deep copy of the node.
	 * 
	 * @return the deep copy of the node
	 */
	Node copy();

	/**
	 * Tests if two {@code Node} objects are equal.
	 * 
	 * @param n:
	 *            the {@code Node} object to be compared to {@code this}
	 * @return true if the two nodes are equal, false otherwise
	 */
	boolean equals(Node n);

	/**
	 * Tests if {@code this} can be replaced by node {@code n}. Used by mutation
	 * rule 1.
	 * 
	 * @param n
	 *            The node to replace {@code this}.
	 * @return true if {@code this} can be replaced by {@code n}, false otherwise
	 */
	boolean canBeReplacedBy(Node n);

	/**
	 * If the node can be shuffled, return a random shuffled node of this kind. The
	 * origin node will also be altered. If it cannot be shuffled, return the origin
	 * node.
	 * 
	 * @return the shuffled node
	 */
	Node shuffle();

	/**
	 * Replaces {@code this} with {@code n}, without changing its children. Literal
	 * integer constants are adjusted up or down by the value of
	 * java.lang.Integer.MAX_VALUE/r.nextInt(), where legal, and where r is a
	 * java.util.Random object. Used by transform mutation. Requires: {@code n} and
	 * {@code this} have the same type.
	 * 
	 * @param n
	 *            The node used to replace {@code this}.
	 */
	void replacedBy(Node n);
	
	

}
