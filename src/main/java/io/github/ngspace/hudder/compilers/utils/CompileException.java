package io.github.ngspace.hudder.compilers.utils;

public class CompileException extends Exception {
	public final int line;
	public final int col;
	
	public CompileException(String string) {this(string,-1,0);}
	public CompileException(String string, int line, int col) {super(string);this.line = line;this.col = col;}
	public CompileException(String string, int line, int col, Throwable e) {
		super(string,e);
		this.line = line;
		this.col = col;
	}

	private static final long serialVersionUID = -5301919978870515553L;

	public String getFailureMessage() {
		return getLocalizedMessage()+(col>-1?"\nat line "+(line+1)+" col "+col:"");
	}
}
