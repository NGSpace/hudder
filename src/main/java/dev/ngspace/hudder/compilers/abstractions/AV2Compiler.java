package dev.ngspace.hudder.compilers.abstractions;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.compilers.utils.CharPosition;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.functions.IV2Function;
import dev.ngspace.hudder.v2runtime.functions.V2FunctionHandler;
import dev.ngspace.hudder.v2runtime.methods.MethodHandler;
import dev.ngspace.hudder.v2runtime.runtime_elements.AV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.values.AV2Value;
import dev.ngspace.hudder.v2runtime.values.DefaultV2VariableParser;
import dev.ngspace.hudder.v2runtime.values.IV2VariableParser;

public abstract class AV2Compiler extends AVarTextCompiler implements Binder {
	
	public Map<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();
	public static Map<String, Object> tempVariables = new HashMap<String, Object>();
	public MethodHandler methodHandler = new MethodHandler();
	public V2FunctionHandler functionHandler = new V2FunctionHandler();
	protected IV2VariableParser variableParser = new DefaultV2VariableParser();
	public boolean SYSTEM_VARIABLES_ENABLED = true;
	
	protected AV2Compiler() {
		HudCompilationManager.addPreCompilerListener(c -> tempVariables.clear());
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(this);
	}
	
	
	/**
	 * Returns the temporary variables.
	 * 
	 * <br><br>
	 * 
	 * Temporary variables get deleted every hud compiliation.
	 * @param key - the name of the variable
	 * @return the value of the variable or null if it is not set
	 */
	public Object getTempVariable(String key) {return tempVariables.get(key);}
	/**
	 * Sets the value of a temporary variable.
	 * 
	 * <br><br>
	 * 
	 * Temporary variables get deleted every hud compiliation.
	 * @param key - the name of the variable
	 * @param value - the new value of the variable
	 */
	public void putTemp(String key, Object value) {tempVariables.put(key, value);}
	
	
	
	/**
	 * Tokenize the provided string to a AV2Value instance.
	 * @param runtime - The V2Runtime.
	 * @param string - The string to tokenize.
	 * @param line - The line at which the string is tokenized
	 * @param col - The col at which the string is tokenized
	 * @returns The tokenized AV2Value
	 * @throws CompileException
	 */
	public AV2Value getV2Value(V2Runtime runtime, String string, int line, int col) throws CompileException {
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
	
	

	@Override public final HudInformation compile(HudderConfig info, String text, String filename)
			throws CompileException {
		V2Runtime runtime = runtimes.get(text);
		if (runtime==null)
			runtimes.put(text, (runtime=buildRuntime(info, text, new CharPosition(-1, -1), filename, null)));
		return runtime.execute().toResult();
	}
	
	
	
	public abstract V2Runtime buildRuntime(HudderConfig info, String text, CharPosition charPosition, String filename,
			V2Runtime scope) throws CompileException;
	
	
	
	@Override public void bindConsumer(BindableConsumer cons, String... names) {
		methodHandler.bindConsumer((c,m,a,t,l,ch,s)->cons.invoke(m, this, s), names);
	}
	@Override public void bindFunction(BindableFunction cons, String... names) {
		functionHandler.bindFunction((c,a,s,l,ch)->cons.invoke(c.compileState, this, s), names);
	}
	


	public void defineFunctionOrMethod(String commands, String[] args, String name, CharPosition pos, String filename)
			throws CompileException {
		V2Runtime runtime = buildRuntime(getConfig(), commands, pos, filename, null);
		
		boolean isMethod = !hasReturnValue(runtime);
		
		if (isMethod) {
			MethodHandler.methods.put(name, (info,state,comp,type,line,charpos,vals) -> {
				if (vals.length<args.length) throw new CompileException("Not enough arguments", pos.line(), pos.charpos());
				for (int i = 0;i<vals.length;i++) {
					Object v = vals[i].get();
					runtime.putScoped("arg"+(i+1), v);
					runtime.putScoped(args[i].trim(), v);
				}
				try {
					state.combineWithResult(runtime.execute().toResult(), false);
				} catch (CompileException e) {
					throw new CompileException("Method "+type+" threw an error: \n"+e.getFailureMessage(),charpos);
				}
			});
		} else {//Is function
			//Make sure the main path actually returns a value
			boolean temp = true;
			for (AV2RuntimeElement element : runtime.getElements()) {
				if (element.returnsAValue()) temp = false;
			}
			if (temp) throw new CompileException("Main path in function \""+name
					+"\" does not return a value!",pos.line(),pos.charpos());
			functionHandler.bindFunction((IV2Function) (funcruntime,funcname,vals,line,charpos) -> {
				if (vals.length<args.length) throw new CompileException("Not enough arguments", pos.line(), pos.charpos());
				for (int i = 0;i<vals.length;i++) {
					Object v = vals[i].get();
					runtime.putScoped("arg"+(i+1), v);
					runtime.putScoped(args[i].trim(), v);
				}
				try {
					CompileState exec = runtime.execute();
					runtime.compileState.combineWithResult(exec.toResult(), false);
					return exec.returnValue;
				} catch (CompileException e) {
					throw new CompileException("Function "+name+" threw an error: \n"+e.getFailureMessage(),line,charpos);
				}
			}, name);
			
		}
	}

	public boolean hasReturnValue(V2Runtime runtime) {
		for (AV2RuntimeElement element : runtime.getElements()) {
			if (element.returnsAValue()) return true;
			if (element.getNestedRuntime()!=null&&hasReturnValue(element.getNestedRuntime())) return true;
		}
		return false;
	}
}
