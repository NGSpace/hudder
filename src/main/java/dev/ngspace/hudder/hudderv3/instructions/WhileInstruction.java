package dev.ngspace.hudder.hudderv3.instructions;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class WhileInstruction extends Instruction {

	private HudderV3Compiler comp;
	private VariableVisitor condition;
	private String block;
	private HudderConfig info;
	private String filename;

	public WhileInstruction(String condition, String block, HudderV3Compiler comp,
			HudderConfig info, String filename) throws ExecutionException {
		this.comp = comp;
		this.condition = comp.parseVariable(condition);
		this.block = block;
		this.info = info;
		this.filename = filename;
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter) throws ExecutionException {
		Label start = new Label();
		Label end = new Label();
		
		methodWriter.putLabel(start);
		condition.visitMethod(methodWriter);
		methodWriter.booleanValue();
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, end);
		
		comp.compile(methodWriter, classWriter, info, block, filename, end);
		methodWriter.pop();
		
		methodWriter.jumpto(start);
		
		methodWriter.putLabel(end);
		
	}
	
}
