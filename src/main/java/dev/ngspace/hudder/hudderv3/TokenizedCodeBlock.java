package dev.ngspace.hudder.hudderv3;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.instructions.compiler.Instruction;
import dev.ngspace.hudder.hudderv3.instructions.compiler.StringInstruction;

public class TokenizedCodeBlock {
	
	private List<Instruction> instructions = new ArrayList<Instruction>();
	protected AV3Compiler comp;
	
	public TokenizedCodeBlock(AV3Compiler compiler) {
		this.comp = compiler;
	}
	
	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}
	
	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	public void writeInstructions(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter,
			Label breakLabel) throws CompileException {
		for (Instruction ins : instructions) {
			ins.visit(methodWriter, classWriter, breakLabel);
		}
	}

	public void appendStringConstant(String string) {
		addInstruction(new StringInstruction(string));
	}
	
	public boolean canReturnValue() {
		for (Instruction ins : instructions) {
			if (ins.canReturnValue()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesReturnValue() {
		for (Instruction ins : instructions) {
			if (ins.doesReturnValue()) {
				return true;
			}
		}
		return false;
	}
}