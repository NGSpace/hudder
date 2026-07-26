package dev.ngspace.hudder.hudderv3.v3variableinstructions.operations.booloperations;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;

public class ComparisionVariableVisitor extends VariableVisitor {
	
	
	private String val1;
	private String val2;
	private String operator;
	public ComparisionVariableVisitor(HudderV3Compiler comp, String val1, String val2, String operator) {
		super(comp);
		this.val1 = val1;
		this.val2 = val2;
		this.operator = operator;
	}
	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		comp.parseVariable(val1).visitMethod(methodWriter);
		int val1index = methodWriter.astore();
		comp.parseVariable(val2).visitMethod(methodWriter);
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
