package dev.ngspace.hudder.hudderv3.v3variableinstructions;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;

public abstract class VariableVisitor {
	
	public HudderV3Compiler comp;
	protected VariableVisitor(HudderV3Compiler comp) {
		this.comp = comp;
	}
	
	public abstract void visitMethod(V3MethodWriter methodWriter) throws ExecutionException;
}
