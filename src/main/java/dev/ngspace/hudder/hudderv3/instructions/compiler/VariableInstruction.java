package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.DynamicVariableVisitor;

public class VariableInstruction extends Instruction {

	public AV3Compiler comp;
	public String variable;
	public VariableVisitor visitor;
	
	public VariableInstruction(AV3Compiler comp, String variable) throws CompileException {
		this.comp = comp;
		this.variable = variable;
		this.visitor = comp.parseVariable(variable);
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		if ("break".equalsIgnoreCase(variable.trim())) {
			methodWriter.jumpto(breaklabel);
		} else {
			visitor.visit(methodWriter);
			// This is such a stupid edge case I was willing to die on for no fucking reason and now it's too late
			if (visitor instanceof DynamicVariableVisitor) {
				Label end = new Label();
				methodWriter.dup();
				methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFNONNULL, end);
				methodWriter.pop();
				methodWriter.loadConstant(variable.toLowerCase());
				methodWriter.putLabel(end);
			}
			methodWriter.appendToBuilderAndPop();
		}
	}
	
}
