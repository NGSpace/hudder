package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class StringVariableVisitor extends VariableVisitor {

	private String value;

	public StringVariableVisitor(AV3Compiler comp, String value) {
		super(comp);
		this.value = value;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		methodWriter.loadConstant(value);
	}
	
}
