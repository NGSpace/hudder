package dev.ngspace.hudder.exceptions;

import java.io.IOException;

import dev.ngspace.hudder.api.compilers.TextPos;

public class ExecutionException extends Exception {
	public final int line;
	public final int col;
	
	public ExecutionException(String string) {this(string, -1, -1);}
	public ExecutionException(String string, int line, int col) {super(string);this.line = line;this.col = col;}
	public ExecutionException(String string, TextPos pos) {this(string, pos.line(), pos.column());}
	public ExecutionException(String string, int line, int col, Throwable e) {
		super(string,e);
		this.line = line;
		this.col = col;
	}

	public ExecutionException(CompileException e) {
		this(e.getMessage(), e.line, e.col, e);
	}

	public ExecutionException(IOException e) {
		this(e, new TextPos(-1,-1));
	}

	public ExecutionException(IOException e, TextPos pos) {
		super(e.getMessage(), e);
		this.line = pos.line();
		this.col = pos.column();
	}

	public ExecutionException(Exception e, int line, int column) {
		super(e.getMessage(), e);
		this.line = line;
		this.col = column;
	}

	private static final long serialVersionUID = -5301919978870515553L;

	public String getFailureMessage() {
		return getLocalizedMessage()+(line>-1?"\n\u00A7bat line "+(line+1)+" col "+col:"");
	}
}