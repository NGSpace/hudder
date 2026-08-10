package dev.ngspace.hudder.hudderv3.instructions.variables;

import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3HudInformation;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.utils.ImplObjectWrapper;
import dev.ngspace.hudder.utils.ObjectWrapper;

public class FunctionCallVariableVisitor extends VariableVisitor {

	private final String[] args;
	private final String funcName;
	private final boolean apiCall;
	
	public FunctionCallVariableVisitor(String funcName, AV3Compiler comp, String[] args, TextPos pos) {
		super(comp, pos);
		this.args = args;
		this.funcName = funcName;
		this.apiCall = HudderV3Helper.api_functions.containsKey("api_function_" + funcName.trim());
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		
		if (apiCall) {
			methodWriter.classWriter.loadApiFunction(funcName.trim());
			methodWriter.aload(0);
			methodWriter.getField("api_function_"+funcName.trim(), BindableFunction.class);
			methodWriter.aload(0);
			methodWriter.getField("uimanager", ArrayElementManager.class);
			methodWriter.aload(0);
			methodWriter.getField("v3compiler", HudderV3Compiler.class);
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
				methodWriter.callSpecial(ImplObjectWrapper.class, "<init>", "(Ljava/lang/Object;II)V", false);
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
		methodWriter.aload(array_index);
		methodWriter.callInterface(BindableFunction.class, "invoke",
				"(Ldev/ngspace/hudder/api/functionsandconsumers/IUIElementManager;Ldev/ngspace/hudder/compilers/abstractions/AHudCompiler;[Ldev/ngspace/hudder/utils/ObjectWrapper;)Ljava/lang/Object;");
	}
	
	protected void visitUserFunction(V3MethodWriter methodWriter, int array_index) throws CompileException {
		if (methodWriter.classWriter.user_functions.containsKey(funcName)) {
			methodWriter.aload(0);
			methodWriter.aload(1);
			methodWriter.aload(2);
			methodWriter.aload(3);
			methodWriter.aload(array_index);
			methodWriter.callSelf(methodWriter.classWriter.user_functions.get(funcName),
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
