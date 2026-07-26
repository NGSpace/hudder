package dev.ngspace.hudder.hudderv3.v3variableinstructions.constants;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;

public class NumberVariableVisitor extends VariableVisitor {

	private String value;

	public NumberVariableVisitor(HudderV3Compiler comp, String value) {
		super(comp);
		this.value = value;
	}

	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		if (value.startsWith("0x")) {
			methodWriter.loadConstant(Integer.parseUnsignedInt(value.substring(2), 16));
		} else if (value.startsWith("#")) {
			methodWriter.loadConstant(Integer.parseUnsignedInt(value.substring(1), 16));
		} else {
			methodWriter.loadConstant(Double.parseDouble(value));
		}
	}
	
}
