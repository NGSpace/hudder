package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class MathVariableVisitor extends VariableVisitor {

	private final String[] values;
	private final char[] operations;
	
	public MathVariableVisitor(String[] values, char[] operations, AV3Compiler comp) {
		super(comp);
		this.values = values;
		this.operations = operations;
	}

	@Override
	public void visit(V3MethodWriter writer) throws ExecutionException {
		writer.complexMath(comp, values, operations);
	}
	
}
