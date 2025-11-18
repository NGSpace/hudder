package dev.ngspace.ngasmhelper;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ClassMaker<T> {
	private Class<T> parent;
	private ClassWriter writer;
	private String name;
	

	public ClassMaker(Class<T> parent, String name, Class<?> interfce) {
		this.parent = parent;
		this.name = name;
	    writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
	    writer.visit(Opcodes.V21,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
			name,
			null,
			parent.getCanonicalName().replace('.', '/'),
			new String[] {interfce.getCanonicalName().replace('.', '/')});
	    writer.visitSource(name, null);
	    
        var mv = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false);
        mv.visitInsn(RETURN);
//        mv.visitMaxs(1, 1);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
	}
	
	public MethodMaker<T> makeMethod(String name, Class<?> ReturnType, Class<?>... classess) {
		var mv = writer.visitMethod(ACC_PUBLIC,
				name,
				ASMUtil.toBytecode(classess) + "V",
				null,
				null);
		return new MethodMaker<T>(this, mv);
	}

	public T build(ClassLoader loader) throws ReflectiveOperationException {
		return parent.cast(new DynamicClassLoader(loader).define(name, writer.toByteArray()).getDeclaredConstructor().newInstance());
	}
	
    // Simple loader for generated classes
    private static final class DynamicClassLoader extends ClassLoader {
        DynamicClassLoader(ClassLoader parent) {
            super(parent);
        }

        Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
