package io.github.ngspace.hudder.v2runtime.runtime_elements;

import java.util.Arrays;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.methods.IMethod;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MethodV2RuntimeElement extends AV2RuntimeElement {

	private AV2Value[] values = {};
	private String type;
	private AV2Compiler compiler;
	private ConfigInfo info;
	private IMethod method;
	private int line;
	private int charpos;

	public MethodV2RuntimeElement(String[] args, AV2Compiler compiler, ConfigInfo info, V2Runtime runtime, int line, int charpos) throws CompileException {
		this.compiler = compiler;
		this.info = info;
		type = args[0];
		for (int i = 1;i<args.length;i++) {
			values = Arrays.copyOf(values, values.length+1);
			values[values.length-1] = compiler.getV2Value(runtime, args[i], line, charpos);
		}
		method = compiler.methodHandler.getMethodFromName(type);
		if (method.isDeprecated(type)) {
			Hudder.showWarningToast(MinecraftClient.getInstance(), Text.literal(type+" method is Deprecated!")
					.formatted(Formatting.BOLD), Text.literal("\u00A7a" + method.getDeprecationWarning(type)));
		}
		this.line = line;
		this.charpos = charpos;
	}
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		method.invoke(info, meta, compiler, type, line, charpos, values);
		return true;
	}
}
