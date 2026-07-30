package dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class NegateVariableVisitor extends VariableVisitor {

	private VariableVisitor value;

	public NegateVariableVisitor(AV3Compiler comp, String value) throws CompileException {
		super(comp);
		this.value = comp.parseVariable(value);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		value.visit(methodWriter);
		methodWriter.checkcast(Boolean.class);
		methodWriter.booleanValue();
		Label end = new Label();
		Label false_result = new Label();
		
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFNE, false_result);
		methodWriter.loadConstant(true);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(false_result);
		methodWriter.loadConstant(false);
		
		methodWriter.putLabel(end);
	}
	
}
