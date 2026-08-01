package dev.ngspace.hudder.hudderv3.instructions.compiler;

import java.util.Iterator;

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

public class ForInstruction extends Instruction {

	private TokenizedCodeBlock block;
	private String variable_name;
	private VariableVisitor value;

	public ForInstruction(String variable_name, String value, String block, AV3Compiler comp,
			HudderConfig info, String filename, TextPos pos) throws CompileException {
		super(pos);
		this.variable_name = variable_name;
		this.value = comp.parseVariable(value);
		this.block = comp.compile(info, block, filename, pos);
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		Label start = new Label();
		Label end = new Label();
		String localVariableName = variable_name.toLowerCase();
		Integer previousVariableIndex = methodWriter.defineScopedVariable(localVariableName);

		value.visit(methodWriter);
		methodWriter.callInterface(Iterable.class, "iterator", "()Ljava/util/Iterator;");
		int iterator_index = methodWriter.astore();
		
		methodWriter.putLabel(start);
		methodWriter.aload(iterator_index);
		methodWriter.callInterface(Iterator.class, "hasNext", "()Z");
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, end);

		methodWriter.aload(iterator_index);
		methodWriter.callInterface(Iterator.class, "next", "()Ljava/lang/Object;");
		methodWriter.storeVariable(localVariableName);
		
		block.writeInstructions(methodWriter, classWriter, end);
		
		methodWriter.jumpto(start);
		
		methodWriter.putLabel(end);
		// The slot is valid only on control-flow paths that enter this loop.
		// Do not let later instructions resolve the same dynamic name to it.
		methodWriter.restoreScopedVariable(localVariableName, previousVariableIndex);
		methodWriter.setBuilderDisabled(builderdisabled);
	}
	
	@Override
	public boolean canReturnValue() {
		return block.canReturnValue();
	}
	
	@Override
	public boolean doesReturnValue() {
		return false;
	}
	
}
