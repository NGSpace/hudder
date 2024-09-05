package io.github.ngspace.hudder.compilers.v2runtime;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.v2runtime.runtime_elements.AV2RuntimeElement;
import io.github.ngspace.hudder.methods.CompileState;
import io.github.ngspace.hudder.methods.MethodHandler;

public class V2Runtime {
	
	public final MethodHandler methodHandler = new MethodHandler();
	
	AV2RuntimeElement[] elements = new AV2RuntimeElement[0];
	public CompileState execute() throws CompileException {
		CompileState meta = new CompileState(CompileState.TOPLEFT);
		StringBuilder builder = new StringBuilder();
		for (AV2RuntimeElement element : elements) element.execute(meta, builder);
		return meta;
	}
	
	public void addRuntimeElement(AV2RuntimeElement element) {elements = addToArray(elements, element);}
	
	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}