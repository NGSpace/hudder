package dev.ngspace.hudder.hudderv3.instructions.variables;

import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3HudInformation;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.utils.ImplObjectWrapper;
import dev.ngspace.hudder.utils.ObjectWrapper;

public class FunctionCallVariableVisitor extends ExpressionVisitor {

	private final String[] args;
	private final String funcName;
	private final boolean apiCall;
	
	public FunctionCallVariableVisitor(String funcName, AV3Compiler comp, String[] args, TextPos pos, String expression) {
		super(comp, pos, expression);
		this.args = args;
		this.funcName = funcName;
		this.apiCall = comp.api_functions.containsKey("api_function_" + funcName.trim());
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		
		if (apiCall) {
			methodWriter.classWriter.loadApiFunction(funcName.trim());
			methodWriter.aload(0);
			methodWriter.getField("api_function_"+funcName.trim(), BindablePositionedFunction.class);
			methodWriter.aload(0);
			methodWriter.getField("uimanager", ArrayElementManager.class);
			methodWriter.aload(0);
			methodWriter.getField("v3compiler", AV3Compiler.class);
			methodWriter.newAndDup(TextPos.class);
			methodWriter.loadConstantUnsafe(pos.line());
			methodWriter.loadConstantUnsafe(pos.column());
			methodWriter.callInit(TextPos.class, Integer.TYPE, Integer.TYPE);
			methodWriter.aload(1);
		}
		
		methodWriter.loadConstantUnsafe(args.length);
		methodWriter.newArray(apiCall ? ObjectWrapper.class: Object.class);
		int array_index = methodWriter.astore();
		for (int i = 0;i<args.length;i++) {
			comp.parseVariable(args[i], pos).visit(methodWriter);
			int value_index = methodWriter.astore();
			methodWriter.aload(array_index);
			methodWriter.loadConstantUnsafe(i);
			if (apiCall) {
				methodWriter.newAndDup(ImplObjectWrapper.class);
			}
			methodWriter.aload(value_index);
			if (apiCall) {
				methodWriter.loadConstantUnsafe(pos.line());
				methodWriter.loadConstantUnsafe(pos.column());
				methodWriter.callInit(ImplObjectWrapper.class, Object.class, Integer.TYPE, Integer.TYPE);
			}
			methodWriter.aastore();
		}
		
		if (apiCall) {
			visitApiCall(methodWriter, array_index);
		} else {
			visitUserFunction(methodWriter, array_index);
		}
	}
	
	protected void visitApiCall(V3MethodWriter methodWriter, int array_index) {
		methodWriter.tryCatchBlock(_->{
			methodWriter.aload(array_index);
			methodWriter.callInterface(BindablePositionedFunction.class, "invoke", Object.class,
					IUIElementManager.class,
					AHudCompiler.class,
					TextPos.class,
					HudderConfig.class,
					ObjectWrapper[].class);
		}, _->methodWriter.throwExecutionExceptionFromCaughtException(pos), Exception.class);
	}
	
	protected void visitUserFunction(V3MethodWriter methodWriter, int array_index) throws CompileException {
		var func = methodWriter.classWriter.user_functions.get(funcName);
		if (func!=null) {
			if (args.length<func.min_args())
				throw new CompileException("Too little arguements for function \""+funcName+'"', pos);
			if (args.length>func.max_args())
				throw new CompileException("Too many arguements for function \""+funcName+'"', pos);
			methodWriter.aload(0);
			methodWriter.aload(1);
			methodWriter.aload(2);
			methodWriter.aload(3);
			methodWriter.aload(array_index);
			methodWriter.callSelf(func.bytecode_name(),
					"(Ldev/ngspace/hudder/config/HudderConfig;"
					+ "Ljava/lang/String;"
					+ "Ljava/lang/String;"
					+ "[Ljava/lang/Object;)"
					+ Type.getDescriptor(V3HudInformation.class), false);
			methodWriter.getField("return_value", V3HudInformation.class, Object.class);
		} else {
			throw new CompileException("Unknown function: " + funcName, pos);
		}
	}
	
}
