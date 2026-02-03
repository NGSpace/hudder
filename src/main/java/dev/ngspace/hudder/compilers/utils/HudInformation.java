package dev.ngspace.hudder.compilers.utils;

import java.util.Arrays;
import java.util.Objects;

import dev.ngspace.hudder.uielements.AUIElement;

public record HudInformation(String TopLeftText, float TLScale, String BottomLeftText, float BLScale,
		String TopRightText, float TRScale, String BottomRightText, float BRScale, AUIElement[] elements) {

	public static HudInformation of(String s) {
		return new HudInformation(s, 1, "", 1, "", 1, "", 1, new AUIElement[0]);
	}
	
	@Override
	public String toString() {
		return "HudInformation [TopLeftText=" + TopLeftText + ", TLScale=" + TLScale + ", BottomLeftText="
				+ BottomLeftText + ", BLScale=" + BLScale + ", TopRightText=" + TopRightText + ", TRScale=" + TRScale
				+ ", BottomRightText=" + BottomRightText + ", BRScale=" + BRScale + ", elements="
				+ Arrays.toString(elements) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + Objects.hash(BLScale, BRScale, BottomLeftText, BottomRightText, TLScale, TRScale,
				TopLeftText, TopRightText);
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
		HudInformation other = (HudInformation) obj;
		return Float.floatToIntBits(BLScale) == Float.floatToIntBits(other.BLScale)
				&& Float.floatToIntBits(BRScale) == Float.floatToIntBits(other.BRScale)
				&& Objects.equals(BottomLeftText, other.BottomLeftText)
				&& Objects.equals(BottomRightText, other.BottomRightText)
				&& Float.floatToIntBits(TLScale) == Float.floatToIntBits(other.TLScale)
				&& Float.floatToIntBits(TRScale) == Float.floatToIntBits(other.TRScale)
				&& Objects.equals(TopLeftText, other.TopLeftText) && Objects.equals(TopRightText, other.TopRightText)
				&& Arrays.equals(elements, other.elements);
	}
}
