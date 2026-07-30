package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;

public class VariableInstruction extends Instruction {

	public AV3Compiler comp;
	public String variable;
	
	public VariableInstruction(AV3Compiler comp, String variable) {
		this.comp = comp;
		this.variable = variable;
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		if ("break".equalsIgnoreCase(variable.trim())) {
			methodWriter.jumpto(breaklabel);
		} else {
			comp.parseVariable(variable).visit(methodWriter);
			methodWriter.appendToBuilderAndPop();
		}
	}
	
}
