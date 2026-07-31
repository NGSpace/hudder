package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class StringVariableVisitor extends VariableVisitor {

	public String value;

	public StringVariableVisitor(AV3Compiler comp, String value) {
		super(comp);
		this.value = value;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.loadConstant(value);
	}
	
}
