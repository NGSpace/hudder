package io.github.ngspace.hudder.v2runtime;

import java.util.Arrays;
import java.util.HashMap;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.v2runtime.runtime_elements.AV2RuntimeElement;
import io.github.ngspace.hudder.v2runtime.runtime_elements.StringV2RuntimeElement;

public class V2Runtime {
	public final AV2Compiler compiler;
	protected V2Runtime scope;
	/**
	 * Should stay mostly unused for now.
	 */
	public static final Object NULL = new Object() {
		@Override public boolean equals(Object obj) {return obj == this || obj == null;}
		@Override public int hashCode() {return super.hashCode();}
		@Override public String toString() {return "null";}
	};
	public V2Runtime(AV2Compiler compiler, V2Runtime scope) {this.compiler = compiler;this.scope = scope;}
	
	protected AV2RuntimeElement[] elements = new AV2RuntimeElement[0];
	public CompileState compileState;
	
	public CompileState execute() throws CompileException {
		compileState = new CompileState(CompileState.TOPLEFT);
		StringBuilder builder = new StringBuilder();
		for (int i = 0;i<elements.length;i++) {
			AV2RuntimeElement element = elements[i];
			if (!element.execute(compileState, builder)||compileState.hasReturned) {
//				System.out.println("r"+(compiler.globalRuntime==this));
				compileState.hasBroken = true;
				break;
			}
//			System.out.println(element.getClass().getSimpleName());
//			if (element instanceof StringV2RuntimeElement e) System.out.println(e.string);
		}
//		System.out.println(builder.toString());
		compileState.addString(builder.toString(), false);
		return compileState;
	}
	
	public void addRuntimeElement(AV2RuntimeElement element) {elements = addToArray(elements, element);}
	public AV2RuntimeElement[] getElements() {return elements;}
	
	public static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	
	HashMap<String, Object> scopedVariables = new HashMap<String, Object>();
	public void putScoped(String name, Object value) {scopedVariables.put(name, value);}
	public Object getScoped(String name) {
		Object object = scopedVariables.get(name);
		if (object==null&&scope!=null) return scope.getScoped(name);
		return object;
	}

	public Object getVariable(String name) {
		Object object = getScoped(name);
		if (object==null) return compiler.getDynamicVariable(name);
		return object;
	}
}