package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class DynamicVariableVisitor extends ExpressionVisitor {

	public String variable;

	public DynamicVariableVisitor(AV3Compiler comp, String variable, TextPos pos, String expression) {
		super(comp, pos, expression);
		this.variable = variable;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		if (methodWriter.hasVariable(variable.toLowerCase())) {
			methodWriter.getVariable(variable.toLowerCase());
		} else {
			methodWriter.aload(0);
			methodWriter.loadConstant(variable.toLowerCase());
			methodWriter.call(AVarTextCompiler.class, "get", "(Ljava/lang/String;)Ljava/lang/Object;", false);
		}
	}
	
	@Override
	public void visitSetValue(V3MethodWriter methodWriter) {
		int valueindex = methodWriter.astore();
		methodWriter.aload(0);
		methodWriter.loadConstant(variable.toLowerCase());
		methodWriter.aload(valueindex);
		methodWriter.call(AVarTextCompiler.class, "put", "(Ljava/lang/String;Ljava/lang/Object;)V",
				false);
		methodWriter.aload(valueindex);
	}
}
