package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import java.util.List;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class ArrayReadVariableVisitor extends ExpressionVisitor {

	private ExpressionVisitor indexValue;
	private ExpressionVisitor array;

	public ArrayReadVariableVisitor(AV3Compiler comp, String value, TextPos pos, String expression) throws CompileException {
		super(comp, pos, expression);
		int indexstart = value.lastIndexOf('[');
		String index = value.substring(indexstart+1,value.length()-1);
		indexValue = comp.parseVariable(index, pos);
		array = comp.parseVariable(value.substring(0, indexstart), pos);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		Label normalarray = new Label();
		Label end = new Label();
		
		array.visit(methodWriter);
		
		methodWriter.dup();
		
		methodWriter.instanceOf(List.class);
		methodWriter.ifeq(normalarray);
		
		methodWriter.checkcast(List.class);
		indexValue.visit(methodWriter);
		methodWriter.checkcastSafe(Number.class, pos);
		methodWriter.intValue();
		methodWriter.callInterface(List.class, "get", "(I)Ljava/lang/Object;");
		
		methodWriter.jumpto(end);

		methodWriter.putLabel(normalarray);

		methodWriter.checkcastSafe(Object[].class, pos);
		indexValue.visit(methodWriter);
		methodWriter.checkcastSafe(Number.class, pos);
		methodWriter.intValue();
		methodWriter.aaload();
		
		methodWriter.putLabel(end);
	}
	
	
	@Override
	public void visitSetValue(V3MethodWriter methodWriter) throws CompileException {
		Label end = new Label();
		Label add = new Label();
		Label set = new Label();
		
		int value_index = methodWriter.astore();
		
		array.visit(methodWriter);

		methodWriter.checkcastSafe(List.class, pos, "Array");
		methodWriter.dup();
		int list_index = methodWriter.astore();
		
		methodWriter.callInterface(List.class, "size", "()I");
		indexValue.visit(methodWriter);
		methodWriter.checkcastSafe(Number.class, pos);
		methodWriter.intValue();
		methodWriter.dup();
		int index_index = methodWriter.istore();
		methodWriter.isub();
		methodWriter.dup();
		methodWriter.ifeq(add);
		
		methodWriter.ifgt(set);
		
		methodWriter.throwRuntimeException("Index out of bounds of array!");

		methodWriter.putLabel(set);

		methodWriter.aload(list_index);
		methodWriter.iload(index_index);
		methodWriter.aload(value_index);
		methodWriter.callInterface(List.class, "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
		methodWriter.pop();
		methodWriter.jumpto(end);

		methodWriter.putLabel(add);
		
		methodWriter.pop();
		methodWriter.aload(list_index);
		methodWriter.aload(value_index);
		methodWriter.callInterface(List.class, "add", "(Ljava/lang/Object;)Z");
		methodWriter.pop();
		
		methodWriter.putLabel(end);
		
		methodWriter.aload(value_index);
	}
}
