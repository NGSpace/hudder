package dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class ComparisionVariableVisitor extends ExpressionVisitor {
	
	
	private ExpressionVisitor val1;
	private ExpressionVisitor val2;
	private String operator;
	public ComparisionVariableVisitor(AV3Compiler comp, String val1, String val2, String operator, TextPos pos)
			throws CompileException {
		super(comp, pos);
		this.val1 = comp.parseVariable(val1, pos);
		this.val2 = comp.parseVariable(val2, pos);
		this.operator = operator;
	}
	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		val1.visit(methodWriter);
		int val1index = methodWriter.astore();
		val2.visit(methodWriter);
		int val2index = methodWriter.astore();
		methodWriter.aload(val1index);
		methodWriter.aload(val2index);
		methodWriter.loadConstant(operator);
		methodWriter.callStatic(HudderV3Helper.class, "compare",
				"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)Z", false);
	    methodWriter.callStatic(
				Boolean.class,
				"valueOf",
				"(Z)Ljava/lang/Boolean;",
				false
			);
	}
	
}
