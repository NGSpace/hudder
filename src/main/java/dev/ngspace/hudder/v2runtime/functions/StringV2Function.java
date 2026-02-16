package dev.ngspace.hudder.v2runtime.functions;

import java.util.List;

import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class StringV2Function implements IV2Function {

	@Override public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos) 
					throws CompileException {
		
		Object value = args[0].get();
		
		if (args.length==2&&(boolean) args[1].get()&&value instanceof Number num)
			return cleanDouble(num.doubleValue());
		if (value instanceof List<?> s) {
			StringBuilder b = new StringBuilder();
			for (var v : s) b.append(v);
			return b.toString();
		}
		return value.toString();
	}
	
	public static String cleanDouble(double d) {
	    if(d == (long) d) return Long.toString((long)d);
	    else return Double.toString((long)d);
	}
}
