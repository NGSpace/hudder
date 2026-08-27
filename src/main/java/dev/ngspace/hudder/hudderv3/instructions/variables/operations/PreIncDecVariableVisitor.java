package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import dev.ngspace.hudder.api.compilers.TextPos;
import dev.ngspace.hudder.api.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class PreIncDecVariableVisitor extends ExpressionVisitor {

	private ExpressionVisitor value;
	private boolean increase;

	public PreIncDecVariableVisitor(String string, AV3Compiler comp, boolean increase, TextPos pos,
			String expression) throws CompileException {
		super(comp, pos, expression);
		this.value = comp.parseVariable(string, pos);
		this.increase = increase;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		value.visit(methodWriter);
		methodWriter.checkcastSafe(Number.class, pos);
		methodWriter.doubleValue();
		methodWriter.loadConstantUnsafe(1d);
		if (increase) {
			methodWriter.dadd();
		} else {
			methodWriter.dsub();
		}
		methodWriter.callStatic(Double.class, "valueOf", false, Double.class, Double.TYPE);
		value.visitSetValue(methodWriter);
	}
	
}
