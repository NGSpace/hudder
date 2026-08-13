package dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class ComparisonVariableVisitor extends ExpressionVisitor {
	
	
	private ExpressionVisitor val1;
	private ExpressionVisitor val2;
	private String operator;
	public ComparisonVariableVisitor(AV3Compiler comp, String val1, String val2, String operator, TextPos pos)
			throws CompileException {
		super(comp, pos);
		this.val1 = comp.parseVariable(val1, pos);
		this.val2 = comp.parseVariable(val2, pos);
		this.operator = operator;
	}
	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		val1.visit(methodWriter);
		int val1index = methodWriter.astore();
		val2.visit(methodWriter);
		int val2index = methodWriter.astore();

		Label is_null = new Label();
		Label end = new Label();
		Label not_numbers = new Label();
		
		// Null Check
		methodWriter.aload(val1index);
		methodWriter.ifnull(is_null);
		methodWriter.aload(val2index);
		methodWriter.ifnull(is_null);
		
		// Numbers check
		methodWriter.aload(val1index);
		methodWriter.instanceOf(Number.class);
		methodWriter.ifeq(not_numbers);
		methodWriter.aload(val2index);
		methodWriter.instanceOf(Number.class);
		methodWriter.ifeq(not_numbers);
		
		// Numbers
		Label true_value = new Label();
		methodWriter.aload(val1index);
		methodWriter.checkcast(Number.class);
		methodWriter.doubleValue();
		methodWriter.aload(val2index);
		methodWriter.checkcast(Number.class);
		methodWriter.doubleValue();
		switch (operator) {
		    case "==" -> {
		        methodWriter.dcmpl();
		        methodWriter.ifeq(true_value);
		    }
		    case "!=" -> {
		        methodWriter.dcmpl();
		        methodWriter.ifne(true_value);
		    }
		    case ">=" -> {
		        methodWriter.dcmpl();
		        methodWriter.ifge(true_value);
		    }
		    case "<=" -> {
		        methodWriter.dcmpg();
		        methodWriter.ifle(true_value);
		    }
		    case ">" -> {
		        methodWriter.dcmpl();
		        methodWriter.ifgt(true_value);
		    }
		    case "<" -> {
		        methodWriter.dcmpg();
		        methodWriter.iflt(true_value);
		    }
			default -> throw new IllegalArgumentException("Unknown comparison operator: " + operator);
		}
		methodWriter.loadConstant(false);
		methodWriter.jumpto(end);
		methodWriter.putLabel(true_value);
		methodWriter.loadConstant(true);
		methodWriter.jumpto(end);
		
		// Not numbers
		methodWriter.putLabel(not_numbers);
		methodWriter.aload(val1index);
		methodWriter.aload(val2index);
		methodWriter.callStatic(Objects.class, "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false);
		switch (operator) {
			case "==": break; // No change
			case "!=": {
				Label false_result = new Label();
				methodWriter.ifne(false_result);
				methodWriter.loadConstant(true);
				methodWriter.jumpto(end);
				methodWriter.putLabel(false_result);
				methodWriter.loadConstant(false);
				methodWriter.jumpto(end);
				break;
			}
			default: {
				mostElaborateExceptionThrower(methodWriter, val1index, val2index);
			}
		}
		methodWriter.callStatic(Boolean.class, "valueOf", "(Z)Ljava/lang/Boolean;", false);
		methodWriter.jumpto(end);
		
		// Is null
		methodWriter.putLabel(is_null);
		switch (operator) {
			case "==": {
				Label eq = new Label();
				methodWriter.aload(val1index);
				methodWriter.aload(val2index);
				methodWriter.ifAcmpeq(eq);
				methodWriter.loadConstant(false);
				methodWriter.jumpto(end);
				methodWriter.putLabel(eq);
				methodWriter.loadConstant(true);
				break;
			}
			case "!=": {
				Label eq = new Label();
				methodWriter.aload(val1index);
				methodWriter.aload(val2index);
				methodWriter.ifAcmpeq(eq);
				methodWriter.loadConstant(true);
				methodWriter.jumpto(end);
				methodWriter.putLabel(eq);
				methodWriter.loadConstant(false);
				break;
			}
			default:
				methodWriter.throwExecutionException("Can not compare null values using the "
					+ operator + " operator.", pos);
		}
		
		methodWriter.putLabel(end);
	}
	
	protected void mostElaborateExceptionThrower(V3MethodWriter methodWriter, int val1index, int val2index) {
		// Value 1
		methodWriter.newStringBuilder();
		methodWriter.loadConstant("Unknown comparasion operator \"" + operator
				+ "\" for values of type: \"");
		methodWriter.call(StringBuilder.class, "append", "(Ljava/lang/String;)"
				+ "Ljava/lang/StringBuilder;", false);
		methodWriter.aload(val1index);
		methodWriter.call(Object.class, "getClass", "()Ljava/lang/Class;", false);
		methodWriter.call(Class.class, "getSimpleName", "()Ljava/lang/String;", false);
		methodWriter.call(StringBuilder.class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		
		// Value 2
		methodWriter.loadConstant("\" and \"");
		methodWriter.call(StringBuilder.class, "append", "(Ljava/lang/String;)"
				+ "Ljava/lang/StringBuilder;", false);
		methodWriter.aload(val2index);
		methodWriter.call(Object.class, "getClass", "()Ljava/lang/Class;", false);
		methodWriter.call(Class.class, "getSimpleName", "()Ljava/lang/String;", false);
		methodWriter.call(StringBuilder.class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

		methodWriter.loadConstant("\"");
		methodWriter.call(StringBuilder.class, "append", "(Ljava/lang/String;)"
				+ "Ljava/lang/StringBuilder;", false);
		
		methodWriter.call(StringBuilder.class, "toString", "()Ljava/lang/String;", false);
		methodWriter.newInsn(ExecutionException.class);
		methodWriter.dupX1();
		methodWriter.swap();
		methodWriter.loadConstantUnsafe(pos.line());
		methodWriter.loadConstantUnsafe(pos.column());
		methodWriter.callInit(ExecutionException.class, "(Ljava/lang/String;II)V");
		methodWriter.athrow();
	}
}
