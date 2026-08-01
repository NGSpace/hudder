package dev.ngspace.hudder.hudderv3.asm;

public class ByteArrayClassLoader extends ClassLoader {

    public ByteArrayClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> define(String binaryName, byte[] bytecode) {
        return defineClass(binaryName, bytecode, 0, bytecode.length);
    }
}
