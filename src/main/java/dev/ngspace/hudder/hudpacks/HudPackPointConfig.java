package dev.ngspace.hudder.hudpacks;

import java.util.Arrays;
import java.util.Objects;

public record HudPackPointConfig(String type, String path, String entry_function, String[] conditions) {

	public HudPackPointConfig(String type, String path, String entry_function) {
		this(type, path, entry_function, null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(conditions);
		result = prime * result + Objects.hash(entry_function, path, type);
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
		HudPackPointConfig other = (HudPackPointConfig) obj;
		return Arrays.equals(conditions, other.conditions) && Objects.equals(entry_function, other.entry_function)
				&& Objects.equals(path, other.path) && Objects.equals(type, other.type);
	}
	
	@Override
	public String toString() {
		return "HudPackPointConfig [type=" + type + ", path=" + path + ", entry_function=" + entry_function
				+ ", conditions=" + Arrays.toString(conditions) + "]";
	}
	
}