package dev.ngspace.hudder.hudderv3.instructions.variables;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3HudInformation;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;

public class FunctionCallVariableVisitor extends VariableVisitor {

	private final String[] args;
	private final String funcName;
	
	public FunctionCallVariableVisitor(String funcName, AV3Compiler comp, String[] args) {
		super(comp);
		this.args = args;
		this.funcName = funcName;
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		Label user_defined = new Label();
		Label end = new Label();
		
		methodWriter.loadConstantUnsafe(args.length);
		methodWriter.methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,
				Type.getInternalName(Object.class));
		int array_index = methodWriter.astore();
		for (int i = 0;i<args.length;i++) {
			comp.parseVariable(args[i]).visit(methodWriter);
			int value_index = methodWriter.astore();
			methodWriter.aload(array_index);
			methodWriter.loadConstantUnsafe(i);
			methodWriter.aload(value_index);
			methodWriter.methodVisitor.visitInsn(Opcodes.AASTORE);
		}
		
		methodWriter.loadConstant(funcName.toLowerCase().trim());
		methodWriter.callStatic(HudderV3Helper.class, "hasApiFunction", "(Ljava/lang/String;)Z", false);
		methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, user_defined);
		
		methodWriter.loadConstant(funcName.toLowerCase().trim());
		methodWriter.loadConstantUnsafe(-1);
		methodWriter.loadConstantUnsafe(-1);// TODO set this to real values
		methodWriter.aload(0);
		methodWriter.getField("uimanager", ArrayElementManager.class);
		methodWriter.aload(0);
		methodWriter.getField("v3compiler", HudderV3Compiler.class);
		methodWriter.aload(array_index);
		methodWriter.callStatic(HudderV3Helper.class, "callApiFunction",
				"(Ljava/lang/String;IILdev/ngspace/hudder/api/functionsandconsumers/ArrayElementManager;Ldev/ngspace/hudder/compilers/abstractions/AVarTextCompiler;[Ljava/lang/Object;)Ljava/lang/Object;", false);
		methodWriter.jumpto(end);
		
		// User method
		methodWriter.putLabel(user_defined);
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
		
		methodWriter.putLabel(end);
		
	}
	
}
