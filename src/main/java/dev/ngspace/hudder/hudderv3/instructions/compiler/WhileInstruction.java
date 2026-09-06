package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class WhileInstruction extends Instruction {

	private ExpressionVisitor condition;
	private TokenizedCodeBlock codeblock;
	
	public WhileInstruction(String condition, String block, AV3Compiler comp, String filename, TextPos pos)
			throws CompileException {
		super(pos);
		this.condition = comp.parseVariable(condition, pos);
		this.codeblock = comp.compile(block, filename, pos);
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		
		methodWriter.getHelper();
		methodWriter.call(HudderV3Helper.class, "getMaxWhile", false, Integer.TYPE);
		int limit_index = methodWriter.istore();
		
		Label start = new Label();
		Label end = new Label();
		Label user_code = new Label();
		Label error = new Label();
		
		methodWriter.putLabel(start);
		condition.visit(methodWriter);
		methodWriter.checkcastSafe(Boolean.class, pos);
		methodWriter.ensureNotNull("Condition can not be null!", pos);
		methodWriter.booleanValue();
		methodWriter.ifeq(end);
		
		methodWriter.iload(limit_index);
		methodWriter.loadConstantUnsafe(1);
		methodWriter.isub();
		methodWriter.dup();
		methodWriter.istore(limit_index);
		methodWriter.ifeq(error);
		methodWriter.jumpto(user_code);
		methodWriter.putLabel(error);
		methodWriter.throwExecutionException("Max while loop reached", pos);
		
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
