package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;
import io.github.ngspace.hudder.meta.MethodValue;

public class StringMethods implements IMethod {

	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, MethodValue... args) throws CompileException {
		try {
			switch (type) {
				case "concat": {
					String str1 = args[1].asString();
					String str2 = args[2].asString();
					comp.put(args[3].asStringSafe(),str1+str2);
					break;
				}
				case "multiplystring": {
					String str1 = args[1].asString();
					int times = args[2].asInt();
					comp.put(args[3].asStringSafe(),str1.repeat(times));
					break;
				}
				case "substring": {
					String str1 = args[1].asString();
					int start = args[2].asInt();
					int end = args[3].asInt();
					try {
						comp.put(args[4].asStringSafe(),str1.substring(start,end));
					} catch (IndexOutOfBoundsException e) {
						throw new CompileException(e.getLocalizedMessage());
					}
					break;
				}
				default: throw new IllegalArgumentException("Unexpected value: " + type);
			}
		} catch (IndexOutOfBoundsException e) {
			throw new CompileException("\""+type+"\" only accepts ;"+type
					+(type.equals("concat")         ?",[string],[string],[result variable name]" :"")
					+(type.equals("multiplystring") ?",[string],[amount],[result variable name]" :"")
					+(type.equals("string")                  ?",[string],[result variable name]" :"")
					+(type.equals("substring") ?",[string],[start],[end],[result variable name]" :"")
					+ ";");
		} catch (Exception e) {throw new CompileException(e.getLocalizedMessage());}
	}

}
