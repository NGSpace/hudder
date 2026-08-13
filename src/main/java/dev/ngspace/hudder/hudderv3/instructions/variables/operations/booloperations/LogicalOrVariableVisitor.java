package dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations;

import java.util.List;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class LogicalOrVariableVisitor extends ExpressionVisitor {

	private List<ExpressionVisitor> values;

	public LogicalOrVariableVisitor(List<ExpressionVisitor> values, AV3Compiler comp, TextPos pos) {
		super(comp, pos);
		this.values = values;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		Label end = new Label();
		Label true_value = new Label();
		
		for (int i = 0;i<values.size();i++) {
			values.get(i).visit(methodWriter);
			methodWriter.checkcastSafe(Boolean.class, pos);
			methodWriter.booleanValue();
			methodWriter.ifne(true_value);
		}
		methodWriter.loadConstant(false);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(true_value);
		
		methodWriter.loadConstant(true);
		
		methodWriter.putLabel(end);
	}
	
}
