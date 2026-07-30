package dev.ngspace.hudder.hudderv3.instructions.variables;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;

public class SystemVariableVisitor extends VariableVisitor {

	public String value;

	public SystemVariableVisitor(AV3Compiler comp, String variable) {
		super(comp);
		this.value = variable;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.callDataVariableRegistry(value.toLowerCase());
	}
	
}
