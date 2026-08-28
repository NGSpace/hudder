package dev.ngspace.hudder.api.compilers.abstractions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import dev.ngspace.hudder.api.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.CompileState;
import dev.ngspace.hudder.api.compilers.HudInformation;
import dev.ngspace.hudder.api.compilers.TextPos;
import dev.ngspace.hudder.api.compilers.interfaces.PreparedCompiler;
import dev.ngspace.hudder.api.compilers.interfaces.HudEvaluator;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.functions.IV2Function;
import dev.ngspace.hudder.v2runtime.functions.V2FunctionHandler;
import dev.ngspace.hudder.v2runtime.methods.MethodHandler;
import dev.ngspace.hudder.v2runtime.runtime_elements.AV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.values.AV2Value;
import dev.ngspace.hudder.v2runtime.values.DefaultV2VariableParser;
import dev.ngspace.hudder.v2runtime.values.IV2VariableParser;

public abstract class AV2Compiler extends AHudCompiler<V2Runtime> implements PositionedBinder,
		HudEvaluator<V2Runtime>, PreparedCompiler, VariablesManager {
	
	public MethodHandler methodHandler = new MethodHandler();
	public V2FunctionHandler functionHandler = new V2FunctionHandler();
	protected IV2VariableParser variableParser = new DefaultV2VariableParser();
	public boolean SYSTEM_VARIABLES_ENABLED = true;
	
	public Map<String, Object> tempVariables = new HashMap<String, Object>();
	public Map<String, Object> variables = new HashMap<String, Object>();

	protected AV2Compiler(HudderConfig config) {
		super(config, new AtomicReference<>(), new HashMap<>());
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
	public V2Runtime processFile(String filepath) throws CompileException, IOException {
		String text = HudFileUtils.readFile(filepath);
		return evalHud(text, filepath);
	}
	
	@Override
	public V2Runtime evalHud(String text, String filepath) throws CompileException {
		return buildRuntimeSafe(text, new TextPos(-1, -1), filepath, null);
	}
	

	@Override public final HudInformation execute(V2Runtime runtime, String filename) throws ExecutionException {
		return runtime.execute().toResult();
	}
	
	
	
	public abstract V2Runtime buildRuntime(String text, TextPos charPosition, String filename, V2Runtime scope)
			throws CompileException, ExecutionException;
	
	
	
	@Override public void bindConsumer(BindablePositionedConsumer cons, String... names) {
		methodHandler.bindConsumer((ci,m,_,_,_,pos,s)->cons.invoke(m, this, pos, ci, s), names);
	}
	@Override public void bindFunction(BindablePositionedFunction cons, String... names) {
		functionHandler.bindFunction((c,_,s,l,co)->cons.invoke(c.compileState, this, new TextPos(l, co),
				c.compiler.config, s), names);
	}
	


	public void defineFunctionOrMethod(String commands, String[] args, String name, TextPos pos, String filename)
			throws CompileException {
		V2Runtime runtime = buildRuntimeSafe(commands, pos, filename, null);
		
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

	private V2Runtime buildRuntimeSafe(String commands, TextPos pos, String filename,
			V2Runtime scope) throws CompileException {
		try {
			return buildRuntime(commands, pos, filename, scope);
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
	
	public record CodeBlock(String code, String text, int starting_index, int ending_index) {}
	
	public record Instruction(byte instruction, String paremeter, int ending_index) {}
	
	// V2 is the only one using those...
	protected TextPos getPosition(int ind, String string) {
		return getPosition(new TextPos(0, 0), ind, string);
	}
	protected TextPos getPosition(TextPos charPosition, int ind, String text) {
		int line = charPosition.line();
		int charpos = charPosition.column();
		if (line==-1||charpos==-1) {
			line = 0;
			charpos = 0;
		}
		
		for (int i = 0;i<ind;i++) {
			if (text.charAt(i)=='\n') {
				line++;
				charpos = 0;
				continue;
			}
			charpos++;
		}
		return new TextPos(line, charpos);
	}
	
	@Override
	public HudInformation evalAndExecuteHud(String text, String debugname) throws CompileException, ExecutionException {
		return execute(evalHud(text, debugname), debugname);
	}
	
	public Object getDynamicVariable(String key) {
		Object obj = getRaw(key);
		if (obj!=null) return obj;
		return key;
	}
	
	public Object getRaw(String key) {
		return variables.get(key);
	}
	
	@Override public Object getVariable(String key) {
		Object obj = DataVariableRegistry.getAny(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	
	@Override public void putVariable(String key, Object value) {variables.put(key, value);}
	
	@Override
	public void reset() throws IOException {
		SYSTEM_VARIABLES_ENABLED = true;
		variables.clear();
		tempVariables.clear();
		super.reset();
	}
	
	@Override
    public void prepareCompiler() {
		tempVariables.clear();
    }
}
