package org.mozilla.javascript;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

// I fucking hate Rhino
public final class RhinoMethodFilter {

    private RhinoMethodFilter() {}

    public static NativeJavaMethod filter(NativeJavaMethod function, Predicate<Method> filter) {
        MemberBox[] overloads = function.methods;
        MemberBox[] allowed = Arrays.stream(overloads)
                        .filter(overload -> overload.isMethod() && filter.test(overload.method()))
                        .toArray(MemberBox[]::new);

        if (allowed.length == overloads.length) return function;
        if (allowed.length == 0) return null;

        NativeJavaMethod filtered = new NativeJavaMethod(allowed, function.getFunctionName());
        filtered.setPrototype(function.getPrototype());
        filtered.setParentScope(function.getParentScope());
        return filtered;
    }
}
