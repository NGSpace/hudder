package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class MathVariableVisitor extends ExpressionVisitor {

	private final ExpressionVisitor[] variables;
	private final List<Character> operations;
	private boolean onlyaddition;
	private Object constant_value;
	
	public MathVariableVisitor(List<String> values, List<Character> operations, AV3Compiler comp, TextPos pos,
			String expression) throws CompileException {
		super(comp, pos, expression);
		this.variables = new ExpressionVisitor[values.size()];
		for (int i = 0;i<values.size();i++) {
			variables[i] = comp.parseVariable(values.get(i), pos);
		}
		this.operations = operations;
		this.onlyaddition = true;
		for (char c : operations)
			if (c!='+')
				onlyaddition = false;
		if (isConstant()) {
			Object[] consts = new Object[variables.length];
			List<Number> nums = new ArrayList<Number>(variables.length);
			boolean string = false;
			
			for (int i = 0;i<variables.length;i++) {
				consts[i] = variables[i].getConstantValue();
				if (consts[i] instanceof Number num)
					nums.add(num);
				else
					string = true;
			}
			if (string) {
				if (onlyaddition) {
					StringBuilder builder = new StringBuilder();
					for (Object obj : consts) {
						if (obj instanceof Number num)
							builder.append(HudderV3Helper.cleanDouble(num.doubleValue()));
						else
							builder.append(obj);
					}
					constant_value = builder.toString();
				} else {
					throw new CompileException("Strings may only be added to.", pos);
				}
			} else {
				double[] secondValues = new double[values.size()];
				char[] secondsOperations = new char[operations.size()];
				int realSecondValuesLength = 0;
				
				//Multiply, Divide and Modulo
				double result = nums.get(0).doubleValue();
				for (int i = 0;i<values.size();i++) {
					if (i==operations.size()) break;
					var val2 = nums.get(i+1).doubleValue();
					if      (operations.get(i)=='*') result = result * val2;
					else if (operations.get(i)=='/') result = result / val2;
					else if (operations.get(i)=='%') result = result % val2;
					else {
						secondValues[realSecondValuesLength] = result;
						secondsOperations[realSecondValuesLength] = operations.get(i);
						result = nums.get(i+1).doubleValue();
						realSecondValuesLength++;
					}
				}
				secondValues[realSecondValuesLength] = result;
				realSecondValuesLength++;
				
				//Plus and Minus

				result = secondValues[0];
				
				for (int i = 0;i<realSecondValuesLength;i++) {
					if (i==realSecondValuesLength-1) break;
					var val2 = secondValues[i+1];
					if      (secondsOperations[i]=='+') result = result + val2;
					else if (secondsOperations[i]=='-') result = result - val2;
				}
				
				constant_value = Double.valueOf(result);
			}
		}
	}

	@Override
	public void visit(V3MethodWriter writer) throws CompileException {
		if (isConstant()) {
			Object val = getConstantValue();
			writer.loadConstant(val);
			if (val instanceof Number) {
				writer.callStatic(Double.class, "valueOf", false, Double.class, Double.TYPE);
			}
			return;
		}
		
		int[] value_indexes = new int[variables.length];
		// Is String
		writer.loadConstantUnsafe(false);
		int is_string_index = writer.istore();
		
		for (int i = 0;i<variables.length;i++) {
			Label isNumber = new Label();
			variables[i].visit(writer);
			value_indexes[i] = writer.astore();
			writer.aload(value_indexes[i]);
			writer.instanceOf(Number.class);
			writer.ifne(isNumber);

			writer.loadConstantUnsafe(true);
			writer.istore(is_string_index);
			
			writer.putLabel(isNumber);
		}
		
		writer.iload(is_string_index);
		Label mathOperation = new Label();
		Label end = new Label();
		writer.ifeq(mathOperation);
		
		if (onlyaddition) {
			// Create StringBuilder
			writer.newAndDup(StringBuilder.class);
			writer.callInit(StringBuilder.class);
	
			int builder_index = writer.astore();
			
			for (int i = 0;i<variables.length;i++) {
				Label append = new Label();
				writer.aload(builder_index);
				writer.aload(value_indexes[i]);
				writer.dup();
				writer.instanceOf(Number.class);
				writer.ifeq(append);
				writer.checkcast(Number.class);
				writer.doubleValue();
				writer.callStatic(HudderV3Helper.class, "cleanDouble", false, String.class, Double.TYPE);
				writer.putLabel(append);
				writer.call(StringBuilder.class, "append", false, StringBuilder.class, Object.class);
				writer.pop();
			}
			
			writer.aload(builder_index);
			writer.call(StringBuilder.class, "toString", false, String.class);
			writer.jumpto(end);
		} else {
			writer.throwExecutionException("Strings may only be added to.", pos);
		}
		
		writer.putLabel(mathOperation);
		
		int operation_index = 0;
		writer.aload(value_indexes[0]);
		writer.checkcast(Number.class);
		writer.doubleValue();
		while (operation_index < operations.size() && isMultiplicative(operations.get(operation_index))) {
			writer.aload(value_indexes[operation_index + 1]);
			writer.checkcast(Number.class);
			writer.doubleValue();
			visitMultiplicativeOperation(writer, operations.get(operation_index));
			operation_index++;
		}

		while (operation_index < operations.size()) {
			char additive_operation = operations.get(operation_index);
			operation_index++;

			writer.aload(value_indexes[operation_index]);
			writer.checkcast(Number.class);
			writer.doubleValue();
			while (operation_index < operations.size() && isMultiplicative(operations.get(operation_index))) {
				writer.aload(value_indexes[operation_index + 1]);
				writer.checkcast(Number.class);
				writer.doubleValue();
				visitMultiplicativeOperation(writer, operations.get(operation_index));
				operation_index++;
			}
			
			if (additive_operation == '+') writer.dadd();
			else writer.dsub();
		}

		writer.callStatic(Double.class, "valueOf", false, Double.class, Double.TYPE);
		
		writer.putLabel(end);
	}
	
	private static boolean isMultiplicative(char operation) {
		return operation == '*' || operation == '/' || operation == '%';
	}

	private static void visitMultiplicativeOperation(V3MethodWriter writer, char operation) {
		if (operation == '*') {
			writer.dmul();
		} else if (operation == '/') {
			writer.ddiv();
		} else {
			writer.drem();
		}
	}

	
	@Override
	public boolean isConstant() {
        for (var v : variables) {
            if (!v.isConstant()) return false;
        }
        return true;
	}
	
	@Override
	public Object getConstantValue() {
		return constant_value;
	}

}
