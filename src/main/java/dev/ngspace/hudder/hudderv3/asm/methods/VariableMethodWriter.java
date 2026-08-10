package dev.ngspace.hudder.hudderv3.asm.methods;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class VariableMethodWriter extends BaseMethodWriter {

	protected Map<String, Integer> variables = new HashMap<String, Integer>();
	
	protected VariableMethodWriter(MethodVisitor methodVisitor) {
		super(methodVisitor);
	}

	public void defineVariable(String name) {
		nullConstant();
		int index = astore();
		variables.put(name, index);
	}
	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}

	public Integer defineScopedVariable(String name) {
		Integer previousIndex = variables.get(name);
		defineVariable(name);
		return previousIndex;
	}

	public void restoreScopedVariable(String name, Integer previousIndex) {
		if (previousIndex == null) {
			variables.remove(name);
		} else {
			variables.put(name, previousIndex);
		}
	}
	
	public void storeVariable(String name) {
		astore(variables.get(name));
	}
	
	public void getVariable(String name) {
		aload(variables.get(name));
	}
	
	// Fields

	public void getField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}

	public void getStaticField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}

	public void putField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}

	public void putStaticField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}
}
