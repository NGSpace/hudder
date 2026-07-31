package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class SetVariableVisitor extends VariableVisitor {

	private final VariableVisitor variable_name;
	private final String value;

	public SetVariableVisitor(AV3Compiler comp, String variable_name, String value) throws CompileException {
		super(comp);
		this.variable_name = comp.parseVariable(variable_name);
		this.value = value;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		comp.parseVariable(value).visit(methodWriter);
		variable_name.visitSetValue(methodWriter);
	}
	
}
