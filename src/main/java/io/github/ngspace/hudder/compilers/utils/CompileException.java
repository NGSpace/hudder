package io.github.ngspace.hudder.compilers.utils;

public class CompileException extends Exception {
	public final int line;
	public final int col;
	public final boolean fatal;
	
	public CompileException(String string) {this(string,0,0);}
	public CompileException(String string, boolean fatal) {this(string,0,0,fatal);}
	public CompileException(String string, int line, int col) {this(string,line,col,false);}
	public CompileException(String string, int line, int col, boolean fatal) {
		super(string);
		this.line = line;
		this.col = col;
		this.fatal = fatal;
	}
	private static final long serialVersionUID = -5301919978870515553L;
}
