package dev.ngspace.hudder.hudderv3.v3variableinstructions.modifiable;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;

public class DynamicVariableVisitor extends VariableVisitor {

	public String value;

	public DynamicVariableVisitor(HudderV3Compiler comp, String variable) {
		super(comp);
		this.value = variable;
	}

	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		methodWriter.aload(0);
		methodWriter.loadConstant(value.toLowerCase());
		methodWriter.call(AVarTextCompiler.class, "get", "(Ljava/lang/String;)Ljava/lang/Object;", false);
		int index = methodWriter.astore();
		methodWriter.aload(index);
	}
	
	
}
