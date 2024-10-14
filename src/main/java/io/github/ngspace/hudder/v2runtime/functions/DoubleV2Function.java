package io.github.ngspace.hudder.v2runtime.functions;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class DoubleV2Function implements IV2Function {
	
	@Override
	public Object execute(V2Runtime runtime, String functionName, AV2Value[] args) throws CompileException {
		Object value = args[0].get();
		
		switch (value) {
			case Number num:
				return num.doubleValue();
			case String str:
				return Double.parseDouble(str);
			case Boolean bool:
				return Boolean.TRUE.equals(bool)?1d:0d;
			default:
				if (value==null)
					throw new CompileException("Value of variable is null!");
				return Double.parseDouble(value.toString());
		}
	}
	
}
