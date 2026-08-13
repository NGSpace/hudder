package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import java.util.Map;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class TemporaryVariableVisitor extends ExpressionVisitor {

	public String variable;

	public TemporaryVariableVisitor(AV3Compiler comp, String variable, TextPos pos) {
		super(comp, pos);
		this.variable = variable.toLowerCase();
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		if (methodWriter.hasVariable(variable)) {
			methodWriter.getVariable(variable);
		} else {
			Label end = new Label();
			methodWriter.getStaticField("tempVariables", AVarTextCompiler.class, Map.class);
			methodWriter.loadConstant(variable);
			methodWriter.callInterface(Map.class, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
			methodWriter.dup();
			methodWriter.ifnonnull(end);
			methodWriter.pop();
			methodWriter.loadConstant(0d);
			methodWriter.putLabel(end);
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
			methodWriter.callInterface(Map.class, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
			methodWriter.pop();
			methodWriter.aload(index);
		}
	}
}
