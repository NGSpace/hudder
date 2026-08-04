package dev.ngspace.hudder.hudderv3.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.hudderv3.V3HudInformation;

public class V3ExecuteMethodWriter extends V3MethodWriter {

	// Builders
	public int topleft_builder_index;
	public int topright_builder_index;
	public int bottomleft_builder_index;
	public int bottomright_builder_index;
	
	public int selected_builder_index;
	
	// Scale
	public int topleft_scale_index;
	public int topright_scale_index;
	public int bottomleft_scale_index;
	public int bottomright_scale_index;
	
	// Return value
	public int return_value_index;
	
	private boolean builder_disabled;
	private boolean muted;

	public V3ExecuteMethodWriter(V3ClassWriter classWriter, String name, Class<?>[] args) {
		super(classWriter, name,
				args, V3HudInformation.class, null, new String[] {
					"dev/ngspace/hudder/exceptions/ExecutionException"
				});
		
		variableindex+=args.length; // the parameters
		
		
		// Create the StringBuilders
		initStringBuilder();
		topleft_builder_index = astore();
		initStringBuilder();
		topright_builder_index = astore();
		initStringBuilder();
		bottomleft_builder_index = astore();
		initStringBuilder();
		bottomright_builder_index = astore();
		
		// Default to topleft
		selected_builder_index = topleft_builder_index;
		
		// Define the scales
		loadConstant(Hudder.config.scale());
		topleft_scale_index = astore();
		loadConstant(Hudder.config.scale());
		topright_scale_index = astore();
		loadConstant(Hudder.config.scale());
		bottomleft_scale_index = astore();
		loadConstant(Hudder.config.scale());
		bottomright_scale_index = astore();
		
		// Return value
		methodVisitor.visitInsn(Opcodes.ACONST_NULL);
		return_value_index = astore();
	}



	public void appendStringConstant(String string) {
		if (shouldNotApppendToBuilder()) {
			return;
		}
		loadBuilder();
		loadConstant(string);
		call(StringBuilder.class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		pop();
	}
	
	private void loadBuilder() {
		aload(selected_builder_index);
	}

	public void appendToBuilderAndPop() {
		if (shouldNotApppendToBuilder()) {
			pop();
			return;
		}
		appendToBuilder();
		pop();
	}

	public void appendToBuilder() {
		if (shouldNotApppendToBuilder()) {
			pop();
			return;
		}
		Label end = new Label();
		Label append = new Label();
		Label append_double = new Label();
		
		int value_index = astore();
		aload(value_index);
		methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(Number.class));
		methodVisitor.visitJumpInsn(Opcodes.IFEQ, append);

		aloadDouble(value_index);
		
		methodVisitor.visitLdcInsn(1d);
		methodVisitor.visitInsn(Opcodes.DREM);
		methodVisitor.visitLdcInsn(0d);
		methodVisitor.visitInsn(Opcodes.DCMPG);
		methodVisitor.visitJumpInsn(Opcodes.IFNE, append_double);

		aload(value_index);
	    methodVisitor.visitTypeInsn(
	            Opcodes.CHECKCAST,
	            Type.getInternalName(Number.class)
	    );
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Number",
				"longValue",
				"()J",
				false
		);
		int long_index = lstore();
		loadBuilder();
		lload(long_index);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
				"(J)L"+STRING_BUILDER+";", false);
		methodVisitor.visitJumpInsn(Opcodes.GOTO, end);
		
		methodVisitor.visitLabel(append_double);
		loadBuilder();
		aload(value_index);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
				"(Ljava/lang/Object;)L"+STRING_BUILDER+";", false);
		methodVisitor.visitJumpInsn(Opcodes.GOTO, end);

		methodVisitor.visitLabel(append);
		loadBuilder();
		aload(value_index);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
				"(Ljava/lang/Object;)L"+STRING_BUILDER+";", false);
		
		methodVisitor.visitLabel(end);
	}



	private void callToString() {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "toString", "()Ljava/lang/String;", false);
	}



	@Override
	public void end() {
		addExecuteAReturn();
		endNoInsn();
	}



	public void addExecuteAReturn() {
		putLabel(finalLabel);
		methodVisitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(V3HudInformation.class));
		methodVisitor.visitInsn(Opcodes.DUP);
		aload(return_value_index);
		aload(topleft_builder_index);
		callToString();
		aloadFloat(topleft_scale_index);
		aload(bottomleft_builder_index);
		callToString();
		aloadFloat(bottomleft_scale_index);
		aload(topright_builder_index);
		callToString();
		aloadFloat(topright_scale_index);
		aload(bottomright_builder_index);
		callToString();
		aloadFloat(bottomright_scale_index);
		aload(0);
		getField("uimanager", ArrayElementManager.class);
		call(ArrayElementManager.class, "toUIElementArray", "()[Ldev/ngspace/hudder/uielements/AUIElement;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(V3HudInformation.class),
				"<init>", "(Ljava/lang/Object;Ljava/lang/String;FLjava/lang/String;FLjava/lang/String;FLjava/lang/String;F[Ldev/ngspace/hudder/uielements/AUIElement;)V", false);
		
		addAReturn();
	}



	public void setBuilderDisabled(boolean b) {
		builder_disabled = b;
	}
	
	public void setMuted(boolean b) {
		muted = b;
	}

	
	
	public boolean isBuilderDisabled() {
		return builder_disabled;
	}

	public boolean isMuted() {
		return muted;
	}
	
	

	public boolean shouldNotApppendToBuilder() {
		return isBuilderDisabled() || isMuted();
	}
	
}
