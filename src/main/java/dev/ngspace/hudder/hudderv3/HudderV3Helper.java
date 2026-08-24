package dev.ngspace.hudder.hudderv3;

import java.io.IOException;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.mozilla.javascript.ScriptableObject;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.utils.NoAccess;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.utils.ValueGetter;
import dev.ngspace.hudder.v2runtime.methods.LoadMethod;
import net.minecraft.network.chat.Component;

public class HudderV3Helper {
	public HudderConfig config;
	private AV3Compiler compiler;

	public HudderV3Helper(HudderConfig config, AV3Compiler compiler) {
		this.config = config;
		this.compiler = compiler;
	}

	public float getDefaultScale() {return config.scale();}
	public int getMaxWhile() {return !config.unsafeoperations() ? Short.MAX_VALUE : -1;}
	

	public BindablePositionedFunction getApiFunction(String name) {
		return compiler.api_functions.get(name);
	}
	public BindablePositionedConsumer getApiConsumer(String name) {
		return compiler.api_consumers.get(name);
	}
	
	public void runLoadMethod(IUIElementManager man, AHudCompiler<?> comp, TextPos pos,
			HudderConfig config, String type, StringBuilder topleft, StringBuilder topright,
			 StringBuilder bottomleft,  StringBuilder bottomright, ObjectWrapper... args)
					 throws ExecutionException {
		String file = args[0].asString();
		try {
			boolean AddText = args.length>1&&args[1].asBoolean() || type.equals("add");
			if (AddText && HudCompilationManager.isFirstRunSinceCacheClear)
				LoadMethod.showDeprecatedMessage(type);
			AHudCompiler<?> ecompiler=(args.length>2?Compilers.getCompilerFromName(args[2].asString()):comp);
			for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
			var result = ecompiler.processAndExecute(config, file, file);
			for (var uielement : result.elements()) {
				man.addUIElement(uielement);
			}
			if (AddText) {
				topleft.append(result.TopLeftText());
				topright.append(result.TopRightText());
				bottomleft.append(result.BottomLeftText());
				bottomright.append(result.BottomRightText());
			}
			for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getLocalizedMessage(), pos);
		} catch (CompileException e) {
			throw new ExecutionException(e.getFailureMessage() +"\nRun Failed for hud file " + file, pos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExecutionException(e, pos);
		}
	}
	
	public static String cleanDouble(double d) {
	    if(d % 1 == 0) return Long.toString((long)d);
	    else return Double.toString(d);
	}

	public static Object getClassProperty(Object object, String objectExpression, String fieldName,
			int line, int col) throws ExecutionException {
		Class<?> objectClass = getClassSafe(object, objectExpression, fieldName, line, col);
		try {
			Field field = objectClass.getDeclaredField(fieldName);
			if (!isAccessible(field)) {
				throw new ExecutionException("No property named \"" + fieldName + "\" in type \""
						+ objectClass.getSimpleName() + '"', line, col);
			}
			return normalizeResult(field.get(object));
		} catch (NoSuchFieldException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new ExecutionException("No property named \"" + fieldName + '"', line, col);
		} catch (ReflectiveOperationException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new ExecutionException("Failed Reflective Operation property named \"" + fieldName + '"',
					line, col);
		}
	}

	public static Object callClassMethod(Object object, String objectExpression, String functionName,
			Object[] parameters, int line, int col) throws ExecutionException {
		Class<?> objectClass = getClassSafe(object, objectExpression, functionName, line, col);
		Object[] safeParameters = parameters == null ? new Object[0] : parameters;
		Class<?>[] parameterClasses = Arrays.stream(safeParameters)
				.map(parameter -> parameter == null ? null : parameter.getClass())
				.toArray(Class<?>[]::new);

		Method selectedMethod = null;
		Object[] selectedParameters = null;
		for (Method method : objectClass.getMethods()) {
			if (!functionName.equals(method.getName())
					|| method.getParameterCount() != parameterClasses.length
					|| !isAccessible(method)) {
				continue;
			}

			Object[] convertedParameters = Arrays.copyOf(safeParameters, safeParameters.length);
			Class<?>[] declaredTypes = method.getParameterTypes();
			boolean compatible = true;
			for (int i = 0; i < declaredTypes.length; i++) {
				if (!convertParameter(declaredTypes[i], safeParameters[i], convertedParameters, i)) {
					compatible = false;
					break;
				}
			}

			if (compatible) {
				selectedMethod = method;
				selectedParameters = convertedParameters;
			}
		}

		if (selectedMethod == null) {
			throw new ExecutionException("No function named \"" + getCallSign(functionName, parameterClasses)
					+ "\" in type \"" + objectClass.getSimpleName() + '"', line, col);
		}

		try {
			selectedMethod.setAccessible(true);
			return normalizeResult(selectedMethod.invoke(object, selectedParameters));
		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if (Hudder.IS_DEBUG) target.printStackTrace();
			throw new ExecutionException(target.getMessage(), line, col, target);
		} catch (IllegalAccessException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new ExecutionException(e.getMessage(), line, col, e);
		}
	}

	private static Class<?> getClassSafe(Object object, String objectExpression, String memberName,
			int line, int col) throws ExecutionException {
		if (object == null || object instanceof Class<?> || object instanceof ClassLoader) {
			throw new ExecutionException("Can't read \"" + memberName + "\" because \"" + objectExpression
					+ "\" is null", line, col);
		}

		Class<?> objectClass = object.getClass();
		if (objectClass.isPrimitive()) {
			throw new SecurityException("Can not read properties of Numbers, Booleans and Chars : "
					+ objectExpression);
		}
		if (!HudderConfig.isAccessible(objectClass)) {
			throw new SecurityException("Access to this type is not allowed");
		}
		return objectClass;
	}

	private static boolean convertParameter(Class<?> declaredType, Object parameter, Object[] convertedParameters,
			int index) {
		if (parameter == null) return !declaredType.isPrimitive();

		if (!declaredType.isPrimitive()) return declaredType.isAssignableFrom(parameter.getClass());

		if (declaredType == boolean.class) return parameter instanceof Boolean;
		if (declaredType == char.class) return parameter instanceof Character;
		if (!(parameter instanceof Number number)) return false;

		if (declaredType == int.class) convertedParameters[index] = number.intValue();
		else if (declaredType == float.class) convertedParameters[index] = number.floatValue();
		else if (declaredType == double.class) convertedParameters[index] = number.doubleValue();
		else if (declaredType == long.class) convertedParameters[index] = number.longValue();
		else if (declaredType == byte.class) convertedParameters[index] = number.byteValue();
		else if (declaredType == short.class) convertedParameters[index] = number.shortValue();
		else return false;

		return true;
	}

	private static Object normalizeResult(Object result) {
		if (result == null) return null;
		if (!HudderConfig.isAccessible(result.getClass())) return null;
		if (result instanceof Set<?> set) return set.toArray();
		if (result instanceof ScriptableObject scriptable) {
			return new ValueGetter() {
				@Override
				public Object get(String name) {
					return scriptable.get(name);
				}

				@Override
				public String toString() {
					return scriptable.toString();
				}
			};
		}
		return result;
	}

	private static String getCallSign(String functionName, Class<?>[] parameterClasses) {
		StringBuilder result = new StringBuilder(functionName).append('(');
		for (int i = 0; i < parameterClasses.length; i++) {
			Class<?> parameterClass = parameterClasses[i];
			result.append(parameterClass == null ? "null" : parameterClass.getSimpleName());
			if (i + 1 < parameterClasses.length) result.append(", ");
		}
		return result.append(')').toString();
	}

	private static boolean isAccessible(Field field) {
		return isAccessible((Member) field) && !field.isAnnotationPresent(NoAccess.class);
	}

	private static boolean isAccessible(Method method) {
		return isAccessible((Member) method) && !method.isAnnotationPresent(NoAccess.class);
	}

	private static boolean isAccessible(Member member) {
		return !member.accessFlags().contains(AccessFlag.PRIVATE);
	}
}
