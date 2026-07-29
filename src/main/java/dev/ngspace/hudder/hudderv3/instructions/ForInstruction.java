package dev.ngspace.hudder.hudderv3.instructions;

import java.util.Iterator;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class ForInstruction extends Instruction {

	private String block;
	private HudderV3Compiler comp;
	private HudderConfig info;
	private String filename;
	private String variable_name;
	private VariableVisitor value;

	public ForInstruction(String variable_name, String value, String block, HudderV3Compiler comp,
			HudderConfig info, String filename) throws ExecutionException {
		this.variable_name = variable_name;
		this.value = comp.parseVariable(value);
		this.block = block;
		this.comp = comp;
		this.info = info;
		this.filename = filename;
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws ExecutionException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		Label start = new Label();
		Label end = new Label();
		methodWriter.defineVariable(variable_name);
		
		value.visit(methodWriter);
		methodWriter.callInterface(Iterable.class, "iterator", "()Ljava/util/Iterator;");
		int iterator_index = methodWriter.astore();
		
		methodWriter.putLabel(start);
		methodWriter.aload(iterator_index);
		methodWriter.callInterface(Iterator.class, "hasNext", "()Z");
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, end);

		methodWriter.aload(iterator_index);
		methodWriter.callInterface(Iterator.class, "next", "()Ljava/lang/Object;");
		methodWriter.storeVariable(variable_name);
		
		comp.compile(methodWriter, classWriter, info, block, filename, end);
		
		methodWriter.jumpto(start);
		
		methodWriter.putLabel(end);
		
		methodWriter.setBuilderDisabled(builderdisabled);
		
	}
	
}
