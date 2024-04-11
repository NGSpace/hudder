package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;

public class StringMethods extends AMethod {

	@Override
	public void invoke(ConfigInfo ci, Meta meta, ATextCompiler compiler, String... args) throws CompileException {
		String type = "concat";
		try {
			type = args[0].toLowerCase();
			switch (type) {
				case "concat": {
					String str1 = String.valueOf(compiler.getVariable(args[1]));
					String str2 = String.valueOf(compiler.getVariable(args[2]));
					compiler.put(args[3],str1+str2);
					break;
				}
				case "multiplystring": {
					String str1 = String.valueOf(compiler.getVariable(args[1]));
					int times = (int) tryParse(compiler.getVariable(args[2]));
					compiler.put(args[3],str1.repeat(times));
					break;
				}
				case "substring": {
					String str1 = String.valueOf(compiler.getVariable(args[1]));
					int start = (int) tryParse(compiler.getVariable(args[2]));
					int end = (int) tryParse(compiler.getVariable(args[3]));
					try {
						compiler.put(args[4],str1.substring(start,end));
					} catch (IndexOutOfBoundsException e) {
						throw new CompileException(e.getLocalizedMessage());
					}
					break;
				}
				case "string": {
					compiler.put(args[1],args[2]);
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
