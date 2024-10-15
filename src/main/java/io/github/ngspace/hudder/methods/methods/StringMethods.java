package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.MethodValue;

public class StringMethods implements IMethod {

	@Override
	public boolean isDeprecated(String name) {
		return true;
	}
	@Override
	public String getDeprecationWarning(String name) {
		return "Use \"" + (name.equalsIgnoreCase("multiplystring") ? "repeat" : name) + "\" function";
	}

	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, MethodValue... args) throws CompileException {
		try {
			switch (type) {
				case "concat": {
					String str1 = args[0].asString();
					String str2 = args[1].asString();
					comp.put(args[2].getAbsoluteValue(),str1+str2);
					break;
				}
				case "multiplystring": {
					String str1 = args[0].asString();
					int times = args[1].asInt();
					comp.put(args[2].getAbsoluteValue(),str1.repeat(times));
					break;
				}
				case "substring": {
					String str1 = args[0].asString();
					int start = args[1].asInt();
					int end = args[2].asInt();
					try {
						comp.put(args[3].getAbsoluteValue(),str1.substring(start,end));
					} catch (IndexOutOfBoundsException e) {
						throw new CompileException(e.getLocalizedMessage());
					}
					break;
				}
				default: throw new IllegalArgumentException("Unexpected value: " + type);
			}
		} catch (IndexOutOfBoundsException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new CompileException("\""+type+"\" only accepts ;"+type
					+(type.equals("concat")         ?",[string],[string],[result variable name]" :"")
					+(type.equals("multiplystring") ?",[string],[amount],[result variable name]" :"")
					+(type.equals("substring") ?",[string],[start],[end],[result variable name]" :"")
					+ ";");
		} catch (Exception e) {throw new CompileException(e.getLocalizedMessage());}
	}

}
