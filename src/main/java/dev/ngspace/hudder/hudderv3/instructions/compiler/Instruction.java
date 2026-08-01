package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;

public abstract class Instruction {
	
	protected TextPos pos;

	protected Instruction(TextPos pos) {
		this.pos = pos;
	}
	
	public abstract void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter,
			Label breaklabel) throws CompileException;
	
	public boolean canReturnValue() {
		return false;
	}
	
	public boolean doesReturnValue() {
		return false;
	}
}
