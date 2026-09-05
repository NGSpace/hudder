package dev.ngspace.hudder.hudderv3;

import dev.ngspace.hudder.api.compilers.utils.HudInformation;
import dev.ngspace.hudder.uielements.AUIElement;

public class V3HudInformation {
	public HudInformation hudInformation;
	public Object return_value;
	
	public V3HudInformation(Object return_value, String TopLeftText, float TLScale, String BottomLeftText, float BLScale,
		String TopRightText, float TRScale, String BottomRightText, float BRScale, AUIElement[] elements) {
		this.hudInformation = new HudInformation(TopLeftText, TLScale, BottomLeftText, BLScale, TopRightText,
				TRScale, BottomRightText, BRScale, elements);
		this.return_value = return_value;
	}
	
}
