package dev.ngspace.hudder.hudderv3.instructions.variables;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;

public class SystemVariableVisitor extends ExpressionVisitor {

	public String value;

	public SystemVariableVisitor(AV3Compiler comp, String variable, TextPos pos, String expression) {
		super(comp, pos, expression);
		this.value = variable;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.tryCatchBlock(_->{
			methodWriter.loadConstant(value.toLowerCase());
			methodWriter.callStatic(DataVariableRegistry.class, "getAny", "(Ljava/lang/String;)Ljava/lang/Object;", false);
		}, _->methodWriter.throwExecutionExceptionFromCaughtException(pos), Exception.class);
	}
	
}
