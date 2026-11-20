package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesManager;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesProvider;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;
import org.objectweb.asm.Label;

import java.util.Locale;

public class DynamicVariableVisitor extends ExpressionVisitor {

	public String variable;

	public DynamicVariableVisitor(AV3Compiler comp, String variable, TextPos pos, String expression) {
		super(comp, pos, expression);
		this.variable = variable.toLowerCase();
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		if (methodWriter.hasVariable(variable)) {
			methodWriter.getVariable(variable);
		} else {
			methodWriter.nullConstant();
		}
	}
	
	@Override
	public void visitSetValue(V3MethodWriter methodWriter) {
		if (!methodWriter.hasVariable(variable))
			methodWriter.defineVariableWithValue(variable, false);
		else
			methodWriter.storeVariable(variable);
		methodWriter.getVariable(variable);
	}
}
