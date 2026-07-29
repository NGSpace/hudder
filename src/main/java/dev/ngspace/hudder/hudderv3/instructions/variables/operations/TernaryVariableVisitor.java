package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class TernaryVariableVisitor extends VariableVisitor {

	private VariableVisitor condition;
	private VariableVisitor truevalue;
	private VariableVisitor falsevalue;

	public TernaryVariableVisitor(AV3Compiler comp, String condition, String truevalue, String falsevalue)
			throws ExecutionException {
		super(comp);
		this.condition = comp.parseVariable(condition);
		this.truevalue = comp.parseVariable(truevalue);
		this.falsevalue = comp.parseVariable(falsevalue);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		Label end = new Label();
		Label label = new Label();
		
		condition.visit(methodWriter);
		methodWriter.checkcast(Boolean.class);
		methodWriter.booleanValue();
		
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
		falsevalue.visit(methodWriter);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(label);
		truevalue.visit(methodWriter);
		
		methodWriter.putLabel(end);
	}
	
}
