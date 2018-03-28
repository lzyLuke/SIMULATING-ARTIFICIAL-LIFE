package ast;

/**
 * A factory that produces the public static Mutation objects corresponding to
 * each mutation
 */
public class MutationFactory {
	public static Mutation getRemove() {
		return new MutationImpl(1);
	}

	public static Mutation getSwap() {
		return new MutationImpl(2);
	}

	public static Mutation getReplace() {
		return new MutationImpl(3);
	}

	public static Mutation getTransform() {
		return new MutationImpl(4);
	}

	public static Mutation getInsert() {
		return new MutationImpl(5);
	}

	public static Mutation getDuplicate() {
		return new MutationImpl(6);
	}
}
