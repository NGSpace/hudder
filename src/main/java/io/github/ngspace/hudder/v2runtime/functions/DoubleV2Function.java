package io.github.ngspace.hudder.v2runtime.functions;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class DoubleV2Function implements IV2Function {
	
	@Override public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos)
			throws CompileException {
		Object value = args[0].get();
		
		switch (value) {
			case Number num:
				return num.intValue();
			case String str:
				return Double.parseDouble(str);
			case Boolean bool:
				return Boolean.TRUE.equals(bool)?1d:0d;
			case Character c:
				return ((int)c);
			default:
				if (value==null) throw new CompileException("Value of variable is null!", line, charpos);
				return Double.parseDouble(value.toString());
		}
	}
	
}
