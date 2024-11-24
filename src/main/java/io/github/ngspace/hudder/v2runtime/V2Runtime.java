package io.github.ngspace.hudder.v2runtime;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.v2runtime.runtime_elements.AV2RuntimeElement;

public class V2Runtime {
	public final AV2Compiler compiler;
	public V2Runtime(AV2Compiler compiler) {this.compiler = compiler;}
	
	AV2RuntimeElement[] elements = new AV2RuntimeElement[0];
	public CompileState compileState;
	public CompileState execute() throws CompileException {
		compileState = new CompileState(CompileState.TOPLEFT);
		StringBuilder builder = new StringBuilder();
		for (AV2RuntimeElement element : elements) {
			if (!element.execute(compileState, builder)) {
				compileState.hasBroken = true;
				break;
			}
		}
		return compileState;
	}
	
	public void addRuntimeElement(AV2RuntimeElement element) {elements = addToArray(elements, element);}
	
	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}