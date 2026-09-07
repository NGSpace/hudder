package dev.ngspace.hudder.v2runtime;

import java.util.Arrays;
import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import dev.ngspace.hudder.api.compilers.compilers.AV2Compiler;
import dev.ngspace.hudder.api.compilers.utils.CompileState;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.runtime_elements.AV2RuntimeElement;

/**
 * @deprecated Use V3
 */
@SuppressWarnings("removal")
@Deprecated(since = "11.1.0", forRemoval = true)
public class V2Runtime {
	@Deprecated(since = "11.1.0", forRemoval = true)
	public final AV2Compiler compiler;
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected @Nullable V2Runtime scope;
	private HudderConfig config;
	/**
	 * Should stay mostly unused for now.
	 */
	@Deprecated(since = "11.1.0", forRemoval = true)
	public static final Object NULL = new Object() {
		@Override public boolean equals(Object obj) {return obj == this || obj == null;}
		@Override public int hashCode() {return super.hashCode();}
		@Override public String toString() {return "null";}
	};
	@Deprecated(since = "11.1.0", forRemoval = true)
	public V2Runtime(AV2Compiler compiler, V2Runtime scope, HudderConfig config) {
		this.compiler = compiler;
		this.scope = scope;
		this.config = config;
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected AV2RuntimeElement[] elements = new AV2RuntimeElement[0];
	@Deprecated(since = "11.1.0", forRemoval = true)
	public CompileState compileState;
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public CompileState execute() throws ExecutionException {
		compileState = new CompileState(CompileState.Sections.TOPLEFT, config);
		StringBuilder builder = new StringBuilder();
		for (int i = 0;i<elements.length;i++) {
			AV2RuntimeElement element = elements[i];
			if (!element.execute(compileState, builder)||compileState.hasReturned) {
				compileState.hasBroken = true;
				break;
			}
		}
		compileState.addString(builder.toString(), false);
		return compileState;
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public void addRuntimeElement(AV2RuntimeElement element) {elements = addToArray(elements, element);}
	@Deprecated(since = "11.1.0", forRemoval = true)
	public AV2RuntimeElement[] getElements() {return elements;}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	HashMap<String, Object> scopedVariables = new HashMap<String, Object>();
	@Deprecated(since = "11.1.0", forRemoval = true)
	public void putScoped(String name, Object value) {scopedVariables.put(name, value);}
	@Deprecated(since = "11.1.0", forRemoval = true)
	public Object getScoped(String name) {
		Object object = scopedVariables.get(name);
		if (object==null&&scope!=null) return scope.getScoped(name);
		return object;
	}

	@Deprecated(since = "11.1.0", forRemoval = true)
	public Object getVariable(String name) {
		Object object = getScoped(name);
		if (object==null) return compiler.getDynamicVariable(name);
		return object;
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public @Nullable V2Runtime getScope() {
		return scope;
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public V2Runtime getMasterScope() {
		return scope == null ? this : scope.getMasterScope();
	}
}