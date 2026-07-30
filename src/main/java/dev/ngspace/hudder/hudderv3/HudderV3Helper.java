package dev.ngspace.hudder.hudderv3;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.mozilla.javascript.ScriptableObject;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ImplObjectWrapper;
import dev.ngspace.hudder.utils.NoAccess;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.utils.ValueGetter;

public class HudderV3Helper {
	public static Map<String, BindableFunction> api_functions = new HashMap<String, BindableFunction>();
	public static Map<String, BindableConsumer> api_consumers = new HashMap<String, BindableConsumer>();

	private HudderV3Helper() {}
	
	public static boolean compare(Object val1, Object val2, String comparisonOperator) throws ExecutionException {
		if (val1==null||val2==null) {
			if (comparisonOperator.equals("=="))
				return val1==val2;
			else if (comparisonOperator.equals("!="))
				return val1!=val2;
			else throw new ExecutionException("Can not compare null values using the "+comparisonOperator+" operator.",
					-1, -1);
		}
		boolean areNums = false;
		double dou1 = 0;
		double dou2 = 0;
		if (val1 instanceof Number num1) {
			dou1 = num1.doubleValue();
			if (val2 instanceof Number num2) {
				dou2 = num2.doubleValue();
				areNums = true;
			}
		}
		return switch (comparisonOperator) {
			case "==" -> areNums ? dou1==dou2 :  Objects.equals(val1, val2);
			case "!=" -> areNums ? dou1!=dou2 : !Objects.equals(val1, val2);
			case ">=" -> dou1>=dou2;
			case "<=" -> dou1<=dou2;
			case ">"  -> dou1> dou2;
			case "<"  -> dou1< dou2;
			default -> throw new IllegalArgumentException("Unknown comparasion operator: " + comparisonOperator);
		};
	}
	
	public static String cleanDouble(double d) {
	    if(d == (long) d) return Long.toString((long)d);
	    else return Double.toString((long)d);
	}
	
	public static boolean hasApiFunction(String name) {
		System.out.println(name);
		return api_functions.containsKey(name);
	}
	
	
	public static Object callApiFunction(String name, ArrayElementManager uiManager,
			AVarTextCompiler compiler, Object... values) throws ExecutionException {
		return api_functions.get(name).invoke(uiManager, compiler,
				Stream.of(values).map(ImplObjectWrapper::new).toList().toArray(new ObjectWrapper[0]));
	}
	
	public static boolean hasApiConsumer(String name) {
		return api_consumers.containsKey(name);
	}
	
	public static void callApiConsumer(String name, ArrayElementManager uiManager,
			AVarTextCompiler compiler, Object... values) throws ExecutionException {
		api_consumers.get(name).invoke(uiManager, compiler,
				ImplObjectWrapper.fromArray(values));
	}

	public static Object getClassProperty(Object object, String objectExpression, String fieldName)
			throws ExecutionException {
		Class<?> objectClass = requireClassAccess(object, objectExpression, fieldName);

		Object result;
		if (object instanceof ValueGetter getter) {
			result = getter.get(fieldName);
		} else {
			try {
				Field field = objectClass.getDeclaredField(fieldName);
				if (!isAccessible(field)) {
					throw new ExecutionException("No property named \"" + fieldName + "\" in type \""
							+ objectClass.getSimpleName() + '"', -1, -1);
				}
				result = field.get(object);
			} catch (NoSuchFieldException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new ExecutionException("No property named \"" + fieldName + '"', -1, -1);
			} catch (ReflectiveOperationException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new ExecutionException("Failed Reflective Operation property named \"" + fieldName + '"',
						-1, -1);
			}
		}

		return normalizeClassAccessResult(result);
	}

	public static Object callClassMethod(Object object, String objectExpression, String functionName,
			Object[] parameters) throws ExecutionException {
		Class<?> objectClass = requireClassAccess(object, objectExpression, functionName);
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
					+ "\" in type \"" + objectClass.getSimpleName() + '"', -1, -1);
		}

		try {
			selectedMethod.setAccessible(true);
			return normalizeClassAccessResult(selectedMethod.invoke(object, selectedParameters));
		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if (Hudder.IS_DEBUG) target.printStackTrace();
			throw new ExecutionException(target.getMessage(), -1, -1, target);
		} catch (IllegalAccessException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new ExecutionException(e.getMessage(), -1, -1, e);
		}
	}

	private static Class<?> requireClassAccess(Object object, String objectExpression, String memberName)
			throws ExecutionException {
		if (object == null || object instanceof Class<?> || object instanceof ClassLoader) {
			throw new ExecutionException("Can't read \"" + memberName + "\" because \"" + objectExpression
					+ "\" is null", -1, -1);
		}

		Class<?> objectClass = object.getClass();
		if (objectClass.isPrimitive()) {
			throw new ExecutionException("Can not read properties of Numbers, Booleans and Chars : "
					+ objectExpression, -1, -1);
		}
		if (!HudderConfig.isAccessible(objectClass)) {
			throw new ExecutionException("Access to this type is not allowed", -1, -1);
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

	private static Object normalizeClassAccessResult(Object result) {
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
