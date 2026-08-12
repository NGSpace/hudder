package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
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
		this.apiCall = HudderV3Helper.api_consumers.containsKey("api_consumer_" + builder[0].trim());
		
		if ("no_sys_var".equalsIgnoreCase(builder[0].trim())) {
			comp.system_variables = false;
		} else if ("sys_var".equalsIgnoreCase(builder[0].trim())) {
			comp.system_variables = true;
		}
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		switch (builder[0].toLowerCase().trim()) {
			case "return":
				comp.parseVariable(builder[1], pos).visit(methodWriter);
				methodWriter.astore(methodWriter.return_value_index);
				methodWriter.jumpto(methodWriter.finalLabel);
				break;
			case "no_sys_var", "sys_var":
				break;
			case "mute":
				if (methodWriter.isBuilderDisabled())
					break;
				methodWriter.setMuted(true);
				break;
			case "topleft":
				if (methodWriter.isBuilderDisabled())
					break;
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.topleft_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.checkcast(Number.class);
					methodWriter.floatValue();
					methodWriter.fstore(methodWriter.topleft_scale_index);
				}
				break;
			case "topright":
				if (methodWriter.isBuilderDisabled())
					break;
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.topright_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.checkcast(Number.class);
					methodWriter.floatValue();
					methodWriter.fstore(methodWriter.topright_scale_index);
				}
				break;
			case "bottomleft":
				if (methodWriter.isBuilderDisabled())
					break;
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.bottomleft_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.checkcast(Number.class);
					methodWriter.floatValue();
					methodWriter.fstore(methodWriter.bottomleft_scale_index);
				}
				break;
			case "bottomright":
				if (methodWriter.isBuilderDisabled())
					break;
				methodWriter.setMuted(false);
				methodWriter.selected_builder_index = methodWriter.bottomright_builder_index;
				if (builder.length>1) {
					comp.parseVariable(builder[1], pos).visit(methodWriter);
					methodWriter.checkcast(Number.class);
					methodWriter.floatValue();
					methodWriter.fstore(methodWriter.bottomright_scale_index);
				}
				break;
			default:

				
				if (apiCall) {
					methodWriter.classWriter.loadApiConsumer(builder[0].trim());
					methodWriter.aload(0);
					methodWriter.getField("api_consumer_"+builder[0].trim(), BindablePositionedConsumer.class);
					methodWriter.aload(0);
					methodWriter.getField("uimanager", ArrayElementManager.class);
					methodWriter.aload(0);
					methodWriter.getField("v3compiler", HudderV3Compiler.class);
					methodWriter.newAndDup(TextPos.class);
					methodWriter.loadConstantUnsafe(pos.line());
					methodWriter.loadConstantUnsafe(pos.column());
					methodWriter.callInit(TextPos.class, "(II)V");
				}
				
				methodWriter.loadConstantUnsafe(builder.length-1);
				methodWriter.newArray(apiCall ? ObjectWrapper.class: Object.class);
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
					methodWriter.aastore();
				}
				
				if (apiCall) {
					visitApiCall(methodWriter, array_index);
				} else {
					visitUserConsumer(methodWriter, array_index);
				}
		}
	}
	
	protected void visitApiCall(V3MethodWriter methodWriter, int array_index) {
		methodWriter.tryCatchBlock(_->{
			methodWriter.aload(array_index);
			methodWriter.callInterface(BindablePositionedConsumer.class, "invoke","("
					+ "Ldev/ngspace/hudder/api/functionsandconsumers/IUIElementManager;"
					+ "Ldev/ngspace/hudder/compilers/abstractions/AHudCompiler;"
					+ "Ldev/ngspace/hudder/compilers/utils/TextPos;"
					+ "[Ldev/ngspace/hudder/utils/ObjectWrapper;"
					+ ")V");
		}, _->methodWriter.throwExecutionExceptionFromCaughtException(pos), Exception.class);
	}
	
	protected void visitUserConsumer(V3MethodWriter methodWriter, int array_index) throws CompileException {
		if (methodWriter.classWriter.user_methods.containsKey(builder[0].trim())) {
			methodWriter.aload(0);
			methodWriter.aload(1);
			methodWriter.aload(2);
			methodWriter.aload(3);
			methodWriter.aload(array_index);
			methodWriter.callSelf(methodWriter.classWriter.user_methods.get(builder[0].trim()),
					"(Ldev/ngspace/hudder/config/HudderConfig;"
					+ "Ljava/lang/String;"
					+ "Ljava/lang/String;"
					+ "[Ljava/lang/Object;)"
					+ Type.getDescriptor(V3HudInformation.class), false);
			methodWriter.pop();
		} else {
			throw new CompileException("Unknown method: " + builder[0], pos);
		}
	}
	
	@Override
	public boolean canReturnValue() {
		return doesReturnValue();
	}
	
	@Override
	public boolean doesReturnValue() {
		return "return".equalsIgnoreCase(builder[0].trim());
	}
}
