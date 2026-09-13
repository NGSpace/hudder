package dev.ngspace.hudder.utils;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class AccessUtils {
	private AccessUtils() {}
	
	public static boolean isFieldAccessible(Field field) {
		return isAccessible(field) && !field.isAnnotationPresent(NoAccess.class);
	}
	
	public static boolean isMethodAccessible(Method method) {
		return isAccessible(method) && !method.isAnnotationPresent(NoAccess.class);
	}
	
	private static boolean isAccessible(Member member) {
		return !member.accessFlags().contains(AccessFlag.PRIVATE);
	}
}
