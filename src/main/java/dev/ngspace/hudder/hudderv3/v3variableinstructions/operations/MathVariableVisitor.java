package dev.ngspace.hudder.hudderv3.v3variableinstructions.operations;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;

public class MathVariableVisitor extends VariableVisitor {

	private final String[] values;
	private final char[] operations;
	
	public MathVariableVisitor(String[] values, char[] operations, HudderV3Compiler comp) {
		super(comp);
		this.values = values;
		this.operations = operations;
	}

	@Override
	public void visitMethod(V3MethodWriter writer) throws ExecutionException {
		writer.complexMath(comp, values, operations);
	}
	
}
