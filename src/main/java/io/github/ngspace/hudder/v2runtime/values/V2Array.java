package io.github.ngspace.hudder.v2runtime.values;

import java.util.ArrayList;
import java.util.List;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

public class V2Array extends AV2Value {

	private AV2Value[] values;

	protected V2Array(String[] strings, AV2Compiler compiler, V2Runtime runtime, int line, int charpos, String debugvalue)
			throws CompileException {
		super(line, charpos, debugvalue, compiler);
		values = new AV2Value[strings.length];
		for (int i = 0;i<strings.length;i++)
			values[i] = compiler.getV2Value(runtime, strings[i], line, charpos);
	}

	@Override public List<Object> get() throws CompileException {
		List<Object> array = new ArrayList<Object>();
		for (int i = 0;i<values.length;i++) array.add(values[i].get());
		return array;
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of an array constant", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}
}
