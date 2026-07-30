package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class WhileInstruction extends Instruction {

	private AV3Compiler comp;
	private VariableVisitor condition;
	private String block;
	private HudderConfig info;
	private String filename;

	public WhileInstruction(String condition, String block, AV3Compiler comp,
			HudderConfig info, String filename) throws CompileException {
		System.out.println(comp);
		this.comp = comp;
		this.condition = comp.parseVariable(condition);
		this.block = block;
		this.info = info;
		this.filename = filename;
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		Label start = new Label();
		Label end = new Label();
		
		methodWriter.putLabel(start);
		condition.visit(methodWriter);
		methodWriter.booleanValue();
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, end);
		
		comp.compile(info, block, filename).writeInstructions(methodWriter, classWriter, breaklabel);
		
		methodWriter.jumpto(start);
		
		methodWriter.putLabel(end);
		
		methodWriter.setBuilderDisabled(builderdisabled);
		
	}
	
}
