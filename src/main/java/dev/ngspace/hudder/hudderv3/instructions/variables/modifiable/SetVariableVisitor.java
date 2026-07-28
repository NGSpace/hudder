package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class SetVariableVisitor extends VariableVisitor {

	private final VariableVisitor variable_name;
	private final String value;

	public SetVariableVisitor(AV3Compiler comp, String variable_name, String value) throws ExecutionException {
		super(comp);
		this.variable_name = comp.parseVariable(variable_name);
		this.value = value;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		comp.parseVariable(value).visit(methodWriter);
		variable_name.visitSetValue(methodWriter);
	}
	
}
