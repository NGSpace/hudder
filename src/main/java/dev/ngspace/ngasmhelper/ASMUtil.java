package dev.ngspace.ngasmhelper;

public class ASMUtil {
	public static String toBytecode(Class<?>[] classes) {
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		for (int i = 0;i<classes.length;i++) {
			builder.append(toBytecode(classes[i]));
		}
		builder.append(')');
		return builder.toString();
	}
	public static String toBytecode(Class<?> clazz) {
		return "L"+clazz.getCanonicalName().replace('.', '/')+";";
	}
}
