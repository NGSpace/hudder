package io.github.ngspace.hudder.v2runtime.functions;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.methods.MethodValue;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class StringV2Function implements IV2Function {
	
	@Override
	public Object execute(V2Runtime runtime, String functionName, AV2Value[] args) throws CompileException {
		if (args.length<1) throw new CompileException("Too little parameters for "+functionName+"!");
		if (args.length>2) throw new CompileException("Too many parameters for "+functionName+"!");
		
		Object value = args[0].get();
		
		if (args.length==2&&(boolean) args[1].get()&&value instanceof Number num)
			return MethodValue.cleanDouble(num.doubleValue());
		return value.toString();
	}
	
}
