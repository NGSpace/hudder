package io.github.ngspace.hudder.v2runtime.values;

import java.util.List;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

public class V2ArrayRead extends AV2Value {
	
	AV2Value indexValue;
	AV2Value array;
	
	protected V2ArrayRead(String value, AV2Compiler compiler, V2Runtime runtime, int line, int charpos) throws CompileException {
		super(line, charpos);
		int indexstart = value.lastIndexOf('[');
		String index = value.substring(indexstart+1,value.length()-1);
		indexValue = compiler.getV2Value(runtime, index, line, charpos);
		array = compiler.getV2Value(runtime, value.substring(0, indexstart), line, charpos);
	}

	@Override public Object get() throws CompileException {
		return array.asList().get(indexValue.asInt());
	}

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		List<Object> list = array.asList();
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
