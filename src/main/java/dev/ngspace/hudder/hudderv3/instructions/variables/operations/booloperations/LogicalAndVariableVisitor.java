package dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class LogicalAndVariableVisitor extends VariableVisitor {

	private VariableVisitor[] values;

	public LogicalAndVariableVisitor(VariableVisitor[] values, AV3Compiler comp, TextPos pos) {
		super(comp, pos);
		this.values = values;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		Label end = new Label();
		Label false_value = new Label();
		
		for (int i = 0;i<values.length;i++) {
			values[i].visit(methodWriter);
			methodWriter.checkcast(Boolean.class);
			methodWriter.booleanValue();
			methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, false_value);
		}
		methodWriter.loadConstant(true);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(false_value);
		
		methodWriter.loadConstant(false);
		
		methodWriter.putLabel(end);
	}
	
}
