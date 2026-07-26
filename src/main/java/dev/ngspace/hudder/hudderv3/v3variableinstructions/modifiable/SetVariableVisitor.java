package dev.ngspace.hudder.hudderv3.v3variableinstructions.modifiable;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;

public class SetVariableVisitor extends VariableVisitor {

	private final String variable_name;
	private final String value;

	public SetVariableVisitor(HudderV3Compiler comp, String variable_name, String value) {
		super(comp);
		this.variable_name = variable_name;
		this.value = value;
	}

	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		comp.parseVariable(value).visitMethod(methodWriter);
		int valueindex = methodWriter.astore();
		methodWriter.aload(0);
		methodWriter.loadConstant(variable_name);
		methodWriter.aload(valueindex);
		methodWriter.call(AVarTextCompiler.class, "put", "(Ljava/lang/String;Ljava/lang/Object;)V",
				false);
		methodWriter.aload(valueindex);
	}
	
}
