package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class TemporaryVariableVisitor extends VariableVisitor {

	public String variable;

	public TemporaryVariableVisitor(AV3Compiler comp, String variable) {
		super(comp);
		this.variable = variable;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		if (methodWriter.hasVariable(variable.toLowerCase())) {
			methodWriter.getVariable(variable.toLowerCase());
		} else {
			methodWriter.nullConstant();
		}
	}
	
	@Override
	public void visitSetValue(V3MethodWriter methodWriter) {
//		int valueindex = methodWriter.astore();
		methodWriter.dup();
		if (!methodWriter.hasVariable(variable.toLowerCase())) {
			methodWriter.defineVariable(variable.toLowerCase());
		}
		methodWriter.storeVariable(variable.toLowerCase());
//		methodWriter.aload(0);
//		methodWriter.loadConstant(variable.toLowerCase());
//		methodWriter.aload(valueindex);
//		methodWriter.call(AVarTextCompiler.class, "put", "(Ljava/lang/String;Ljava/lang/Object;)V",
//				false);
//		methodWriter.aload(valueindex);
	}
}
