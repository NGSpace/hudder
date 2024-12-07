package io.github.ngspace.hudder.compilers.abstractions;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.compilers.utils.UnifiedCompiler;
import io.github.ngspace.hudder.compilers.utils.UnifiedCompiler.BindableConsumer;
import io.github.ngspace.hudder.compilers.utils.UnifiedCompiler.BindableFunction;
import io.github.ngspace.hudder.compilers.utils.UnifiedCompiler.ConsumerBinder;
import io.github.ngspace.hudder.compilers.utils.UnifiedCompiler.FunctionBinder;
import io.github.ngspace.hudder.hudder.HudCompilationManager;
import io.github.ngspace.hudder.hudder.config.HudderConfig;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.functions.V2FunctionHandler;
import io.github.ngspace.hudder.v2runtime.methods.MethodHandler;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;
import io.github.ngspace.hudder.v2runtime.values.IV2VariableParser;
import io.github.ngspace.hudder.v2runtime.values.DefaultV2VariableParser;

public abstract class AV2Compiler extends AVarTextCompiler implements ConsumerBinder, FunctionBinder {
	
	public Map<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();
	public Map<String, Object> tempVariables = new HashMap<String, Object>();
	public MethodHandler methodHandler = new MethodHandler();
	public V2FunctionHandler functionHandler = new V2FunctionHandler();
	protected IV2VariableParser variableParser = new DefaultV2VariableParser();
	
	protected AV2Compiler() {
		HudCompilationManager.addPreCompilerListener(c -> {if (this==c) tempVariables.clear();});
		UnifiedCompiler comp = UnifiedCompiler.instance;
		comp.applyConsumers(this);
		comp.applyFunctions(this);
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
	
	

	@Override public final HudInformation compile(HudderConfig info, String text, String filename) throws CompileException {
		V2Runtime runtime = runtimes.get(text);
		if (runtime==null) runtimes.put(text, (runtime=buildRuntime(info,text, new CharPosition(-1, -1), filename)));
		return runtime.execute().toResult();
	}
	
	
	
	public abstract V2Runtime buildRuntime(HudderConfig info, String text, CharPosition charPosition, String filename) throws CompileException;
	
	
	
	@Override public void bindConsumer(BindableConsumer cons, String... names) {
		methodHandler.bindConsumer((c,m,a,t,l,ch,s)->cons.invoke(m, this, l, ch, s), names);
	}
	@Override public void bindFunction(BindableFunction cons, String... names) {
		functionHandler.bindFunction((c,a,s,l,ch)->cons.invoke(c.compileState, this, s), names);
	}
}
