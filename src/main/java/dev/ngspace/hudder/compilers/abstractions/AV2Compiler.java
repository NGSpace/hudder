package dev.ngspace.hudder.compilers.abstractions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.functions.IV2Function;
import dev.ngspace.hudder.v2runtime.functions.V2FunctionHandler;
import dev.ngspace.hudder.v2runtime.methods.MethodHandler;
import dev.ngspace.hudder.v2runtime.runtime_elements.AV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.values.AV2Value;
import dev.ngspace.hudder.v2runtime.values.DefaultV2VariableParser;
import dev.ngspace.hudder.v2runtime.values.IV2VariableParser;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public abstract class AV2Compiler extends AVarTextCompiler implements Binder {
	
	public Map<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();
	public MethodHandler methodHandler = new MethodHandler();
	public V2FunctionHandler functionHandler = new V2FunctionHandler();
	protected IV2VariableParser variableParser = new DefaultV2VariableParser();
	public boolean SYSTEM_VARIABLES_ENABLED = true;
	
	protected AV2Compiler() {
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(this);
	}
	
	
	
	/**
	 * Tokenize the provided string to a AV2Value instance.
	 * @param runtime - The V2Runtime.
	 * @param string - The string to tokenize.
	 * @param line - The line at which the string is tokenized
	 * @param col - The col at which the string is tokenized
	 * @returns The tokenized AV2Value
	 * @throws CompileException
	 */
	public AV2Value getV2Value(V2Runtime runtime, String string, int line, int col) throws ExecutionException {
		return getVariableParser().parse(runtime, string, this, line, col);
	}

	/**
	 * @return The variable parser used by this compiler.
	 */
	public IV2VariableParser getVariableParser() {
		return variableParser;
	}
	
	/**
	 * Sets the variable parser used by this compiler.
	 */
	public void setVariableParser(IV2VariableParser parser) {
		variableParser = parser;
	}
	
	
	@Override
	public void compileFile(String text, String filepath) throws CompileException {
		if (!runtimes.containsKey(text))
			runtimes.put(text, buildRuntimeSafe(Hudder.config, text, new TextPos(-1, -1), filepath, null));
	}
	

	@Override public final HudInformation execute(HudderConfig info, String text, String filename)
			throws ExecutionException {
		return runtimes.get(text).execute().toResult();
	}
	
	
	
	public abstract V2Runtime buildRuntime(HudderConfig info, String text, TextPos charPosition, String filename,
			V2Runtime scope) throws CompileException, ExecutionException;
	
	
	
	@Override public void bindConsumer(BindableConsumer cons, String... names) {
		methodHandler.bindConsumer((_,m,_,_,_,_,s)->cons.invoke(m, this, s), names);
	}
	@Override public void bindFunction(BindableFunction cons, String... names) {
		functionHandler.bindFunction((c,_,s,_,_)->cons.invoke(c.compileState, this, s), names);
	}
	


	public void defineFunctionOrMethod(String commands, String[] args, String name, TextPos pos, String filename)
			throws CompileException {
		V2Runtime runtime = buildRuntimeSafe(Hudder.config, commands, pos, filename, null);
		
		boolean isMethod = !canReturnValue(runtime);
		
		if (isMethod) {
			MethodHandler.methods.put(name, (_,state,_,type,_,charpos,vals) -> {
				if (vals.length<args.length) throw new ExecutionException("Not enough arguments", pos);
				for (int i = 0;i<vals.length;i++) {
					Object v = vals[i].get();
					runtime.putScoped("arg"+(i+1), v);
					runtime.putScoped(args[i].trim(), v);
				}
				try {
					state.combineWithResult(runtime.execute().toResult(), false);
				} catch (ExecutionException e) {
					throw new ExecutionException("Method "+type+" threw an error: \n"+e.getFailureMessage(),charpos);
				}
			});
		} else {//Is function
			//Make sure the main path actually returns a value
			boolean temp = true;
			for (AV2RuntimeElement element : runtime.getElements()) {
				if (element.returnsAValue()) temp = false;
			}
			if (temp) throw new CompileException("Function \""+name
					+"\" does not always return a value!",pos);
			functionHandler.bindFunction((IV2Function) (_,_,vals,line,charpos) -> {
				if (vals.length<args.length) throw new ExecutionException("Not enough arguments", pos);
				for (int i = 0;i<vals.length;i++) {
					Object v = vals[i].get();
					runtime.putScoped("arg"+(i+1), v);
					runtime.putScoped(args[i].trim(), v);
				}
				try {
					CompileState exec = runtime.execute();
					runtime.compileState.combineWithResult(exec.toResult(), false);
					return exec.returnValue;
				} catch (ExecutionException e) {
					throw new ExecutionException("Function "+name+" threw an error: \n"+e.getFailureMessage(),line,charpos);
				}
			}, name);
			
		}
	}

	private V2Runtime buildRuntimeSafe(HudderConfig config, String commands, TextPos pos, String filename,
			V2Runtime scope) throws CompileException {
		try {
			return buildRuntime(config, commands, pos, filename, scope);
		} catch (ExecutionException e) {
			throw new CompileException(e);
		}
	}
	
	/**
	 * 
	 * @deprecated use canReturnValue
	 */
	@Deprecated(since = "10.1.0", forRemoval = false)
	public boolean hasReturnValue(V2Runtime runtime) {
		return canReturnValue(runtime);
	}

	public boolean canReturnValue(V2Runtime runtime) {
		for (AV2RuntimeElement element : runtime.getElements()) {
			if (element.returnsAValue()) return true;
			if (element.getNestedRuntimes()!=null)
				for (V2Runtime nestedRuntime : element.getNestedRuntimes())
					if (canReturnValue(nestedRuntime)) return true;
		}
		return false;
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		return false;
	}


	public HudInformation compileAndExecute(HudderConfig info, String text, String filename) throws ExecutionException {
		try {
			if (!runtimes.containsKey(text))
				compileFile(text, filename);
			return execute(info, text, filename);
		} catch (CompileException e) {
			throw new ExecutionException(e);
		}
	}
	
	public record CodeBlock(String code, String text, int starting_index, int ending_index) {}
	
	public record Instruction(byte instruction, String paremeter, int ending_index) {}
	
	@Override
	public void resetState() throws IOException {
		SYSTEM_VARIABLES_ENABLED = true;
		runtimes.clear();
		super.resetState();
	}
}
