package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class SetVariableVisitor extends VariableVisitor {

	private final VariableVisitor variable_name;
	private final VariableVisitor value;

	public SetVariableVisitor(AV3Compiler comp, String variable_name, String value, TextPos pos) throws CompileException {
		super(comp, pos);
		this.variable_name = comp.parseVariable(variable_name, pos);
		this.value = comp.parseVariable(value, pos);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		value.visit(methodWriter);
		variable_name.visitSetValue(methodWriter);
	}
	
}
