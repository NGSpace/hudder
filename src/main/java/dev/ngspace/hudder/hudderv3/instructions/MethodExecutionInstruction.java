package dev.ngspace.hudder.hudderv3.instructions;


import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.V3HudInformation;

public class MethodExecutionInstruction extends Instruction {
	
	public String[] builder;
	private AV3Compiler comp;

	public MethodExecutionInstruction(AV3Compiler comp, String[] builder) {
		this.builder = builder;
		this.comp = comp;
	}

	@Override
	public void visit(V3ExecuteMethodWriter executeMethod, V3ClassWriter classWriter) throws ExecutionException {
		Label user_defined = new Label();
		Label end = new Label();
		
		executeMethod.loadConstantUnsafe(builder.length-1);
		executeMethod.methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,
				Type.getInternalName(Object.class));
		int array_index = executeMethod.astore();
		for (int i = 1;i<builder.length;i++) {
			comp.parseVariable(builder[i]).visitMethod(executeMethod);
			int value_index = executeMethod.astore();
			executeMethod.aload(array_index);
			executeMethod.loadConstantUnsafe(i-1);
			executeMethod.aload(value_index);
			executeMethod.methodVisitor.visitInsn(Opcodes.AASTORE);
		}
		executeMethod.loadConstant(builder[0].trim());
		executeMethod.callStatic(HudderV3Helper.class, "hasApiConsumer", "(Ljava/lang/String;)Z", false);
		executeMethod.methodVisitor.visitJumpInsn(Opcodes.IFEQ, user_defined);
		
		// API method
		executeMethod.loadConstant(builder[0].trim());
		executeMethod.aload(0);
		executeMethod.getField("uimanager", ArrayElementManager.class);
		executeMethod.aload(0);
		executeMethod.getField("v3compiler", HudderV3Compiler.class);
		executeMethod.aload(array_index);
		executeMethod.callStatic(HudderV3Helper.class, "callApiConsumer",
				"(Ljava/lang/String;Ldev/ngspace/hudder/api/functionsandconsumers/ArrayElementManager;Ldev/ngspace/hudder/compilers/abstractions/AVarTextCompiler;[Ljava/lang/Object;)V", false);
		executeMethod.jumpto(end);
		
		// User method
		executeMethod.putLabel(user_defined);
		if (comp.user_methods.containsKey(builder[0].trim())) {
			executeMethod.aload(0);
			executeMethod.aload(1);
			executeMethod.aload(2);
			executeMethod.aload(3);
			executeMethod.aload(array_index);
			executeMethod.callSelf(comp.user_methods.get(builder[0].trim()), "(Ldev/ngspace/hudder/config/HudderConfig;"
					+ "Ljava/lang/String;"
					+ "Ljava/lang/String;"
					+ "[Ljava/lang/Object;)"
					+ Type.getDescriptor(V3HudInformation.class), false);
			executeMethod.pop();
		}
		
		executeMethod.putLabel(end);
	}
	
}
