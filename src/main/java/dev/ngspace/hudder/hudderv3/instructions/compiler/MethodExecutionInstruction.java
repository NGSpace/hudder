package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3HudInformation;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.utils.ImplObjectWrapper;
import dev.ngspace.hudder.utils.ObjectWrapper;

public class MethodExecutionInstruction extends Instruction {
	
	private String[] builder;
	AV3Compiler comp;
	private boolean apiCall;

	public MethodExecutionInstruction(String[] builder, AV3Compiler comp, TextPos pos) {
		super(pos);
		this.builder = builder;
		this.comp = comp;
		this.apiCall = HudderV3Helper.api_consumers.containsKey("api_consumer_" + builder[0].toLowerCase().trim());
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		switch (builder[0]) {
			case "return":
				comp.parseVariable(builder[1], pos).visit(methodWriter);
				methodWriter.astore(methodWriter.return_value_index);
				methodWriter.jumpto(methodWriter.finalLabel);
				break;
			case "mute":
				methodWriter.setMuted(true);
				break;
			case "topleft":
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.topleft_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.astore(methodWriter.topleft_scale_index);
				}
				break;
			case "topright":
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.topright_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.astore(methodWriter.topright_scale_index);
				}
				break;
			case "bottomleft":
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.bottomleft_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.astore(methodWriter.bottomleft_scale_index);
				}
				break;
			case "bottomright":
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.bottomright_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.astore(methodWriter.bottomright_scale_index);
				}
				break;
			default:
				methodWriter.loadConstantUnsafe(builder.length-1);
				methodWriter.methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,
						Type.getInternalName(apiCall ? ObjectWrapper.class: Object.class));
				int array_index = methodWriter.astore();
				for (int i = 1;i<builder.length;i++) {
					comp.parseVariable(builder[i], pos).visit(methodWriter);
					int value_index = methodWriter.astore();
					methodWriter.aload(array_index);
					methodWriter.loadConstantUnsafe(i-1);
					if (apiCall) {
						methodWriter.newAndDup(ImplObjectWrapper.class);
					}
					methodWriter.aload(value_index);
					if (apiCall) {
						methodWriter.loadConstantUnsafe(pos.line());
						methodWriter.loadConstantUnsafe(pos.column());
						methodWriter.callSpecial(ImplObjectWrapper.class, "<init>", "(Ljava/lang/Object;II)V", false);
					}
					methodWriter.methodVisitor.visitInsn(Opcodes.AASTORE);
				}
				
				if (apiCall) {
					visitApiCall(methodWriter, array_index);
				} else {
					visitUserConsumer(methodWriter, array_index);
				}
		}
	}
	
	protected void visitApiCall(V3MethodWriter methodWriter, int array_index) {
		methodWriter.aload(0);
		methodWriter.aload(array_index);
		methodWriter.callSelf("api_consumer_"+builder[0].toLowerCase().trim(),
				"([Ldev/ngspace/hudder/utils/ObjectWrapper;)V", false);
	}
	
	protected void visitUserConsumer(V3MethodWriter methodWriter, int array_index) {
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
			methodWriter.throwRuntimeException("Unknown method: " + builder[0]);
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
