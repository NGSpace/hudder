package io.github.ngspace.hudder.v2runtime.runtime_elements;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.values.V2Value;

//What a name...
public class BasicConditionV2RuntimeElement extends AV2RuntimeElement {

	V2Value[] results = {};
	V2Value[] conditions = {};
	AVarTextCompiler compiler;
	ConfigInfo info;
	boolean hasFinalElse;
	public BasicConditionV2RuntimeElement(String[] condArgs, AV2Compiler compiler, ConfigInfo info) {
		this.compiler = compiler;
		this.info = info;
		
		hasFinalElse = condArgs.length%2==1;
		try {
		for (int i = 0;i<condArgs.length;i++) {
			String str = condArgs[i];
			System.out.println(str);
			if (hasFinalElse&&i==condArgs.length-1) {
				results = addToArray(results, compiler.getV2Value(str));
				break;
			}
			if (i%2==0) conditions = addToArray(conditions, compiler.getV2Value(str));
			else results = addToArray(results, compiler.getV2Value(str));
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		CompileResult res = null;
		for (int i = 0;i<conditions.length;i++) {
			if (conditions[i].asBooleanSafe()) {
				res = compiler.compile(info,results[i].asString());
			}
		}
		if (res==null&&hasFinalElse) res = compiler.compile(info,results[results.length-1].asString());
		if (res==null) res = CompileResult.of("");
		builder.append(res.TopLeftText);
	}

	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}
