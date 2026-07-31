package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class MathVariableVisitor extends VariableVisitor {

	private final String[] values;
	private final char[] operations;
	
	public MathVariableVisitor(String[] values, char[] operations, AV3Compiler comp) {
		super(comp);
		this.values = values;
		this.operations = operations;
	}

	@Override
	public void visit(V3MethodWriter writer) throws CompileException {
		int[] value_indexes = new int[values.length];
		// Is String
		writer.loadConstantUnsafe(false);
		int is_string_index = writer.istore();
		
		for (int i = 0;i<values.length;i++) {
			Label isNumber = new Label();
			comp.parseVariable(values[i]).visit(writer);
			value_indexes[i] = writer.astore();
			writer.aload(value_indexes[i]);
			writer.methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(Number.class));
			writer.methodVisitor.visitJumpInsn(Opcodes.IFNE, isNumber);

			writer.loadConstantUnsafe(true);
			writer.methodVisitor.visitVarInsn(Opcodes.ISTORE, is_string_index);
			
			writer.putLabel(isNumber);
		}
		
		writer.iload(is_string_index);
		Label mathOperation = new Label();
		Label end = new Label();
		writer.methodVisitor.visitJumpInsn(Opcodes.IFEQ, mathOperation);

		// Create StringBuilder
		writer.initStringBuilder();
		int builder_index = writer.astore();
		
		for (int i = 0;i<values.length;i++) {
			Label append = new Label();
			writer.aload(builder_index);
			writer.aload(value_indexes[i]);
			writer.dup();
			writer.methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(Number.class));
			writer.methodVisitor.visitJumpInsn(Opcodes.IFEQ, append);
			writer.checkcast(Number.class);
			writer.doubleValue();
			writer.callStatic(HudderV3Helper.class, "cleanDouble", "(D)Ljava/lang/String;", false);
			writer.putLabel(append);
			writer.call(StringBuilder.class, "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
			writer.pop();
		}
		
		writer.aload(builder_index);
		writer.call(StringBuilder.class, "toString", "()Ljava/lang/String;", false);
		writer.jumpto(end);
		
		writer.putLabel(mathOperation);
		
		int operation_index = 0;
		writer.aloadDouble(value_indexes[0]);
		while (operation_index < operations.length && isMultiplicative(operations[operation_index])) {
			writer.aloadDouble(value_indexes[operation_index + 1]);
			visitMultiplicativeOperation(writer, operations[operation_index]);
			operation_index++;
		}

		while (operation_index < operations.length) {
			char additive_operation = operations[operation_index];
			operation_index++;

			writer.aloadDouble(value_indexes[operation_index]);
			while (operation_index < operations.length && isMultiplicative(operations[operation_index])) {
				writer.aloadDouble(value_indexes[operation_index + 1]);
				visitMultiplicativeOperation(writer, operations[operation_index]);
				operation_index++;
			}

			writer.methodVisitor.visitInsn(additive_operation == '+' ? Opcodes.DADD : Opcodes.DSUB);
		}

		writer.callStatic(Double.class, "valueOf", "(D)Ljava/lang/Double;", false);
		
		writer.putLabel(end);
	}
	
	private static boolean isMultiplicative(char operation) {
		return operation == '*' || operation == '/' || operation == '%';
	}

	private static void visitMultiplicativeOperation(V3MethodWriter writer, char operation) {
		if (operation == '*') {
			writer.methodVisitor.visitInsn(Opcodes.DMUL);
		} else if (operation == '/') {
			writer.methodVisitor.visitInsn(Opcodes.DDIV);
		} else {
			writer.methodVisitor.visitInsn(Opcodes.DREM);
		}
	}

}
