package io.github.ngspace.hudder.v2runtime.functions;

import java.util.List;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.methods.MethodValue;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class StringV2Function implements IV2Function {

	@Override public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos)
			throws CompileException {
		
		Object value = args[0].get();
		
		if (args.length==2&&(boolean) args[1].get()&&value instanceof Number num)
			return MethodValue.cleanDouble(num.doubleValue());
		if (value instanceof List<?> s) {
			StringBuilder b = new StringBuilder();
			for (var v : s) {
//				System.out.println(v.toString());
				b.append(v);
			}
			return b.toString();
		}
		return value.toString();
	}
	
}
