package ast;

/**
 * A mutation to the AST
 */
public interface Mutation {
	/**
	 * Compares the type of this mutation to {@code m}
	 * 
	 * @param m
	 *            The mutation to compare with
	 * @return Whether this mutation is the same type as {@code m}
	 */
	boolean equals(Mutation m);

	/**
	 * Gets a random rule mutation.
	 * 
	 * @param p
	 *            The program to be mutated.
	 * @return The mutated program. If no mutation occurs, returns the original
	 *         program.
	 */
	public Program getRuleMutation(Program p);
}
