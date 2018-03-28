package exceptions;

/**
 * An exception indicating a syntax error.
 */
public class SyntaxError extends Exception {
	private static final long serialVersionUID = 211220140930L;
	int line;
	String fault;

	public SyntaxError() {
	}

	public SyntaxError(String fault, int line) {
		this.fault = fault;
		this.line = line;
	}

	public String getFault() {
		return fault;
	}

	public int getLine() {
		return line;
	}
}
