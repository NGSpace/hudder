package dev.ngspace.hudder.hudderv3.v3variableinstructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;

public class FunctionCallVariableVisitor extends VariableVisitor {

	private final String[] args;
	private final String funcName;
	
	public FunctionCallVariableVisitor(String funcName, HudderV3Compiler comp, String[] args) {
		super(comp);
		this.args = args;
		this.funcName = funcName;
	}

	@Override
	public void visitMethod(V3MethodWriter methodWriter) throws ExecutionException {
		int[] value_indecies = new int[args.length];
		methodWriter.loadConstantUnsafe(value_indecies.length);
		methodWriter.methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,
				Type.getInternalName(Object.class));
		int array_index = methodWriter.astore();
		for (int i = 0;i<args.length;i++) {
			comp.parseVariable(args[i]).visitMethod(methodWriter);
			int value_index = methodWriter.astore();
			methodWriter.aload(array_index);
			methodWriter.loadConstantUnsafe(i);
			methodWriter.aload(value_index);
			methodWriter.methodVisitor.visitInsn(Opcodes.AASTORE);
		}
		methodWriter.loadConstant(funcName);
		methodWriter.aload(0);
		methodWriter.getField("uimanager", ArrayElementManager.class);
		methodWriter.aload(0);
		methodWriter.getField("v3compiler", HudderV3Compiler.class);
		methodWriter.aload(array_index);
		methodWriter.callStatic(HudderV3Helper.class, "callApiFunction",
				"(Ljava/lang/String;Ldev/ngspace/hudder/api/functionsandconsumers/ArrayElementManager;Ldev/ngspace/hudder/compilers/abstractions/AVarTextCompiler;[Ljava/lang/Object;)Ljava/lang/Object;", false);
		
	}
	
}
