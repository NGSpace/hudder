package dev.ngspace.hudder.hudderv3.asm;

import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.asm.methods.ClassAccessMethodWriter;

public class V3MethodWriter extends ClassAccessMethodWriter {
	
	public V3ClassWriter classWriter;
	public String methodName;
	
	public V3MethodWriter(V3ClassWriter classWriter, String name, Class<?>[] parameters, Class<?> returntype,
			String signature, String[] exceptions) {
		super(classWriter.classWriter.visitMethod(Opcodes.ACC_PUBLIC, name,
				Type.getMethodDescriptor(returntype == null ? Type.VOID_TYPE : Type.getType(returntype),
						List.of(parameters).stream().map(Type::getType).toList().toArray(new Type[0])),
				signature, exceptions));
		this.classWriter = classWriter;
		this.methodName = name;
	}
	
	@Override
	public String getClassName() {
		return classWriter.classname;
	}
	
	public void newStringBuilder() {
		newAndDup(StringBuilder.class);
		callInit(StringBuilder.class, "()V");
	}

	public void ensureNotNull(String error, TextPos pos) {
		dup();
		Label nonnull = new Label();
		ifnonnull(nonnull);
		throwExecutionException(error, pos);
		putLabel(nonnull);
	}

	public void checkcastSafe(Class<?> type, TextPos pos) {
		checkcastSafe(type, pos, type.getSimpleName());
	}

	public void checkcastSafe(Class<?> type, TextPos pos, String friendly_name) {
		Label wrong_type = new Label();
		Label correct_type = new Label();
		Label end = new Label();

		dup();
		ifnull(correct_type);// The Verifier will cry if we don't checkcast the null
		dup();
		instanceOf(type);
		ifeq(wrong_type);
		
		putLabel(correct_type);
		checkcast(type);
		jumpto(end);
		
		putLabel(wrong_type);
		newStringBuilder();
		loadConstant("Can't convert object of type ");
		call(StringBuilder.class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		swap();
		call(Object.class, "getClass", "()Ljava/lang/Class;", false);
		call(Class.class, "getSimpleName", "()Ljava/lang/String;", false);
		call(StringBuilder.class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		loadConstant(" to type " + friendly_name);
		call(StringBuilder.class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		call(StringBuilder.class, "toString", "()Ljava/lang/String;", false);
		newInsn(ExecutionException.class);
		dupX1();
		swap();
		loadConstantUnsafe(pos.line());
		loadConstantUnsafe(pos.column());
		callSpecial(ExecutionException.class, "<init>", "(Ljava/lang/String;II)V", false);
		athrow();
		
		putLabel(end);
	}
	
	public void throwRuntimeException(String exception) {
		newAndDup(RuntimeException.class);
		loadConstant(exception);
		callSpecial(RuntimeException.class, "<init>", "(Ljava/lang/String;)V", false);
		athrow();
	}
	
	public void throwExecutionException(String exception, TextPos pos) {
		newAndDup(ExecutionException.class);
		loadConstant(exception);
		loadConstantUnsafe(pos.line());
		loadConstantUnsafe(pos.column());
		callSpecial(ExecutionException.class, "<init>", "(Ljava/lang/String;II)V", false);
		athrow();
	}

	public void throwExecutionExceptionFromCaughtException(TextPos pos) {
		Label execution_excpetion_handler = new Label();
		Label general_exception_handler = new Label();
		
		dup();
		instanceOf(ExecutionException.class);
		ifne(execution_excpetion_handler);
		
		putLabel(general_exception_handler);
		newInsn(ExecutionException.class);
		dupX1();
		swap();
		loadConstantUnsafe(pos.line());
		loadConstantUnsafe(pos.column());
		callInit(ExecutionException.class, "(Ljava/lang/Exception;II)V");
		athrow();
		
		putLabel(execution_excpetion_handler);
		dup();
		checkcast(ExecutionException.class);
		getField("line", ExecutionException.class, int.class);
		iflt(general_exception_handler);
		athrow();
	}

	public void getHelper() {
		aload(0);
		getField("helper", HudderV3Helper.class);
	}
}
