package dev.ngspace.hudder.utils;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class AccessUtils {
	private AccessUtils() {}
	
	public static boolean isFieldAccessible(Field field) {
		return isMemberAccessible(field) && !field.isAnnotationPresent(NoAccess.class);
	}
	
	public static boolean isMethodAccessible(Method method) {
		return isMemberAccessible(method) && !method.isAnnotationPresent(NoAccess.class);
	}
	
	public static boolean isClassAccessible(Class<?> clazz) {
		return !clazz.accessFlags().contains(AccessFlag.PRIVATE) && !clazz.isAnnotationPresent(NoAccess.class);
	}
	
	private static boolean isMemberAccessible(Member member) {
		return !member.accessFlags().contains(AccessFlag.PRIVATE);
	}
	
	public static boolean isPublic(Member member) {
		return member.accessFlags().contains(AccessFlag.PUBLIC)&&!member.accessFlags().contains(AccessFlag.PRIVATE);
	}
}
