package dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations;

import java.util.List;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class LogicalAndVariableVisitor extends ExpressionVisitor {

	private List<ExpressionVisitor> values;

	public LogicalAndVariableVisitor(List<ExpressionVisitor> values, AV3Compiler comp, TextPos pos, String expression) {
		super(comp, pos, expression);
		this.values = values;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		Label end = new Label();
		Label false_value = new Label();
		
		for (int i = 0;i<values.size();i++) {
			values.get(i).visit(methodWriter);
			methodWriter.checkcastSafe(Boolean.class, pos);
			methodWriter.booleanValue();
			methodWriter.ifeq(false_value);
		}
		methodWriter.loadConstant(true);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(false_value);
		
		methodWriter.loadConstant(false);
		
		methodWriter.putLabel(end);
	}
	
}
