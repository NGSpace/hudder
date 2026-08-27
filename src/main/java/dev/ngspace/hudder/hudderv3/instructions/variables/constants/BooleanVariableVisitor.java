package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import dev.ngspace.hudder.api.compilers.TextPos;
import dev.ngspace.hudder.api.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class BooleanVariableVisitor extends ExpressionVisitor {

	private boolean value;

	public BooleanVariableVisitor(AV3Compiler comp, boolean value, TextPos pos, String expression) {
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
