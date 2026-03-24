package dev.ngspace.hudder.hudpacks;

import java.util.Arrays;
import java.util.Objects;

public record BufferedTexture(String path, byte[] img) {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(img);
		result = prime * result + Objects.hash(path);
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
		BufferedTexture other = (BufferedTexture) obj;
		return Arrays.equals(img, other.img) && Objects.equals(path, other.path);
	}

	@Override
	public String toString() {
		return "BufferedTexture [path=" + path + ", img=" + Arrays.toString(img) + "]";
	}
	
}
