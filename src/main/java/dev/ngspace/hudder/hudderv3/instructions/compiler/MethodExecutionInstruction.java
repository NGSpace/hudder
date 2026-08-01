package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3HudInformation;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;

public class MethodExecutionInstruction extends Instruction {
	
	private String[] builder;
	AV3Compiler comp;

	public MethodExecutionInstruction(String[] builder, AV3Compiler comp, TextPos pos) {
		super(pos);
		this.builder = builder;
		this.comp = comp;
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		switch (builder[0]) {
			case "return":
				comp.parseVariable(builder[1]).visit(methodWriter);
				methodWriter.astore(methodWriter.return_value_index);
				methodWriter.jumpto(methodWriter.finalLabel);
				break;
			case "mute":
				methodWriter.selected_builder_index = methodWriter.mute_builder_index;
				break;
			case "topleft":
				methodWriter.selected_builder_index = methodWriter.topleft_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1]).visit(methodWriter);
					methodWriter.astore(methodWriter.topleft_scale_index);
				}
				break;
			case "topright":
				methodWriter.selected_builder_index = methodWriter.topright_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1]).visit(methodWriter);
					methodWriter.astore(methodWriter.topright_scale_index);
				}
				break;
			case "bottomleft":
				methodWriter.selected_builder_index = methodWriter.bottomleft_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1]).visit(methodWriter);
					methodWriter.astore(methodWriter.bottomleft_scale_index);
				}
				break;
			case "bottomright":
				methodWriter.selected_builder_index = methodWriter.bottomright_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1]).visit(methodWriter);
					methodWriter.astore(methodWriter.bottomright_scale_index);
				}
				break;
			default:
				Label user_defined = new Label();
				Label end = new Label();
				
				methodWriter.loadConstantUnsafe(builder.length-1);
				methodWriter.newArray(Object.class);
				int array_index = methodWriter.astore();
				for (int i = 1;i<builder.length;i++) {
					comp.parseVariable(builder[i]).visit(methodWriter);
					int value_index = methodWriter.astore();
					methodWriter.aload(array_index);
					methodWriter.loadConstantUnsafe(i-1);
					methodWriter.aload(value_index);
					methodWriter.aastore();
				}
				methodWriter.loadConstant(builder[0].toLowerCase().trim());
				methodWriter.callStatic(HudderV3Helper.class, "hasApiConsumer", "(Ljava/lang/String;)Z", false);
				methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, user_defined);
				
				// API method
				methodWriter.loadConstant(builder[0].toLowerCase().trim());
				methodWriter.loadConstantUnsafe(pos.line());
				methodWriter.loadConstantUnsafe(pos.column());
				methodWriter.aload(0);
				methodWriter.getField("uimanager", ArrayElementManager.class);
				methodWriter.aload(0);
				methodWriter.getField("v3compiler", HudderV3Compiler.class);
				methodWriter.aload(array_index);
				methodWriter.callStatic(HudderV3Helper.class, "callApiConsumer",
						"(Ljava/lang/String;IILdev/ngspace/hudder/api/functionsandconsumers/ArrayElementManager;Ldev/ngspace/hudder/compilers/abstractions/AVarTextCompiler;[Ljava/lang/Object;)V", false);
				methodWriter.jumpto(end);
				
				// User method
				methodWriter.putLabel(user_defined);
				if (comp.user_methods.containsKey(builder[0].toLowerCase().trim())) {
					methodWriter.aload(0);
					methodWriter.aload(1);
					methodWriter.aload(2);
					methodWriter.aload(3);
					methodWriter.aload(array_index);
					methodWriter.callSelf(comp.user_methods.get(builder[0].toLowerCase().trim()), "(Ldev/ngspace/hudder/config/HudderConfig;"
							+ "Ljava/lang/String;"
							+ "Ljava/lang/String;"
							+ "[Ljava/lang/Object;)"
							+ Type.getDescriptor(V3HudInformation.class), false);
					methodWriter.pop();
				} else {
					methodWriter.throwExecutionException("Unknown method \"" + builder[0] + '"', pos);
				}
				
				methodWriter.putLabel(end);
		}
	}
	
	@Override
	public boolean canReturnValue() {
		return doesReturnValue();
	}
	
	@Override
	public boolean doesReturnValue() {
		return "return".equals(builder[0]);
	}
}
