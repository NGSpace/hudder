package dev.ngspace.hudder.hudderv3.instructions.variables;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;

public abstract class VariableVisitor {
	
	public AV3Compiler comp;
	protected VariableVisitor(AV3Compiler comp) {
		this.comp = comp;
	}
	
	public abstract void visitMethod(V3MethodWriter methodWriter) throws ExecutionException;
}
