package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class SetVariableVisitor extends ExpressionVisitor {

	private final ExpressionVisitor variable_name;
	private final ExpressionVisitor value;

	public SetVariableVisitor(AV3Compiler comp, String variable_name, String value, TextPos pos, String expression) throws CompileException {
		super(comp, pos, expression);
		this.variable_name = comp.parseVariable(variable_name, pos);
		this.value = comp.parseVariable(value, pos);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		value.visit(methodWriter);
		variable_name.visitSetValue(methodWriter);
	}
	
}
