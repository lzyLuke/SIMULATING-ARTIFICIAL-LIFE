package interpret;

import ast.Condition;
import ast.Expr;
import ast.Program;
import simulate.Critter;

/**
 * An example interface for interpreting a critter program. This is just a starting
 * point and may be changed as much as you like.
 */
public interface Interpreter {
    /**
     * Execute program {@code p} until either the maximum number of rules per
     * turn is reached or some rule whose command contains an action is
     * executed.
     * @param p
     * @return a result containing the action to be performed;
     * the action may be null if the maximum number of rules
     * per turn was exceeded.
     */
    Outcome interpret(Program p);

    /**
     * Evaluate the given condition.
     * @param c
     * @return a boolean that results from evaluating c.
     */
    boolean eval(Condition c);

    /**
     * Evaluate the given expression.
     * @param e
     * @return an integer that results from evaluating e.
     */
    int eval(Expr e);
    
    /**
     * 
     * @return the current critter that are getting rule interpreted.
     */
	Critter getCritter();
}
