package dev.ngspace.hudder.hudderv3.instructions;

import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;

public abstract class Instruction {
	public abstract void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter) throws ExecutionException;
}
