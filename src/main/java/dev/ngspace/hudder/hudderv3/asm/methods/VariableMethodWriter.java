package dev.ngspace.hudder.hudderv3.asm.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class VariableMethodWriter extends BaseMethodWriter {
	
	private final Map<String, LocalVariable> variables;
	// I know it's a shit name, couldn't do any better.
	private final List<LocalVariable> unregistered_debug_variables = new ArrayList<>();
	
	protected VariableMethodWriter(Map<String, LocalVariable> variables, MethodVisitor methodVisitor) {
		super(methodVisitor);
		this.variables = variables;
	}
	
	public void defineVariable(String name, boolean readonly) {
		nullConstant();
		defineVariableWithValue(name, readonly);
	}
	
	public void defineVariableWithValue(String name, boolean readonly) {
		Label variable_start = new Label();
		putLabel(variable_start);
		int index = astore();
		LocalVariable var = new LocalVariable(name, index, variable_start, readonly);
		variables.put(name, var);
		unregistered_debug_variables.add(var);
	}
	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}
	
	public LocalVariable defineScopedVariable(String name, boolean readonly) {
		LocalVariable previousIndex = variables.get(name);
		defineVariable(name, readonly);
		return previousIndex;
	}
	
	public LocalVariable defineScopedVariableWithValue(String name, boolean readonly) {
		LocalVariable previousIndex = variables.get(name);
		defineVariableWithValue(name, readonly);
		return previousIndex;
	}

	public void restoreScopedVariable(String name, LocalVariable previousIndex) {
		if (previousIndex == null) {
			variables.remove(name);
		} else {
			variables.put(name, previousIndex);
		}
	}
	
	public void storeVariable(String name) {
		LocalVariable var = variables.get(name);
		if (var.readOnly())
			throw new UnsupportedOperationException("Variable \"" + name + "\" is read only!");
		astore(var.index);
	}
	
	public void forceStoreVariable(String name) {
		astore(variables.get(name).index);
	}
	
	public void getVariable(String name) {
		aload(variables.get(name).index);
	}
	
	// Fill the variable table to allow for easier debugging.
	@Override
	protected void endNoInsn() {
		Label variable_table = new Label(); // No, I can't use final_label here.
		putLabel(variable_table);
		for (var entry : unregistered_debug_variables)
			methodVisitor.visitLocalVariable(entry.name(), Type.getDescriptor(Object.class), null, entry.start,
					variable_table, entry.index);
		super.endNoInsn();
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
	
	
	public static record LocalVariable(String name, int index, Label start, boolean readOnly) {}
}
