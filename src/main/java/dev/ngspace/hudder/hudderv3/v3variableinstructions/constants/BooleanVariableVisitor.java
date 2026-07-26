package dev.ngspace.hudder.hudderv3.v3variableinstructions.constants;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;

public class BooleanVariableVisitor extends VariableVisitor {

	private boolean value;

	public BooleanVariableVisitor(HudderV3Compiler comp, boolean value) {
		super(comp);
		this.value = value;
	}

	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		methodWriter.loadConstant(value);
	}
	
}
