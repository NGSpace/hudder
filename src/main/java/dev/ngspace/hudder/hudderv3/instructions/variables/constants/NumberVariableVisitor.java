package dev.ngspace.hudder.hudderv3.instructions.variables.constants;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class NumberVariableVisitor extends VariableVisitor {

	private double value;

	public NumberVariableVisitor(AV3Compiler comp, String value, TextPos pos) {
		super(comp, pos);
		if (value.startsWith("0x")) {
			this.value = Integer.parseUnsignedInt(value.substring(2), 16);
		} else if (value.startsWith("#")) {
			this.value = Integer.parseUnsignedInt(value.substring(1), 16);
		} else {
			this.value = Double.parseDouble(value);
		}
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		methodWriter.loadConstant(value);
	}

	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public Object getConstantValue() {
		return value;
	}
	
}
