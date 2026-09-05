package dev.ngspace.hudder.hudderv3.instructions.compiler;

import java.util.Iterator;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class ForInstruction extends Instruction {

	private TokenizedCodeBlock block;
	private String variable_name;
	private ExpressionVisitor value;

	public ForInstruction(String variable_name, String value, String block, AV3Compiler comp, String filename,
			TextPos pos) throws CompileException {
		super(pos);
		this.variable_name = variable_name;
		this.value = comp.parseVariable(value, pos);
		this.block = comp.compile(block, filename, pos);
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		Label start = new Label();
		Label end = new Label();
		String localVariableName = variable_name.toLowerCase();

		value.visit(methodWriter);
		methodWriter.ensureNotNull("Can not iterate over null value!", pos);
		methodWriter.checkcastSafe(Iterable.class, pos);
		methodWriter.callInterface(Iterable.class, "iterator", Iterator.class);
		int iterator_index = methodWriter.astore();
		
		methodWriter.putLabel(start);
		methodWriter.aload(iterator_index);
		methodWriter.callInterface(Iterator.class, "hasNext", Boolean.TYPE);
		methodWriter.ifeq(end);

		methodWriter.aload(iterator_index);
		Integer previousVariableIndex = methodWriter.defineScopedVariable(localVariableName);
		methodWriter.callInterface(Iterator.class, "next", Object.class);
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
