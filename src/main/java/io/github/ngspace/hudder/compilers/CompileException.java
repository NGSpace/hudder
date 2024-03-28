package io.github.ngspace.hudder.compilers;

public class CompileException extends Exception {
	public final int line;
	public final int col;
	public CompileException(String string) {
		this(string,0,0);
	}
	public CompileException(String string, int line, int col) {
		super(string);
		this.line = line;
		this.col = col;
	}

	private static final long serialVersionUID = -5301919978870515553L;
}
