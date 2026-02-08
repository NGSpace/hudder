package dev.ngspace.hudder.v2runtime.values.modifiable;

import java.util.List;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2ArrayRead extends AV2Value {
	
	AV2Value indexValue;
	AV2Value array;
	
	public V2ArrayRead(String value, AV2Compiler compiler, V2Runtime runtime, int line, int charpos, String debugvalue) throws CompileException {
		super(line, charpos, debugvalue, compiler);
		int indexstart = value.lastIndexOf('[');
		String index = value.substring(indexstart+1,value.length()-1);
		indexValue = compiler.getV2Value(runtime, index, line, charpos);
		array = compiler.getV2Value(runtime, value.substring(0, indexstart), line, charpos);
	}

	@Override public Object get() throws CompileException {
		Object get = array.get();
		if (get instanceof List<?> b) return b.get(indexValue.asInt());
		else if (get instanceof Object[] b) return b[indexValue.asInt()];
		throw new CompileException(invalidTypeMessage("Array", value, get), line, charpos);
	}

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		@SuppressWarnings("unchecked")
		List<Object> list = array.asType(List.class);
		int index = indexValue.asInt();
		if (index==list.size()) {
			list.add(value);
		} else if (index>list.size()) {
			throw new CompileException("You can't set value " + index + " of array before all previous points are set",line,charpos);
		} else list.set(index, value);
	}
	
	//Even if the given array's isConstant function returns true, since the value of this can change then it is
	@Override public boolean isConstant() throws CompileException {return false;}
}
