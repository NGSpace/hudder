package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class StringVariableVisitor extends ExpressionVisitor {

	public String value;

	public StringVariableVisitor(AV3Compiler comp, String value, TextPos pos, String expression) {
		super(comp, pos, expression);
		this.value = value;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.loadConstant(value);
	}

	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public Object getConstantValue() {
		return value;
	}
}
