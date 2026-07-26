package dev.ngspace.hudder.hudderv3.v3variableinstructions.constants;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;

public class StringVariableVisitor extends VariableVisitor {

	private String value;

	public StringVariableVisitor(HudderV3Compiler comp, String value) {
		super(comp);
		this.value = value;
	}

	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		methodWriter.loadConstant(value);
	}
	
}
