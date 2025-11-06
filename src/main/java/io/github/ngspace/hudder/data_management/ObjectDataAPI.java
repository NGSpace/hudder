package io.github.ngspace.hudder.data_management;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ObjectDataAPI {private ObjectDataAPI() {}
	static List<Function<String, Object>> getters = new ArrayList<Function<String,Object>>();
	public static Object getObject(String key) {
		for (var getter : getters) {
			Object obj = getter.apply(key);
			if (obj!=null) return obj;
		}
		return null;
	}
	/**
	 * @deprecated use registerVariable
	 * @param function
	 */
	@Deprecated(since = "9.0.0", forRemoval = true)
	public static void addObjectGetter(Function<String, Object> function) {getters.add(function);}
	public static List<Function<String, Object>> getObjectGetters() {return getters;}
}
