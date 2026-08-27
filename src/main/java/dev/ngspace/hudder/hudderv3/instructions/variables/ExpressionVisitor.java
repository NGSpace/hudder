package dev.ngspace.hudder.hudderv3.instructions.variables;

import dev.ngspace.hudder.api.compilers.TextPos;
import dev.ngspace.hudder.api.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;

public abstract class ExpressionVisitor {
	
	protected AV3Compiler comp;
	protected TextPos pos;
	protected String expression;
	protected ExpressionVisitor(AV3Compiler comp, TextPos pos, String expression) {
		this.comp = comp;
		this.pos = pos;
		this.expression = expression;
	}
	
	public abstract void visit(V3MethodWriter methodWriter) throws CompileException;
	
	public void visitSetValue(V3MethodWriter methodWriter) throws CompileException {
		throw new CompileException("Setting a value to this variable is not supported", pos);
	}
	
	public boolean isConstant() {
		return false;
	}
	
	public Object getConstantValue() {
		return null;
	}
}
