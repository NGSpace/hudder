package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;

public abstract class Instruction {
	public abstract void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter,
			Label breaklabel) throws CompileException;
	
	public boolean canReturnValue() {
		return false;
	}
	
	public boolean doesReturnValue() {
		return false;
	}
}
