package parse;

import java.io.Reader;

import ast.*;
import exceptions.SyntaxError;

class ParserImpl implements Parser {

	private Tokenizer tokenizer;
	private static ProgramImpl result;

	@Override
	/*
	 * Create a new tokenizer using the reader stream, parse the program using
	 * {@code arserImpl.parseProgram(Tokenizer)} method, it will catch SyntaxError
	 * thrown in the parsing process and it will print the error token with specific
	 * line numbers and also print its StackTrace. This will be convenient when
	 * debugging.
	 * 
	 * @param r the reader stream from text
	 * 
	 * @return a parsed Program
	 */
	public Program parse(Reader r) {
		tokenizer = new Tokenizer(r);
		try {
			result = ParserImpl.parseProgram(tokenizer);
		} catch (SyntaxError e) {
			System.out.print("error on token \" " + e.getFault() + " \" at line");
			System.out.println(e.getLine());
			e.printStackTrace();
			System.exit(1);
		}

		return result;
	}

	/**
	 * Parses a program from the stream of tokens provided by the Tokenizer,
	 * consuming tokens representing the program. All following methods with a name
	 * "parseX" have the same spec except that they parse syntactic form X.
	 * 
	 * @return the created AST
	 * @throws SyntaxError
	 *             if there the input tokens have invalid syntax
	 */
	public static ProgramImpl parseProgram(Tokenizer t) throws SyntaxError {
		ProgramImpl prog = new ProgramImpl();
		// To test the peek of the tokenizer is EOF or not. If it is EOF, then it
		// reaches to its end.
		while (!t.peek().getType().toString().equals("EOF")) {
			prog.addRules(parseRule(t)); // Every line is a rule
		}

		return prog;

	}

	/**
	 * To parse rule, a rule should be "Condition --> Command"
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return The Rule that have been parsed, a rule should contain a condition
	 *         node and a command node.
	 * @throws SyntaxError
	 */
	public static Rule parseRule(Tokenizer t) throws SyntaxError {
		Condition condition = parseCondition(t);

		if (t.peek().toString().equals("-->"))
			t.next(); // We do not need to parse -->
		else
			throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());

		Command command = parseCommand(t);
		if (t.peek().toString().equals(";"))
			t.next(); // The end of the line
		else
			throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());

		return new Rule(condition, command);

	}

	/**
	 * parse a command, a command is made of several update node and a action node(
	 * If it has)
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return The Command Node that have been parsed.
	 * @throws SyntaxError
	 */
	public static Command parseCommand(Tokenizer t) throws SyntaxError {
		Command newCommand = new Command();

		while (t.hasNext() && !t.peek().isAction() && !t.peek().getType().toString().equals(";")) {
			newCommand.addUpdate(parseUpdate(t));
		} // Several update is added to a command. When it comes a token ";" or a action
			// token, then we stop add update nodes.

		if (t.hasNext() && t.peek().isAction() && !t.peek().getType().toString().equals(";"))
			newCommand.addAction(parseAction(t));
		// Action node should appeared after update node (If it has)

		return newCommand;
	}

	/**
	 * Parse a update node, a update is made of a mem[expr] node(MiscellExpr) and a
	 * expr node. Update --> mem[expr] := expr
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return The update that have been parsed, an Update Node should contain a
	 *         MiscellExpr Node and a Expr node.
	 * @throws SyntaxError
	 */
	public static Update parseUpdate(Tokenizer t) throws SyntaxError {

		Expr memExpr;
		Expr expr;
		if (t.peek().isMemSugar()) {
			memExpr = new MiscellExpr("MEM", TranslateSugar(t));
			// the current token is a MemSugar, then it will be translated into
			// its origin representation MEM[<number>]
			((MiscellExpr) memExpr).isCommand(); // Mark this MiscellExpr is a children of Command node.
		} else {

			consume(t, TokenType.MEM);
			consume(t, TokenType.LBRACKET);
			memExpr = new MiscellExpr("MEM", parseExpression(t));
			((MiscellExpr) memExpr).isCommand(); // Mark this MiscellExpr is a children of Command Node
			consume(t, TokenType.RBRACKET);

		}

		if (t.peek().toString().equals(":="))
			t.next(); // We do not need to parse :=
		else
			throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());

		expr = parseExpression(t);

		return new Update(memExpr, expr);
	}

	/**
	 * Parse an Action node. Action --> wait | forward | backward | left | right |
	 * eat | attack | grow | bud | mate | tag[expr] | serve[expr]
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return The Action that have been parsed
	 * @throws SyntaxError
	 */
	public static Action parseAction(Tokenizer t) throws SyntaxError {
		String act = t.next().getType().toString();
		// get the stringRep of the current token;

		if (act.equals("tag") || act.equals("serve")) {
			consume(t, TokenType.LBRACKET);
			Expr expr = parseExpression(t);
			consume(t, TokenType.RBRACKET);
			return new Action(act, expr); // An action is made of a stringRep of act and expr.
		} else if (act.equals("wait") || act.equals("forward") || act.equals("backward") || act.equals("left")
				|| act.equals("right") || act.equals("eat") || act.equals("attack") || act.equals("grow")
				|| act.equals("bud") || act.equals("mate"))
			return new Action(act, null);
		else
			throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());
	}

	/**
	 * Parse an condition node. Condition --> conjunction (| or conjunction |)*
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return The Condition that have been parsed
	 * @throws SyntaxError
	 */
	public static Condition parseCondition(Tokenizer t) throws SyntaxError {
		Condition left = parseConjunction(t);
		while (t.peek().getType().toString().equals("or")) {
			consume(t, TokenType.OR); // Consume an OR Token
			left = new BinaryCondition(left, BinaryCondition.Operator.OR, parseConjunction(t));
		}
		return left;
	}

	/**
	 * Parse an conjunction node using BinaryCondition Node. Conjunction -->
	 * relation (| or relation |)*
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return The Conjunction that have been parsed
	 * @throws SyntaxError
	 */
	public static Condition parseConjunction(Tokenizer t) throws SyntaxError {
		Condition left = parseRelation(t);
		while (t.peek().getType().toString().equals("and")) {
			consume(t, TokenType.AND);
			left = new BinaryCondition(left, BinaryCondition.Operator.AND, parseRelation(t));
		}
		return left;

	}

	/**
	 * Parse a relation node. relation -- > expr rel expr | { condition }
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return A Relation node that have been parsed
	 * @throws SyntaxError
	 */
	public static Relation parseRelation(Tokenizer t) throws SyntaxError {
		switch (t.peek().getType()) {
		case LBRACE: // parse { condition }
			consume(t, TokenType.LBRACE);
			Condition condition = parseCondition(t);
			consume(t, TokenType.RBRACE);
			return new Relation(condition); // This relation node only contains a single condition node
		default: // parse expr rel expr
			Expr left = parseExpression(t);
			String op = t.peek().getType().toString();
			// parse rel --> < | <= | = | >= | > | !=
			if (op.equals("<") || op.equals("<=") || op.equals("=") || op.equals(">=") || op.equals(">")
					|| op.equals("!="))
				t.next();
			else
				throw new SyntaxError();

			Expr right = parseExpression(t);
			return new Relation(left, op, right); // This contains expr rel expr
		}

	}

	/**
	 * Parse a Expression node. expr --> term (| addop term |)*
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return A Expression node that have been parsed
	 * @throws SyntaxError
	 */
	public static Expr parseExpression(Tokenizer t) throws SyntaxError {
		Expr l = parseTerm(t);
		String op;
		while (t.peek().isAddOp()) { // If the peek token is an AddOp, thus there must be a expr followed by this.

			switch (t.peek().getType()) {

			case PLUS:
				consume(t, TokenType.PLUS);
				op = "+";
				break;
			case MINUS:
				consume(t, TokenType.MINUS);
				op = "-";
				break;
			default:
				throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());
			}

			l = new BinaryExpression(l, op, parseTerm(t)); // Using BinaryExpression, we can express a expression that
															// has multiple operators and factors.
		}
		return l;
	}

	/**
	 * Parse a Term node. term --> factor (| mulop factor |)*
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return A Relation node that have been parsed
	 * @throws SyntaxError
	 */
	public static Expr parseTerm(Tokenizer t) throws SyntaxError {
		Expr l = parseFactor(t); // parse the first factor (This must exists.)
		String op;
		while (t.peek().isMulOp()) { // If there is an MulOp, then a factor must comes after this.
			switch (t.peek().getType()) {

			case MUL:
				consume(t, TokenType.MUL);
				op = "*";
				break;
			case DIV:
				consume(t, TokenType.DIV);
				op = "/";
				break;
			case MOD:
				consume(t, TokenType.MOD);
				op = "mod";
				break;

			default:
				throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());

			}
			l = new BinaryExpression(l, op, parseFactor(t)); // We also use BinaryExpression to represent this
																// chain-like relation.
																// Only the operators differs from Expression
		}
		return l;
	}

	/**
	 * Parse a Factor node. factor --> <number> | mem[expr] | (expr) | -factor |
	 * nearby[expr] | ahead[expr] | random[expr] | smell
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return A Factor node that have been parsed
	 * @throws SyntaxError
	 */
	public static Expr parseFactor(Tokenizer t) throws SyntaxError {
		MiscellExpr MisExpr;
		switch (t.peek().getType()) { // To judge the peek token and to see what to parse.
		case NUM:
			ast.Number num = new ast.Number(t.next().toNumToken().getValue());
			return num;
		case LPAREN:
			consume(t, TokenType.LPAREN);
			Expr be = parseExpression(t);
			consume(t, TokenType.RPAREN);
			return be;
		case MINUS:
			consume(t, TokenType.MINUS);
			String op = "-";
			return new UnaryExpr(op, parseFactor(t));
		case AHEAD:
			consume(t, TokenType.AHEAD);
			consume(t, TokenType.LBRACKET);
			MisExpr = new MiscellExpr("AHEAD", parseExpression(t));
			consume(t, TokenType.RBRACKET);
			return MisExpr;
		case MEM:
			consume(t, TokenType.MEM);
			consume(t, TokenType.LBRACKET);
			MisExpr = new MiscellExpr("MEM", parseExpression(t));
			consume(t, TokenType.RBRACKET);
			return MisExpr;
		case NEARBY:
			consume(t, TokenType.NEARBY);
			consume(t, TokenType.LBRACKET);
			MisExpr = new MiscellExpr("NEARBY", parseExpression(t));
			consume(t, TokenType.RBRACKET);
			return MisExpr;
		case RANDOM:
			consume(t, TokenType.RANDOM);
			consume(t, TokenType.LBRACKET);
			MisExpr = new MiscellExpr("RANDOM", parseExpression(t));
			consume(t, TokenType.RBRACKET);
			return MisExpr;
		case SMELL:
			consume(t, TokenType.SMELL);
			MisExpr = new MiscellExpr("SMELL", null);
			return MisExpr;
		default:
			return new MiscellExpr("MEM", TranslateSugar(t)); // MemSugar must be translated into a number to begin its
																// origin representation.
		}
	}

	/**
	 * Translate the MEMSUGAR into the number of MEM[...]. We need this number to
	 * represent MEM[...]
	 * 
	 * @param t
	 *            the given Tokenizer
	 * @return a Expr ( a Number class)
	 * @throws SyntaxError
	 */
	public static Expr TranslateSugar(Tokenizer t) throws SyntaxError {
		switch (t.next().getType()) {
		case ABV_MEMSIZE:
			return new ast.Number(0);
		case ABV_DEFENSE:
			return new ast.Number(1);
		case ABV_OFFENSE:
			return new ast.Number(2);
		case ABV_SIZE:
			return new ast.Number(3);
		case ABV_ENERGY:
			return new ast.Number(4);
		case ABV_PASS:
			return new ast.Number(5);
		case ABV_TAG:
			return new ast.Number(6);
		case ABV_POSTURE:
			return new ast.Number(7);
		default:
			throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());// Error in translatingSugar, throw an
																				// SyntaxException.
		}
	}

	/**
	 * Consumes a token of the expected type.
	 * 
	 * @throws SyntaxError
	 *             if the wrong kind of token is encountered.
	 */
	public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {
		if (t.peek().getType().equals(tt)) {
			t.next();
		} else {
			throw new SyntaxError(t.peek().toString(), t.peek().lineNumber());
		}

	}
}
