package dev.ngspace.hudder.hudderv3.instructions.variables;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
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
	private final TextPos pos;
	private final boolean apicall;
	
	public FunctionCallVariableVisitor(String funcName, AV3Compiler comp, String[] args, TextPos pos) {
		super(comp);
		this.args = args;
		this.funcName = funcName;
		this.pos = pos;
		this.apicall = comp.user_functions.containsKey(funcName);
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.loadConstantUnsafe(args.length);
		methodWriter.methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,
				Type.getInternalName(apicall ? ObjectWrapper[].class : Object.class));
		int array_index = methodWriter.astore();
		for (int i = 0;i<args.length;i++) {
			comp.parseVariable(args[i]).visit(methodWriter);
			int value_index = methodWriter.astore();
			methodWriter.aload(array_index);
			methodWriter.loadConstantUnsafe(i);
			if (apicall) {
				methodWriter.newAndDup(ImplObjectWrapper.class);
			}
			methodWriter.aload(value_index);
			if (apicall) {
				methodWriter.loadConstantUnsafe(pos.line());
				methodWriter.loadConstantUnsafe(pos.column());
				methodWriter.call(ImplObjectWrapper.class, "<init>", "(Ljava/lang/Object;II)V", false);
			}
			methodWriter.methodVisitor.visitInsn(Opcodes.AASTORE);
		}
		
		if (apicall) {
			visitUserFunction(methodWriter, array_index);
		} else {
			visitApiCall(methodWriter, array_index);
		}
	}
	
	protected void visitApiCall(V3MethodWriter methodWriter, int array_index) {
//		methodWriter.loadConstant(funcName.toLowerCase().trim());
//		methodWriter.loadConstantUnsafe(-1);
//		methodWriter.loadConstantUnsafe(-1);
//		methodWriter.aload(0);
//		methodWriter.getField("uimanager", ArrayElementManager.class);
//		methodWriter.aload(0);
//		methodWriter.getField("v3compiler", HudderV3Compiler.class);
		methodWriter.aload(0);
		methodWriter.aload(array_index);
		methodWriter.callSelf("api_function_"+funcName.toLowerCase().trim(),
				"([Ldev/ngspace/hudder/utils/ObjectWrapper;)Ljava/lang/Object;", false);
//		methodWriter.callStatic(HudderV3Helper.class, "callApiFunction",
//				"(Ljava/lang/String;IILdev/ngspace/hudder/api/functionsandconsumers/ArrayElementManager;Ldev/ngspace/hudder/compilers/abstractions/AVarTextCompiler;[Ljava/lang/Object;)Ljava/lang/Object;", false);
	}
	
	protected void visitUserFunction(V3MethodWriter methodWriter, int array_index) {
		if (comp.user_functions.containsKey(funcName)) {
			methodWriter.aload(0);
			methodWriter.aload(1);
			methodWriter.aload(2);
			methodWriter.aload(3);
			methodWriter.aload(array_index);
			methodWriter.callSelf(comp.user_functions.get(funcName), "(Ldev/ngspace/hudder/config/HudderConfig;"
					+ "Ljava/lang/String;"
					+ "Ljava/lang/String;"
					+ "[Ljava/lang/Object;)"
					+ Type.getDescriptor(V3HudInformation.class), false);
			methodWriter.getField("return_value", V3HudInformation.class, Object.class);
		} else {
			methodWriter.throwRuntimeException("Unknown function: " + funcName);
		}
	}
	
}
