package dev.ngspace.hudder.hudderv3.instructions;

import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class ArrayReadVariableVisitor extends VariableVisitor {

	private VariableVisitor indexValue;
	private VariableVisitor array;

	public ArrayReadVariableVisitor(AV3Compiler comp, String value) throws ExecutionException {
		super(comp);
		int indexstart = value.lastIndexOf('[');
		String index = value.substring(indexstart+1,value.length()-1);
		indexValue = comp.parseVariable(index);
		array = comp.parseVariable(value.substring(0, indexstart));
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		Label normalarray = new Label();
		Label end = new Label();
		
		array.visit(methodWriter);
		
		methodWriter.dup();
		
		methodWriter.methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(List.class));
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, normalarray);
		
		methodWriter.methodVisitor.visitTypeInsn(
		        Opcodes.CHECKCAST,
		        Type.getInternalName(List.class)
		);
		indexValue.visit(methodWriter);
		methodWriter.intValue();
		methodWriter.callInterface(List.class, "get", "(I)Ljava/lang/Object;");
		
		methodWriter.jumpto(end);

		methodWriter.putLabel(normalarray);
		
		methodWriter.methodVisitor.visitTypeInsn(
		        Opcodes.CHECKCAST,
		        Type.getDescriptor(Object[].class)
		);
		indexValue.visit(methodWriter);
		methodWriter.intValue();
		methodWriter.aaload();
		
		methodWriter.putLabel(end);
	}
	
	
	// Should translate to:
	/*
	 * 	List list = (List)array;
		if (list.size()-index.intValue()==0) {
			list.add(value);
		} else if (index>list.size()) {
			throw new ExecutionException("You can't set value " + index + " of array before all previous points are set",line,charpos);
		} else list.set(index, value);
	 */
	@Override
	public void visitSetValue(V3MethodWriter methodWriter) throws ExecutionException {
		Label end = new Label();
		Label add = new Label();
		Label set = new Label();
		
		int value_index = methodWriter.astore();
		
		array.visit(methodWriter);
		
		methodWriter.methodVisitor.visitTypeInsn(
		        Opcodes.CHECKCAST,
		        Type.getInternalName(List.class)
		);
		methodWriter.dup();
		int list_index = methodWriter.astore();
		
		methodWriter.callInterface(List.class, "size", "()I");
		indexValue.visit(methodWriter);
		methodWriter.intValue();
		methodWriter.dup();
		int index_index = methodWriter.astore();
		methodWriter.methodVisitor.visitInsn(Opcodes.ISUB);
		methodWriter.dup();
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, add);
		
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFGT, set);
		
		methodWriter.throwRuntimeException("Index out of bounds of array!");

		methodWriter.putLabel(set);

		methodWriter.aload(list_index);
		methodWriter.aload(index_index);
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
	}
}
