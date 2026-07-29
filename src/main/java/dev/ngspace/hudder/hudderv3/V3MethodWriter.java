package dev.ngspace.hudder.hudderv3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;

public class V3MethodWriter {
	
	public static final String STRING_BUILDER = Type.getInternalName(StringBuilder.class);
	public static final String VAR_REGISTRY = Type.getInternalName(DataVariableRegistry.class);
	
	public V3ClassWriter classWriter;
	public MethodVisitor methodVisitor;
	public int variableindex = 0;
	public String methodName;
	public Label finalLabel = new Label();
	public Map<String, Integer> variables = new HashMap<String, Integer>();

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
	
	

	public int defineVariable(String name) {
		methodVisitor.visitInsn(Opcodes.ACONST_NULL);
		int index = astore();
		variables.put(name, index);
		return index;
	}
	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}
	
	public void storeVariable(String name) {
		astore(variables.get(name));
	}
	
	public void getVariable(String name) {
		aload(variables.get(name));
	}



	public void initStringBuilder() {
		newAndDup(StringBuilder.class);
		callSpecial(StringBuilder.class, "<init>", "()V", false);
	}

	public void getField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classWriter.classname,
				name, Type.getDescriptor(type));
	}

	public void getField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}

	public void putField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classWriter.classname,
				name, Type.getDescriptor(type));
	}

	public void putField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}



	public void newAndDup(Class<?> type) {
		methodVisitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(type));
		dup();
	}
	
	public void dup() {
		methodVisitor.visitInsn(Opcodes.DUP);
	}



	public void callSpecial(Class<?> type, String name, String sign, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(type), name, sign,
				isInterface);
	}
	public void callInit(Class<?> type, String sign) {
		callSpecial(type, "<init>", sign, false);
	}



	public void booleanValue() {
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Boolean",
				"booleanValue",
				"()Z",
				false
		);
	}



	public void intValue() {
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Number",
				"intValue",
				"()I",
				false
		);
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
	public void aaload() {
		methodVisitor.visitInsn(Opcodes.AALOAD);
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
	public void aloadFloat(int index) {
		methodVisitor.visitVarInsn(Opcodes.ALOAD, index);

		methodVisitor.visitTypeInsn(
		    Opcodes.CHECKCAST,
		    Type.getInternalName(Number.class)
		);
		
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Number",
				"floatValue",
				"()F",
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
	public void loadConstant(float constant) {
		if (constant%1==0) {
			loadConstant((long)constant);
		} else {
			methodVisitor.visitLdcInsn(constant);
			//Convert to Object
			methodVisitor.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					"java/lang/Float",
					"valueOf",
					"(F)Ljava/lang/Float;",
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
	public void loadConstantUnsafe(Object constant) {
		methodVisitor.visitLdcInsn(constant);
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
	
	public void astore(int index) {
		methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
	}

	public void aastore() {
		methodVisitor.visitInsn(Opcodes.AASTORE);
	}
	
	public int lstore() {
		methodVisitor.visitVarInsn(Opcodes.LSTORE, ++variableindex);
		return variableindex++;
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



	public void callInterface(Class<?> clazz, String name, String descriptor) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(clazz), name, descriptor,
				true);
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

	public void jumpto(Label end) {
		methodVisitor.visitJumpInsn(Opcodes.GOTO, end);
	}

	public void end() {
		end(Opcodes.RETURN);
	}

	public void end(int Opcode) {
		putLabel(finalLabel);
		methodVisitor.visitInsn(Opcode);
		
		endNoInsn();
	}

	protected void endNoInsn() {
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}

	public void complexMath(AV3Compiler comp, String[] values, char[] operations) throws ExecutionException {
		int[] value_indexes = new int[values.length];
		// Is String
		methodVisitor.visitLdcInsn(false);
		methodVisitor.visitVarInsn(Opcodes.ISTORE, ++variableindex);
		int is_string_index = variableindex;
		
		for (int i = 0;i<values.length;i++) {
			Label isNumber = new Label();
			comp.parseVariable(values[i]).visit(this);
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
		initStringBuilder();
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



	public void throwRuntimeException(String exception) {
		newAndDup(RuntimeException.class);
		loadConstant(exception);
		callSpecial(RuntimeException.class, "<init>", "(Ljava/lang/String;)V", false);
		methodVisitor.visitInsn(Opcodes.ATHROW);
	}



	public void newArray(Class<?> type) {
		methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,
				Type.getInternalName(type));
	}
}
