package dev.ngspace.hudder.exceptions;

import dev.ngspace.hudder.compilers.utils.TextPos;

public class CompileException extends Exception {
	public final int line;
	public final int col;
	
	public CompileException(String string, int line, int col) {super(string);this.line = line;this.col = col;}
	public CompileException(String string, TextPos pos) {this(string, pos.line(), pos.column());}
	public CompileException(String string, int line, int col, Throwable e) {
		super(string,e);
		this.line = line;
		this.col = col;
	}

	public CompileException(ExecutionException e1) {
		this(e1.getMessage(), e1.line, e1.col, e1);
	}

	private static final long serialVersionUID = -5301919978870515553L;

	public String getFailureMessage() {
		return getLocalizedMessage()+(line>-1?"\n\u00A7bat line "+(line+1)+" col "+col:"");
	}
}
