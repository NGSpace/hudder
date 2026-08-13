package dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class NegateVariableVisitor extends ExpressionVisitor {

	private ExpressionVisitor value;

	public NegateVariableVisitor(AV3Compiler comp, String value, TextPos pos) throws CompileException {
		super(comp, pos);
		this.value = comp.parseVariable(value, pos);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		value.visit(methodWriter);
		methodWriter.checkcast(Boolean.class);
		methodWriter.booleanValue();
		Label end = new Label();
		Label false_result = new Label();
		
		methodWriter.ifne(false_result);
		methodWriter.loadConstant(true);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(false_result);
		methodWriter.loadConstant(false);
		
		methodWriter.putLabel(end);
	}
	
}
