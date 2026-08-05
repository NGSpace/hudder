package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class BooleanVariableVisitor extends VariableVisitor {

	private boolean value;

	public BooleanVariableVisitor(AV3Compiler comp, boolean value, TextPos pos) {
		super(comp, pos);
		this.value = value;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.loadConstant(value);
	}
	
}
