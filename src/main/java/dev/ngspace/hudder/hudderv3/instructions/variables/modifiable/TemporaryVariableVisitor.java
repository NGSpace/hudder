package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import java.util.Map;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class TemporaryVariableVisitor extends VariableVisitor {

	public String variable;

	public TemporaryVariableVisitor(AV3Compiler comp, String variable) {
		super(comp);
		this.variable = variable.toLowerCase();
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		if (methodWriter.hasVariable(variable)) {
			methodWriter.getVariable(variable);
		} else {
			methodWriter.getStaticField("tempVariables", AVarTextCompiler.class, Map.class);
			methodWriter.loadConstant(variable);
			methodWriter.call(Map.class, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
		}
	}
	
	@Override
	public void visitSetValue(V3MethodWriter methodWriter) {
		if (methodWriter.hasVariable(variable)) {
			methodWriter.dup();
			methodWriter.storeVariable(variable);
		} else {
			int index = methodWriter.astore();
			methodWriter.getStaticField("tempVariables", AVarTextCompiler.class, Map.class);
			methodWriter.loadConstant(variable);
			methodWriter.aload(index);
			methodWriter.call(Map.class, "set", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
			methodWriter.pop();
			methodWriter.aload(index);
		}
	}
}
