package dev.ngspace.hudder.hudderv3;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;

public class V3MethodWriter {
	
	public static final String STRING_BUILDER = Type.getInternalName(StringBuilder.class);
	public static final String VAR_REGISTRY = Type.getInternalName(DataVariableRegistry.class);
	
	public V3ClassWriter classWriter;
	public MethodVisitor methodVisitor;
	public int variableindex = 0;
	public String methodName;

	public V3MethodWriter(V3ClassWriter classWriter, String name, Class<?>[] parameters, Class<?> returntype, String signature,
			String[] exceptions) {
		this.classWriter = classWriter;
		this.methodName = name;
		
		this.methodVisitor = classWriter.classWriter.visitMethod(Opcodes.ACC_PUBLIC, name,
				Type.getMethodDescriptor(Type.getType(returntype),
						List.of(parameters).stream().map(Type::getType).toList().toArray(new Type[0])),
				signature, exceptions);
		methodVisitor.visitCode();
	}

	public void getField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classWriter.classname,
				name, Type.getDescriptor(type));
	}

	public void aload(int index) {
		methodVisitor.visitVarInsn(Opcodes.ALOAD, index);
	}
	public void lload(int index) {
		methodVisitor.visitVarInsn(Opcodes.LLOAD, index);
	}
	public void dload(int index) {
		methodVisitor.visitVarInsn(Opcodes.DLOAD, index);
	}
	public void aloadDouble(int index) {
		methodVisitor.visitVarInsn(Opcodes.ALOAD, index);

		methodVisitor.visitTypeInsn(
		    Opcodes.CHECKCAST,
		    Type.getInternalName(Number.class)
		);
		
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Number",
				"doubleValue",
				"()D",
				false
		);
	}
	
	public void loadConstant(Object constant) {
		methodVisitor.visitLdcInsn(constant);
	}
	public void loadConstant(double constant) {
		if (constant%1==0) {
			loadConstant((long)constant);
		} else {
			methodVisitor.visitLdcInsn(constant);
			//Convert to Object
			methodVisitor.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					"java/lang/Double",
					"valueOf",
					"(D)Ljava/lang/Double;",
					false
			);
		}
	}
	public void loadConstant(long constant) {
		methodVisitor.visitLdcInsn(constant);
		//Convert to Object
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"java/lang/Long",
				"valueOf",
				"(J)Ljava/lang/Long;",
				false
		);
	}
	public void loadConstant(boolean constant) {
		methodVisitor.visitLdcInsn(constant);
		//Convert to Object
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"java/lang/Boolean",
				"valueOf",
				"(Z)Ljava/lang/Boolean;",
				false
			);
	}
	
	public int astore() {
		methodVisitor.visitVarInsn(Opcodes.ASTORE, ++variableindex);
		return variableindex;
	}
	
	public int lstore() {
		methodVisitor.visitVarInsn(Opcodes.LSTORE, ++variableindex);
		return variableindex;
	}
	
	public int dstore() {
		methodVisitor.visitVarInsn(Opcodes.DSTORE, ++variableindex);
		return variableindex;
	}
	
	
	public void pop() {
		methodVisitor.visitInsn(Opcodes.POP);
	}

	public void callDataVariableRegistry(String variable) {
		loadConstant(variable.toLowerCase());
		callDataVariableRegistry();
	}

	public void callDataVariableRegistry() {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, VAR_REGISTRY, "getAny",
				"(Ljava/lang/String;)Ljava/lang/Object;", false);
	}

	public void callStatic(Class<?> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}

	public void call(Class<?> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}

	public void callSelf(String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classWriter.classname, name, descriptor,
				isInterface);
	}

	public void getStatic(Class<?> clazz, String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(clazz), name,
				Type.getInternalName(type));
	}

	public void addAReturn() {
		methodVisitor.visitInsn(Opcodes.ARETURN);
	}

	public void putLabel(Label label) {
		methodVisitor.visitLabel(label);
	}

	public void end() {
		end(Opcodes.RETURN);
	}

	public void end(int Opcode) {
		methodVisitor.visitInsn(Opcode);
		
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}

	public void complexMath(V3VariableProcessor processor, HudderV3Compiler comp, String[] values,
			char[] operations) throws ExecutionException {
		int[] value_indexes = new int[values.length];
		// Is String
		methodVisitor.visitLdcInsn(false);
		methodVisitor.visitVarInsn(Opcodes.ISTORE, ++variableindex);
		int is_string_index = variableindex;
		
		for (int i = 0;i<values.length;i++) {
			Label isNumber = new Label();
			processor.parseVariable(this, values[i], comp);
			value_indexes[i] = astore();
			aload(value_indexes[i]);
			methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(Number.class));
			methodVisitor.visitJumpInsn(Opcodes.IFNE, isNumber);

			methodVisitor.visitLdcInsn(true);
			methodVisitor.visitVarInsn(Opcodes.ISTORE, is_string_index);
			
			methodVisitor.visitLabel(isNumber);
		}
		
		methodVisitor.visitVarInsn(Opcodes.ILOAD, is_string_index);
		Label mathOperation = new Label();
		Label end = new Label();
		methodVisitor.visitJumpInsn(Opcodes.IFEQ, mathOperation);

		// Create StringBuilder
		methodVisitor.visitTypeInsn(Opcodes.NEW, STRING_BUILDER);
		methodVisitor.visitInsn(Opcodes.DUP);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, STRING_BUILDER, "<init>", "()V", false);
		int builder_index = astore();
		
		for (int i = 0;i<values.length;i++) {
			aload(builder_index);
			aload(value_indexes[i]);
			methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
					"(Ljava/lang/Object;)L"+STRING_BUILDER+";", false);
			pop();
		}
		
		aload(builder_index);
		call(StringBuilder.class, "toString", "()Ljava/lang/String;", false);
		methodVisitor.visitJumpInsn(Opcodes.GOTO, end);
		
		methodVisitor.visitLabel(mathOperation);
		
		ArrayList<Character> final_operations = new ArrayList<Character>();

		aloadDouble(value_indexes[0]);
		for (int i = 0;i<operations.length;i++) {
			aloadDouble(value_indexes[i+1]);
			if (operations[i]=='*') {
				methodVisitor.visitInsn(Opcodes.DMUL);
			} else if (operations[i]=='/') {
				methodVisitor.visitInsn(Opcodes.DDIV);
			} else if (operations[i]=='%') {
				methodVisitor.visitInsn(Opcodes.DREM);
			} else {
				final_operations.add(operations[i]);
			}
		}
		
		for (int i = final_operations.size()-1;i>=0;i--) {
			if (final_operations.get(i)=='+') {
				final_operations.remove(i);
				methodVisitor.visitInsn(Opcodes.DADD);
			} else if (final_operations.get(i)=='-') {
				final_operations.remove(i);
				methodVisitor.visitInsn(Opcodes.DSUB);
			}
		}

		methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"java/lang/Double",
				"valueOf",
				"(D)Ljava/lang/Double;",
				false
		);
		
		methodVisitor.visitLabel(end);
	}
}
