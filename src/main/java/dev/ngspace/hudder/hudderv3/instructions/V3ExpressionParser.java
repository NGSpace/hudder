package dev.ngspace.hudder.hudderv3.instructions;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public interface V3ExpressionParser {
	public ExpressionVisitor parseExpression(String valuee, AV3Compiler comp, TextPos pos) throws CompileException;
}
