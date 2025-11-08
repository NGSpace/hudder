package io.github.ngspace.hudder.v2runtime.values.operations;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.mozilla.javascript.ScriptableObject;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.utils.HudderUtils;
import io.github.ngspace.hudder.utils.NoAccess;
import io.github.ngspace.hudder.utils.ValueGetter;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class V2ClassPropertyCall extends AV2Value {
	
	private static final String[] forbiddenValuesAndFunctions = {"getClass","hashCode","wait","notify","notifyAll","clone","finalize"};
	private AV2Value classobj;
	private boolean isFunctionCall;
	private AV2Value[] functionCallArgs;
	private String funcName = "";
	private String fieldName = "";

	public V2ClassPropertyCall(int line, int charpos, String value, AV2Compiler c, V2Runtime runtime,
			AV2Value classobj, String prop) throws CompileException {
		super(line, charpos, value, c);
		this.classobj = classobj;
		if (!prop.startsWith("(")&&prop.endsWith(")")) {
			int argStart = prop.indexOf("(");
			if (argStart!=-1) {
				this.funcName = prop.substring(0, argStart);
				if (funcName.matches("^[a-zA-Z0-9_-]*$")) {
					
					String parametersString = prop.substring(argStart+1, prop.length()-1);
					
					String[] tokenizedArgs = HudderUtils.processParemeters(parametersString);
					functionCallArgs = new AV2Value[tokenizedArgs.length];
					
					for (int i=0;i<functionCallArgs.length;i++) 
						functionCallArgs[i] = c.getV2Value(runtime, tokenizedArgs[i], line, charpos);
					
					this.isFunctionCall = true;
				}
			}
		}
		if (!isFunctionCall) fieldName = prop;
		for (String forbidden : forbiddenValuesAndFunctions) {
			if (forbidden.equals(funcName)) throw new CompileException("No function named \""+funcName+'"',line,charpos);
			if (forbidden.equals(fieldName)) throw new CompileException("No property named \""+fieldName+'"',line,charpos);
		}
	}
	@Override public Object get() throws CompileException {
		Object obj = smartGet();
		if (obj==null) return null;
		if (!HudderConfig.isAccessible(obj.getClass()))
			return null;
		if (obj instanceof Set<?> r) return r.toArray();
		if (obj instanceof ScriptableObject en) {
			return new ValueGetter() {
				@Override public Object get(String n) {return en.get(n);}
				@Override public String toString() {return en.toString();}
			};
		}
		return obj;
	}

	public Object smartGet() throws CompileException {
		
		Object objValue = classobj.get();
		if (V2Runtime.NULL.equals(objValue)
				|| objValue instanceof Class<?>
				|| objValue instanceof ClassLoader)
			throw new CompileException("Can't read \"" + funcName+fieldName + "\" because \"" + classobj.value
					+ "\" is null", line, charpos);
		Class<?> objClass = objValue.getClass();
		
		if (objClass.isPrimitive())
			throw new CompileException("Can not read properties of Numbers, Booleans and Chars : "+classobj.value,line,charpos);

		if (!HudderConfig.isAccessible(objClass))
			throw new CompileException("Access to this type is not allowed",line,charpos);
		
		if (isFunctionCall) {
			Object[] parameters = new Object[functionCallArgs.length];
			Class<?>[] classes = new Class<?>[functionCallArgs.length];
			for (int i=0;i<functionCallArgs.length;i++) {
				parameters[i] = functionCallArgs[i].get();
				classes[i] = parameters[i].getClass();
			}
			Object[] finalParameters = Arrays.copyOf(parameters, parameters.length);
			Method finalmethod = null;
			for (Method method : objClass.getMethods()) {
				if (!funcName.equals(method.getName())||method.getParameterCount()!=classes.length
						||!isAccessible(method)) continue;
				boolean isCompatible = true;
				Class<?>[] v = method.getParameterTypes();
				
				for (int i=0;i<v.length;i++) {
					if (v[i].isPrimitive() && v[i] != char.class && v[i] != boolean.class) {
						if (parameters[i] instanceof Number num) {
							if      (v[i] == int.class   ) finalParameters[i] = num.intValue   ();
							else if (v[i] == float.class ) finalParameters[i] = num.floatValue ();
							else if (v[i] == double.class) finalParameters[i] = num.doubleValue();
							else if (v[i] == long.class  ) finalParameters[i] = num.longValue  ();
							else if (v[i] == byte.class  ) finalParameters[i] = num.byteValue  ();
							else if (v[i] == short.class ) finalParameters[i] = num.shortValue ();
							else isCompatible = false;
						} else isCompatible = false;
					} else if (!v[i].isAssignableFrom(classes[i])) isCompatible = false;
				}
				if (isCompatible) finalmethod = method;
			}
			if (finalmethod==null)
				throw new CompileException("No function named \""+getCallSign(classes)+"\" in type \"" +objClass.getSimpleName()+'"',line,charpos);
			
			try {
				return finalmethod.invoke(objValue, finalParameters);
			} catch (InvocationTargetException e) {
				if (Hudder.IS_DEBUG) e.getTargetException().printStackTrace();
				throw new CompileException(e.getTargetException().getMessage(), line, charpos, e.getTargetException());
			} catch (IllegalAccessException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new CompileException(e.getMessage(), line, charpos, e);
			}
		}
		
		try {
			Field f = objClass.getDeclaredField(fieldName);
			if (!isAccessible(f)) throw new CompileException("No property named \""+fieldName+"\" in type \"" +objClass.getSimpleName()+'"',line,charpos);
			return f.get(objValue);
		} catch (NoSuchFieldException e) {
			if (objValue instanceof ValueGetter getter) return getter.get(fieldName);
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new CompileException("No property named \""+fieldName+'"',line,charpos);
		} catch (ReflectiveOperationException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new CompileException("Failed Reflective Operation property named \""+fieldName+'"',line,charpos);
		}
	}
	
	

	private String getCallSign(Class<?>[] classes) {
		String res = funcName + "(";
		for (int i = 0;i<classes.length;i++) res+=classes[i].getSimpleName()+(classes.length==i+1?"":", ");
		return res + ")";
	}

	private boolean isAccessible(Field field) {
		return isAccessible((Member) field)&&!field.isAnnotationPresent(NoAccess.class);
	}
	private boolean isAccessible(Method method) {
		return isAccessible((Member) method)&&!method.isAnnotationPresent(NoAccess.class);
	}
	private boolean isAccessible(Member method) {
		return method.accessFlags().contains(AccessFlag.PUBLIC)&&!method.accessFlags().contains(AccessFlag.PRIVATE);
	}
	
	

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a ClassPropertyCall", line, charpos);
		
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}
}