package ast;

import interpret.Interpreter;

/**
 * An interface representing a Boolean condition in a critter program.
 *
 */
public interface Condition extends Node, Expr {
	boolean accept(Interpreter i);
}
