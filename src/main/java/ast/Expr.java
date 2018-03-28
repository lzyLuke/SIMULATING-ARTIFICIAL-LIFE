package ast;
import interpret.*;
/**
 * A critter program expression that has an integer value.
 */
public interface Expr extends Node {
	/**
	 * If the node can be shuffled, return a random shuffled node of this kind, the
	 * node its self will also be altered. If it cannot be altered, return the
	 * origin node.
	 * 
	 * @return the shuffled node
	 */
	Expr shuffle();
	
}
