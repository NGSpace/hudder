package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class TernaryVariableVisitor extends ExpressionVisitor {

	private ExpressionVisitor condition;
	private ExpressionVisitor truevalue;
	private ExpressionVisitor falsevalue;

	public TernaryVariableVisitor(AV3Compiler comp, String condition, String truevalue, String falsevalue,
			TextPos pos, String expression) throws CompileException {
		super(comp, pos, expression);
		this.condition = comp.parseVariable(condition, pos);
		this.truevalue = comp.parseVariable(truevalue, pos);
		this.falsevalue = comp.parseVariable(falsevalue, pos);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		Label end = new Label();
		Label label = new Label();
		
		condition.visit(methodWriter);
		methodWriter.checkcastSafe(Boolean.class, pos);
		methodWriter.ensureNotNull("Condition can not be null!", pos);
		methodWriter.booleanValue();
		
		methodWriter.ifne(label);
		falsevalue.visit(methodWriter);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(label);
		truevalue.visit(methodWriter);
		
		methodWriter.putLabel(end);
	}
	
}
