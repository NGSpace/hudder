package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class PostIncDecVariableVisitor extends VariableVisitor {

	private VariableVisitor value;
	private boolean increase;

	public PostIncDecVariableVisitor(String string, AV3Compiler comp, boolean increase)
			throws ExecutionException {
		super(comp);
		this.value = comp.parseVariable(string);
		this.increase = increase;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		value.visit(methodWriter);
		methodWriter.checkcast(Number.class);
		methodWriter.doubleValue();
		methodWriter.dup2();
		methodWriter.loadConstantUnsafe(1d);
		if (increase) {
			methodWriter.methodVisitor.visitInsn(Opcodes.DADD);
		} else {
			methodWriter.methodVisitor.visitInsn(Opcodes.DSUB);
		}
		methodWriter.callStatic(Double.class, "valueOf", "(D)Ljava/lang/Double;", false);
		value.visitSetValue(methodWriter);
		methodWriter.pop();
		methodWriter.callStatic(Double.class, "valueOf", "(D)Ljava/lang/Double;", false);
	}
	
}
