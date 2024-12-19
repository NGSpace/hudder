package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.functions.IV2Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class V2FunctionVar extends AV2Value {
	IV2Function func;
	V2Runtime runtime;
	AV2Value[] args = new AV2Value[0];
	private String funcname;

	public V2FunctionVar(V2Runtime runtime, AV2Compiler compiler, String name, String[] nonprocessedargs,
			int line, int charpos, String debugvalue) throws CompileException {
		super(line, charpos, debugvalue, compiler);
		this.runtime = runtime;
		this.func = compiler.functionHandler.getFunction(name);
		if (func==null) throw new CompileException("Unknown function name: \""+name+'"', line, charpos);
		this.funcname = name;
		this.args = new AV2Value[nonprocessedargs.length];
		
		for (int i = 0;i<nonprocessedargs.length;i++)
			this.args[i] = compiler.getV2Value(runtime, nonprocessedargs[i], line, charpos);
		
		if (func.isDeprecated(funcname)) {
			Hudder.showWarningToast(Component.literal(funcname+" function is Deprecated!").withStyle(ChatFormatting.BOLD),
					Component.literal("\u00A7a" + func.getDeprecationWarning(funcname)));
		}
	}

	@Override public Object get() throws CompileException {
		return func.execute(runtime, funcname, args, line, charpos);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a function", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}
	
}
