package dev.ngspace.hudder.hudderv3.asm;

import org.objectweb.asm.Label;

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
		loadConstantUnsafe(Hudder.config.scale());
		topleft_scale_index = fstore();
		loadConstantUnsafe(Hudder.config.scale());
		topright_scale_index = fstore();
		loadConstantUnsafe(Hudder.config.scale());
		bottomleft_scale_index = fstore();
		loadConstantUnsafe(Hudder.config.scale());
		bottomright_scale_index = fstore();
		
		// Return value
		nullConstant();
		return_value_index = astore();
	}
	
	public void initStringBuilder() {
		newAndDup(StringBuilder.class);
		callInit(StringBuilder.class, "()V");
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
		instanceOf(Number.class);
		ifeq(append);

		aload(value_index);
		checkcast(Number.class);
		doubleValue();
		
		loadConstantUnsafe(1d);
		drem();
		loadConstantUnsafe(0d);
		dcmpg();
		ifne(append_double);

		aload(value_index);
		checkcast(Number.class);
		longValue();
		int long_index = lstore();
		loadBuilder();
		lload(long_index);
		call(StringBuilder.class, "append", "(J)Ljava/lang/StringBuilder;", false);
		jumpto(end);
		
		putLabel(append_double);
		loadBuilder();
		aload(value_index);
		call(StringBuilder.class, "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
		jumpto(end);

		putLabel(append);
		loadBuilder();
		aload(value_index);
		call(StringBuilder.class, "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
		
		putLabel(end);
	}



	private void callToString() {
		call(StringBuilder.class, "toString", "()Ljava/lang/String;", false);
	}



	@Override
	public void end() {
		addExecuteAReturn();
		endNoInsn();
	}



	public void addExecuteAReturn() {
		putLabel(finalLabel);
		newAndDup(V3HudInformation.class);
		aload(return_value_index);
		aload(topleft_builder_index);
		callToString();
		fload(topleft_scale_index);
		aload(bottomleft_builder_index);
		callToString();
		fload(bottomleft_scale_index);
		aload(topright_builder_index);
		callToString();
		fload(topright_scale_index);
		aload(bottomright_builder_index);
		callToString();
		fload(bottomright_scale_index);
		aload(0);
		getField("uimanager", ArrayElementManager.class);
		call(ArrayElementManager.class, "toUIElementArray", "()[Ldev/ngspace/hudder/uielements/AUIElement;", false);
		callInit(V3HudInformation.class, "("
				+ "Ljava/lang/Object;"
				+ "Ljava/lang/String;F"
				+ "Ljava/lang/String;F"
				+ "Ljava/lang/String;F"
				+ "Ljava/lang/String;F"
				+ "[Ldev/ngspace/hudder/uielements/AUIElement;)V");
		
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
