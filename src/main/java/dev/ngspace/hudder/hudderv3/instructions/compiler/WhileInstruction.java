package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

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
	private boolean has_limits;
	
	public WhileInstruction(String condition, String block, AV3Compiler comp,
			HudderConfig info, String filename, TextPos pos) throws CompileException {
		super(pos);
		this.condition = comp.parseVariable(condition, pos);
		this.codeblock = comp.compile(info, block, filename, pos);
		this.has_limits = !info.unsafeoperations();
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		
		int limit_index = -1;
		if (has_limits) {
			methodWriter.loadConstantUnsafe(Short.MAX_VALUE);
			limit_index = methodWriter.istore();
		}
		
		Label start = new Label();
		Label end = new Label();
		Label user_code = new Label();
		
		methodWriter.putLabel(start);
		condition.visit(methodWriter);
		methodWriter.booleanValue();
		methodWriter.ifeq(end);
		if (has_limits) {
			Label error = new Label();
			methodWriter.iload(limit_index);
			methodWriter.loadConstantUnsafe(1);
			methodWriter.isub();
			methodWriter.dup();
			methodWriter.istore(limit_index);
			methodWriter.ifeq(error);
			methodWriter.jumpto(user_code);
			methodWriter.putLabel(error);
			methodWriter.throwExecutionException("Max while loop reached", pos);
		}
		
		methodWriter.putLabel(user_code);
		
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
