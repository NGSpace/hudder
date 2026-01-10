package io.github.ngspace.hudder.data_management;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @deprecated use DataVariableRegistry
 */
@Deprecated(since = "9.0.0", forRemoval = true)
public class ObjectDataAPI {private ObjectDataAPI() {}
	@Deprecated(since = "9.0.0", forRemoval = true)
	static List<Function<String, Object>> getters = new ArrayList<Function<String,Object>>();
	@Deprecated(since = "9.0.0", forRemoval = true)
	public static Object getObject(String key) {
		for (var getter : getters) {
			Object obj = getter.apply(key);
			if (obj!=null) return obj;
		}
		return null;
	}
	/**
	 * @deprecated use {@link io.github.ngspace.hudder.data_management.api.DataVariableRegistry}
	 * @param function
	 */
	@Deprecated(since = "9.0.0", forRemoval = true)
	public static void addObjectGetter(Function<String, Object> function) {getters.add(function);}
	@Deprecated(since = "9.0.0", forRemoval = true)
	public static List<Function<String, Object>> getObjectGetters() {return getters;}
}
