package dev.ngspace.hudder.hudderv3.instructions.variables.modifiable;

import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class ArrayReadVariableVisitor extends VariableVisitor {

	private VariableVisitor indexValue;
	private VariableVisitor array;

	public ArrayReadVariableVisitor(AV3Compiler comp, String value) throws CompileException {
		super(comp);
		int indexstart = value.lastIndexOf('[');
		String index = value.substring(indexstart+1,value.length()-1);
		indexValue = comp.parseVariable(index);
		array = comp.parseVariable(value.substring(0, indexstart));
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		Label normalarray = new Label();
		Label end = new Label();
		
		array.visit(methodWriter);
		
		methodWriter.dup();
		
		methodWriter.methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(List.class));
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, normalarray);
		
		methodWriter.checkcast(List.class);
		indexValue.visit(methodWriter);
		methodWriter.checkcast(Number.class);
		methodWriter.intValue();
		methodWriter.callInterface(List.class, "get", "(I)Ljava/lang/Object;");
		
		methodWriter.jumpto(end);

		methodWriter.putLabel(normalarray);

		methodWriter.checkcast(Object[].class);
		indexValue.visit(methodWriter);
		methodWriter.checkcast(Number.class);
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

		methodWriter.checkcast(List.class);
		methodWriter.dup();
		int list_index = methodWriter.astore();
		
		methodWriter.callInterface(List.class, "size", "()I");
		indexValue.visit(methodWriter);
		methodWriter.checkcast(Number.class);
		methodWriter.intValue();
		methodWriter.dup();
		int index_index = methodWriter.istore();
		methodWriter.methodVisitor.visitInsn(Opcodes.ISUB);
		methodWriter.dup();
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, add);
		
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFGT, set);
		
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
