package io.github.ngspace.hudder.v2runtime.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.AVarTextCompiler;
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
		var res = new ArrayList<Object>();
		
		int r = new int[] {1,2}[1];
		
		for (char c : value.toCharArray())
			res.add(c);
//		compiler.put(value.substring(0, indexstart-1), res);
	}

	@Override public Object get() throws CompileException {
		@SuppressWarnings("unchecked")
		var list = (List<Object>) array.get();
		return list.get(indexValue.asInt());
	}

	@Override
	public void setValue(AVarTextCompiler compiler, Object value) throws CompileException {
		@SuppressWarnings("unchecked")
		var list = (List<Object>) array.get();
		int index = indexValue.asInt();
		if (index==list.size()) {
			list.add(value);
		} else if (index>list.size()) {
			throw new CompileException("You can't set value " + index + " of array before all previous points are set",line,charpos);
		} else list.set(index, value);
	}
	
}
