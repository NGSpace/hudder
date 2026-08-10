package dev.ngspace.hudder.hudderv3.asm.methods;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;

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
}
