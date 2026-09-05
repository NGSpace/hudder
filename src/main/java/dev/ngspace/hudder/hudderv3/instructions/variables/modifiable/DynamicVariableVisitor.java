package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesManager;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesProvider;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
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
			methodWriter.callInterface(VariablesProvider.class, "getVariable", Object.class, String.class);
		}
	}
	
	@Override
	public void visitSetValue(V3MethodWriter methodWriter) {
		int valueindex = methodWriter.astore();
		methodWriter.aload(0);
		methodWriter.loadConstant(variable.toLowerCase());
		methodWriter.aload(valueindex);
		methodWriter.callInterface(VariablesManager.class, "putVariable", null, String.class, Object.class);
		methodWriter.aload(valueindex);
	}
}
