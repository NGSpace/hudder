package dev.ngspace.hudder.api.compilers.abstractions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.HudInformation;
import dev.ngspace.hudder.api.compilers.TextPos;
import dev.ngspace.hudder.api.compilers.interfaces.PreparedCompiler;
import dev.ngspace.hudder.api.compilers.interfaces.StringEvaluator;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.GeneratedCompiler;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.V3APIFunctions;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.ImplV3ExpressionParser;
import dev.ngspace.hudder.hudderv3.instructions.V3ExpressionParser;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;
import dev.ngspace.hudder.utils.HudFileUtils;

public abstract class AV3Compiler extends AHudCompiler<GeneratedCompiler> implements PositionedBinder,
		StringEvaluator<GeneratedCompiler>, PreparedCompiler {
	
	public static final String VERIFIER_ERROR_NOTE = """
			
			# This is a bytecode-gen error!
			# Please report this to the developer of Hudder!
			""";
	
	public V3ExpressionParser expressionParser = new ImplV3ExpressionParser();
	public boolean system_variables = true;

	public Map<String, BindablePositionedFunction> api_functions = new HashMap<String, BindablePositionedFunction>();
	public Map<String, BindablePositionedConsumer> api_consumers = new HashMap<String, BindablePositionedConsumer>();

	protected AV3Compiler(HudderConfig config) {
		super(config, new AtomicReference<>(), new HashMap<>());
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(this);
		V3APIFunctions.bindAllAPIFunctions(this);
	}

	@Override
	public GeneratedCompiler processFile(String filepath) throws CompileException, IOException {
		String text = HudFileUtils.readFile(filepath);
		return evalHud(text, filepath);
	}
	
	@Override
	public GeneratedCompiler evalHud(String text, String filepath) throws CompileException {
		try {
			HudderV3Helper helper = new HudderV3Helper(config, this);
			
			V3ClassWriter classWriter = new V3ClassWriter("dev/ngspace/hudder/hudderv3/GeneratedClass",
					filepath, helper);
			classWriter.createInit();
			
			V3ExecuteMethodWriter executeMethod = classWriter.createExecuteMethod("execute", null);
			
			Label end = new Label();
			
			executeMethod.aload(0);
			executeMethod.getField("uimanager", ArrayElementManager.class);
			executeMethod.callInterface(List.class, "clear", (Class<?>) null);
			
			compile(text, filepath, new TextPos(0, 0)).writeInstructions(executeMethod, classWriter, end);
			
			executeMethod.putLabel(end);
			executeMethod.end();
		
			Class<?> dynamicClass = classWriter.toClass();
			
			Object instance = dynamicClass.getDeclaredConstructor(AV3Compiler.class, HudderV3Helper.class)
					.newInstance(this, helper);
			return (GeneratedCompiler) instance;
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException re)
				throw re;
			e.printStackTrace();
			throw new CompileException(e.getTargetException().toString(),-1, -1, e.getTargetException());
		} catch (ClassFormatError e) {
			// The compilation manager does not handle JVM errors and will crash the game which is bad.
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (VerifyError e) {
			// The compilation manager does not handle Verifier errors and will crash the game which is bad.
			e.printStackTrace();
			String msg = VERIFIER_ERROR_NOTE + e.getMessage();
			int frame = msg.indexOf("Current Frame");
			int at = msg.indexOf("@");
			throw new RuntimeException(msg.substring(0, msg.indexOf('\n')+1) +
					'\n' + msg.substring(at==-1?0:at, frame==-1?msg.length():frame));
		} catch (CompileException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CompileException(e.toString(),-1, -1, e);
		} 
	}

	@Override
	public HudInformation execute(GeneratedCompiler compiler, String filename) throws ExecutionException {
		try {
			return compiler.execute(config, filename).hudInformation;
		} catch (VerifyError e) {
			// The compilation manager does not handle Verifier errors and will crash the game which is bad.
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public abstract TokenizedCodeBlock compile(String text, String filename, TextPos offset)
			throws CompileException;
	
	
	
	public ExpressionVisitor parseVariable(String string, TextPos pos) throws CompileException {
		return expressionParser.parseExpression(string, this, pos);
	}
	
	
	
	public V3ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public void setExpressionParser(V3ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}


	@Override
	public void reset() throws IOException {
		system_variables = true;
		for (var instance : instances.values())
			if (instance != null)
				instance.shutdown();
		super.reset();
	}

	@Override
	public void bindConsumer(BindablePositionedConsumer cons, String... names) {
		for (String name : names) {
			api_consumers.put("api_consumer_" + name, cons);
		}
	}
	
	@Override
	public void bindFunction(BindablePositionedFunction cons, String... names) {
		for (String name : names) {
			api_functions.put("api_function_" + name, cons);
		}
	}
	@Override
	public HudInformation evalAndExecuteHud(String text, String debugname) throws CompileException, ExecutionException {
		return execute(evalHud(text, debugname), debugname);
	}
	
	@Override
    public void prepareCompiler() {
		if (mainInstance.get()!=null)
			mainInstance.get().prepareCompiler();
    }
}
