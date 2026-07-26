package dev.ngspace.hudder.hudderv3.v3variableinstructions;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;

public class SystemVariableVisitor extends VariableVisitor {

	public String value;

	public SystemVariableVisitor(HudderV3Compiler comp, String variable) {
		super(comp);
		this.value = variable;
	}

	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		methodWriter.callDataVariableRegistry(value.toLowerCase());
	}
	
}
