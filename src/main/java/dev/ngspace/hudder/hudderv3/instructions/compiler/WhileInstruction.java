package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class WhileInstruction extends Instruction {

	private VariableVisitor condition;
	private TokenizedCodeBlock codeblock;
	
	public WhileInstruction(String condition, String block, AV3Compiler comp,
			HudderConfig info, String filename, TextPos pos) throws CompileException {
		super(pos);
		this.condition = comp.parseVariable(condition);
		this.codeblock = comp.compile(info, block, filename, pos);
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
		
		codeblock.writeInstructions(methodWriter, classWriter, end);
		
		methodWriter.jumpto(start);
		
		methodWriter.putLabel(end);
		
		methodWriter.setBuilderDisabled(builderdisabled);
	}
	
	@Override
	public boolean canReturnValue() {
		return codeblock.canReturnValue();
	}
	
	@Override
	public boolean doesReturnValue() {
		return false;
	}
	
}
