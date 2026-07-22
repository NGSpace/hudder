package dev.ngspace.hudder.hudpacks;

import java.util.Arrays;
import java.util.Objects;

public record HudPackSettings(String name, String type, Object default_value, String[] values) {

	public HudPackSettings(String name, String type, Object default_value) {
		this(name, type, default_value, null);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		result = prime * result + Objects.hash(default_value, name, type);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HudPackSettings other = (HudPackSettings) obj;
		return Objects.equals(default_value, other.default_value) && Objects.equals(name, other.name)
				&& Objects.equals(type, other.type) && Arrays.equals(values, other.values);
	}

	@Override
	public String toString() {
		return "HudPackSettings [name=" + name + ", type=" + type + ", default_value=" + default_value + ", values="
				+ Arrays.toString(values) + "]";
	}
}
