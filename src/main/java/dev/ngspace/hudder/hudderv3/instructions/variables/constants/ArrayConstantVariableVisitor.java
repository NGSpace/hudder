package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import java.util.ArrayList;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class ArrayConstantVariableVisitor extends VariableVisitor {

	private VariableVisitor[] values;

	public ArrayConstantVariableVisitor(String[] strings, AV3Compiler comp) throws ExecutionException {
		super(comp);
		values = new VariableVisitor[strings.length];
		for (int i = 0;i<strings.length;i++)
			values[i] = comp.parseVariable(strings[i]);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		methodWriter.newAndDup(ArrayList.class);
		methodWriter.loadConstantUnsafe(values.length);
		methodWriter.callInit(ArrayList.class, "(I)V");
		for (int i = 0;i<values.length;i++) {
			methodWriter.dup();
			values[0].visit(methodWriter);
			methodWriter.call(ArrayList.class, "add", "(Ljava/lang/Object;)Z", false);
			methodWriter.pop();// Add returns a boolean, burn it.
		}
	}
	
}
