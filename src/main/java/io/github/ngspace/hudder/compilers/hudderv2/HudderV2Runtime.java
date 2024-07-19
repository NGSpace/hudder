package io.github.ngspace.hudder.compilers.hudderv2;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.hudderv2.runtime_elements.AV2RuntimeElement;
import io.github.ngspace.hudder.meta.Meta;

public class HudderV2Runtime {
	public final HudderV2Compiler compiler;
	public HudderV2Runtime(HudderV2Compiler compiler) {
		this.compiler = compiler;
	}
	
	
	
	AV2RuntimeElement[] elements = new AV2RuntimeElement[0];
	public Meta execute() throws CompileException {
		Meta meta = new Meta(Meta.TOPLEFT);
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