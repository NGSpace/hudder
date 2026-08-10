package dev.ngspace.hudder.hudderv3;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public interface V3VariableProcessor {

	public VariableVisitor parseVariable(String valuee, AV3Compiler comp, TextPos pos) throws CompileException;
}
