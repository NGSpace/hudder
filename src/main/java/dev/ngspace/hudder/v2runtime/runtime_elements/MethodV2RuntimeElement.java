package dev.ngspace.hudder.v2runtime.runtime_elements;

import java.util.Arrays;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CharPosition;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.methods.V2IMethod;
import dev.ngspace.hudder.v2runtime.values.AV2Value;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class MethodV2RuntimeElement extends AV2RuntimeElement {

	private AV2Value[] values = {};
	private String type;
	private AV2Compiler compiler;
	private HudderConfig info;
	private V2IMethod method;
	private CharPosition pos;
	private V2Runtime runtime;

	public MethodV2RuntimeElement(String[] args, AV2Compiler compiler, HudderConfig info, V2Runtime runtime, int line, int charpos) throws CompileException {
		this.compiler = compiler;
		this.info = info;
		this.runtime = runtime;
		this.type = args[0];
		for (int i = 1;i<args.length;i++) {
			values = Arrays.copyOf(values, values.length+1);
			values[values.length-1] = compiler.getV2Value(runtime, args[i], line, charpos);
		}
		this.method = compiler.methodHandler.getMethodFromName(type);
		if (method.isDeprecated(type)) {
			Hudder.showWarningToast(Component.literal(type+" method is Deprecated!").withStyle(ChatFormatting.BOLD),
					Component.literal("\u00A7a" + method.getDeprecationWarning(type)));
		}
		this.pos = new CharPosition(line, charpos);
	}
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		method.invoke(info, meta, compiler, runtime, type, pos, values);
		return true;
	}
}
