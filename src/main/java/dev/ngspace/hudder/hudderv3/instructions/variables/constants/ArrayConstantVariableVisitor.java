package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import java.util.ArrayList;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class ArrayConstantVariableVisitor extends ExpressionVisitor {

	private ExpressionVisitor[] values;

	public ArrayConstantVariableVisitor(String[] strings, AV3Compiler comp, TextPos pos, String expression) throws CompileException {
		super(comp, pos, expression);
		values = new ExpressionVisitor[strings.length];
		for (int i = 0;i<strings.length;i++)
			values[i] = comp.parseVariable(strings[i], pos);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.newAndDup(ArrayList.class);
		methodWriter.loadConstantUnsafe(values.length);
		methodWriter.callInit(ArrayList.class, Integer.TYPE);
		for (int i = 0;i<values.length;i++) {
			methodWriter.dup();
			values[i].visit(methodWriter);
			methodWriter.call(ArrayList.class, "add", false, Boolean.TYPE, Object.class);
			methodWriter.pop();// Add returns a boolean, burn it.
		}
	}
	
}
