package io.github.ngspace.hudder.compilers.hudderv2;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.hudderv2.runtime_elements.AV2RuntimeElement;
import io.github.ngspace.hudder.meta.CompileState;

public class V2Runtime {
	public final ATextCompiler compiler;
	public V2Runtime(ATextCompiler compiler) {
		this.compiler = compiler;
	}
	
	
	
	AV2RuntimeElement[] elements = new AV2RuntimeElement[0];
	public CompileState execute() throws CompileException {
		CompileState meta = new CompileState(CompileState.TOPLEFT);
		StringBuilder builder = new StringBuilder();
		for (AV2RuntimeElement element : elements) element.execute(meta, builder);
		return meta;
	}
	
	
	
	public void addRuntimeElement(AV2RuntimeElement element) {
		elements = addToArray(elements, element);
	}
	public static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}