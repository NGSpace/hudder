package dev.ngspace.hudder.v2runtime.runtime_elements;

import java.io.IOException;
import java.util.Arrays;

import dev.ngspace.hudder.api.compilers.compilers.AV2Compiler;
import dev.ngspace.hudder.api.compilers.utils.CompileState;
import dev.ngspace.hudder.api.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class ConditionV2RuntimeElement extends AV2RuntimeElement {

	AV2Value[] results = {};
	AV2Value[] conditions = {};
	AV2Compiler compiler;
	boolean hasElse;
	private String filename;
	private HudderConfig config;
	private int line;
	private int charpos;
	
	public ConditionV2RuntimeElement(HudderConfig config, String[] condArgs, AV2Compiler compiler, V2Runtime runtime, int line,
			int charpos, String filename) throws ExecutionException {
		this.compiler = compiler;
		this.config = config;
		this.filename = filename;
		this.line = line;
		this.charpos = charpos;
		
		hasElse = condArgs.length%2==1;
		for (int i = 0;i<condArgs.length;i++) {
			String str = condArgs[i];
			if (hasElse&&i==condArgs.length-1) {
				results = addToArray(results, compiler.getV2Value(runtime, str, line, charpos));
				break;
			}
			if (i%2==0) conditions = addToArray(conditions, compiler.getV2Value(runtime, str, line, charpos));
			else results = addToArray(results, compiler.getV2Value(runtime, str, line, charpos));
		}
	}
	
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws ExecutionException {
		try {
			HudInformation res = null;
			for (int i = 0;i<conditions.length;i++) {
				if (conditions[i].asBoolean()) {
					res = config.compilationManager.compileAndExecuteSecondaryHud(compiler, results[i].asString(),filename);
					break;
				}
			}
			if (res==null&&hasElse)
				res = config.compilationManager.compileAndExecuteSecondaryHud(compiler,
						results[results.length-1].asString(),filename);
			if (res!=null) {
				builder.append(res.TopLeftText());
				for (var v : res.elements()) meta.elements.add(v);
			}
			return true;
		} catch (IOException | CompileException e) {
			throw new ExecutionException(e, line, charpos);
		} 
	}

	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}
