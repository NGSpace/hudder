package dev.ngspace.ngasmhelper;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.MethodVisitor;

public class MethodMaker<T> {

	private ClassMaker<T> classMaker;
	private MethodVisitor mv;

	public MethodMaker(ClassMaker<T> classMaker, MethodVisitor mv) {
		this.classMaker = classMaker;
		this.mv = mv;
		mv.visitCode();
		
		// 1. Load `builder` (local 1) onto the stack
		mv.visitVarInsn(ALOAD, 1);

		// 2. Push constant "hello"
		mv.visitLdcInsn("hello");

		// 3. Call builder.append("hello")
		mv.visitMethodInsn(
		        INVOKEVIRTUAL,
		        "java/lang/StringBuilder",            // internal name
		        "append",
		        "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
		        false
		);

		// 4. Discard the returned StringBuilder (since exec returns void)
		mv.visitInsn(POP);
	}

	public ClassMaker<T> build() {
		mv.visitInsn(RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
		return classMaker;
	}
	
}
