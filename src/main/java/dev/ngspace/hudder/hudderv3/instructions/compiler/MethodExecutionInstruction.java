package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
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
	private boolean run_method;

	public MethodExecutionInstruction(String[] builder, AV3Compiler comp, TextPos pos) {
		super(pos);
		this.builder = builder;
		this.comp = comp;
		this.run_method = builder[0].toLowerCase().matches("load|run|execute|add|compile");
		
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
				if (builder.length!=2) {
					throw new CompileException("Return method must have a return value!", pos);
				}
				comp.parseVariable(builder[1], pos).visit(methodWriter);
				methodWriter.astore(methodWriter.return_value_index);
				methodWriter.jumpto(methodWriter.finalLabel);
				break;
			case "throw":
				if (builder.length!=2) {
					throw new CompileException("Throw method must provide an error message!", pos);
				}
				comp.parseVariable(builder[1], pos).visit(methodWriter);
				methodWriter.checkcastSafe(String.class, pos);
				methodWriter.newInsn(ExecutionException.class);
				methodWriter.dupX1();
				methodWriter.swap();
				methodWriter.loadConstantUnsafe(pos.line());
				methodWriter.loadConstantUnsafe(pos.column());
				methodWriter.callInit(ExecutionException.class, String.class, Integer.TYPE, Integer.TYPE);
				methodWriter.athrow();
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
					methodWriter.checkcastSafe(Number.class, pos);
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
					methodWriter.checkcastSafe(Number.class, pos);
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
					methodWriter.checkcastSafe(Number.class, pos);
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
					methodWriter.checkcastSafe(Number.class, pos);
					methodWriter.floatValue();
					methodWriter.fstore(methodWriter.bottomright_scale_index);
				}
				break;
			default:
				boolean apiCall = !classWriter.user_methods.containsKey(builder[0].trim()) || run_method;
				
				if (apiCall) {
					if (run_method) {
						methodWriter.getHelper();
					} else {
						methodWriter.classWriter.loadApiConsumer(builder[0].trim());
						methodWriter.aload(0);
						methodWriter.getField("api_consumer_"+builder[0].trim(), BindablePositionedConsumer.class);
					}
					methodWriter.aload(0);
					methodWriter.getField("uimanager", ArrayElementManager.class);
					methodWriter.aload(0);
					methodWriter.getField("v3compiler", AV3Compiler.class);
					methodWriter.newAndDup(TextPos.class);
					methodWriter.loadConstantUnsafe(pos.line());
					methodWriter.loadConstantUnsafe(pos.column());
					methodWriter.callInit(TextPos.class, Integer.TYPE, Integer.TYPE);
					methodWriter.aload(1);
					if (run_method) {
						methodWriter.loadConstant(builder[0].toLowerCase());
						methodWriter.aload(methodWriter.topleft_builder_index);
						methodWriter.aload(methodWriter.topright_builder_index);
						methodWriter.aload(methodWriter.bottomleft_builder_index);
						methodWriter.aload(methodWriter.bottomright_builder_index);
					}
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
						methodWriter.callInit(ImplObjectWrapper.class, Object.class, Integer.TYPE, Integer.TYPE);
					}
					methodWriter.aastore();
				}
				
				if (run_method) {
					methodWriter.aload(array_index);
					methodWriter.call(HudderV3Helper.class, "runLoadMethod", false, null,
							IUIElementManager.class,
							AHudCompiler.class,
							TextPos.class,
							HudderConfig.class,
							String.class,
							StringBuilder.class,
							StringBuilder.class,
							StringBuilder.class,
							StringBuilder.class,
							ObjectWrapper[].class);
				} else if (apiCall) {
					if (!classWriter.helper.hasApiConsumer("api_consumer_"+builder[0].trim()))
						throw new CompileException("Unknown method: " + builder[0], pos);
					visitApiCall(methodWriter, array_index);
				} else {
					visitUserConsumer(methodWriter, array_index);
				}
		}
	}
	
	protected void visitApiCall(V3MethodWriter methodWriter, int array_index) {
		methodWriter.tryCatchBlock(_->{
			methodWriter.aload(array_index);
			methodWriter.callInterface(BindablePositionedConsumer.class, "invoke", null,
					IUIElementManager.class,
					AHudCompiler.class,
					TextPos.class,
					HudderConfig.class,
					ObjectWrapper[].class);
		}, _->methodWriter.throwExecutionExceptionFromCaughtException(pos), Exception.class);
	}
	
	protected void visitUserConsumer(V3MethodWriter methodWriter, int array_index) throws CompileException {
		var cons = methodWriter.classWriter.user_methods.get(builder[0].trim());
		if (builder.length-1<cons.min_args())
			throw new CompileException("Too little arguements for function \""+builder[0].trim()+'"', pos);
		if (builder.length-1>cons.max_args())
			throw new CompileException("Too many arguements for function \""+builder[0].trim()+'"', pos);
		methodWriter.aload(0);
		methodWriter.aload(1);
		methodWriter.aload(2);
		methodWriter.aload(3);
		methodWriter.aload(array_index);
		methodWriter.callSelf(cons.bytecode_name(), false,
				V3HudInformation.class,
				HudderConfig.class,
				String.class,
				String.class,
				Object[].class);
		methodWriter.pop();
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
