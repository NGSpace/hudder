package io.github.ngspace.hudder.v2runtime.runtime_elements;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

//What a name...
public class BasicConditionV2RuntimeElement extends AV2RuntimeElement {

	AV2Value[] results = {};
	AV2Value[] conditions = {};
	AVarTextCompiler compiler;
	ConfigInfo info;
	boolean hasFinalElse;
	public BasicConditionV2RuntimeElement(String[] condArgs, AV2Compiler compiler, ConfigInfo info, V2Runtime runtime,
			int line, int charpos) {
		this.compiler = compiler;
		this.info = info;
		
		hasFinalElse = condArgs.length%2==1;
		try {
		for (int i = 0;i<condArgs.length;i++) {
			String str = condArgs[i];
			if (hasFinalElse&&i==condArgs.length-1) {
				results = addToArray(results, compiler.getV2Value(runtime, str,line,charpos));
				break;
			}
			if (i%2==0) conditions = addToArray(conditions, compiler.getV2Value(runtime, str,line,charpos));
			else results = addToArray(results, compiler.getV2Value(runtime, str,line,charpos));
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		CompileResult res = null;
		for (int i = 0;i<conditions.length;i++) {
			if (conditions[i].asBoolean()) {
				res = compiler.compile(info,results[i].asString());
			}
		}
		if (res==null&&hasFinalElse) res = compiler.compile(info,results[results.length-1].asString());
		if (res==null) res = CompileResult.of("");
		builder.append(res.TopLeftText);
		for (var v : res.elements) meta.elements.add(v);
	}

	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}
