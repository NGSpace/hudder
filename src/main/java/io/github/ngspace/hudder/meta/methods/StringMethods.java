package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler.Value;

public class StringMethods implements IMethod {

	@Override
	public void invoke(ConfigInfo ci, Meta meta, ATextCompiler comp, String type, Value... args) throws CompileException {
		try {
			switch (type) {
				case "concat": {
					String str1 = args[1].asString();
					String str2 = args[2].asString();
					comp.put(args[3].getAbsoluteValue(),str1+str2);
					break;
				}
				case "multiplystring": {
					String str1 = args[1].asString();
					int times = args[2].asInt();
					comp.put(args[3].getAbsoluteValue(),str1.repeat(times));
					break;
				}
				case "substring": {
					String str1 = args[1].asString();
					int start = args[2].asInt();
					int end = args[3].asInt();
					try {
						comp.put(args[4].getAbsoluteValue(),str1.substring(start,end));
					} catch (IndexOutOfBoundsException e) {
						throw new CompileException(e.getLocalizedMessage());
					}
					break;
				}
				case "string": {
					comp.put(args[1].getAbsoluteValue(),args[2].getAbsoluteValue());
					break;
				}
				default: throw new IllegalArgumentException("Unexpected value: " + type);
			}
		} catch (IndexOutOfBoundsException e) {
			throw new CompileException("\""+type+"\" only accepts \""+type
					+(type.equals("concat")         ?",[string],[string],[result variable name]" :"")
					+(type.equals("multiplystring") ?",[string],[amount],[result variable name]" :"")
					+(type.equals("string")                  ?",[string],[result variable name]" :"")
					+(type.equals("substring") ?",[string],[start],[end],[result variable name]" :"")
					+ "\"");
		} catch (Exception e) {throw new CompileException(e.getLocalizedMessage());}
	}

}
