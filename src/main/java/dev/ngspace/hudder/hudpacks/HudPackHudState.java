package dev.ngspace.hudder.hudpacks;

import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.IUIElementManager;

public class HudPackHudState {

	public static final String TOPLEFT = "topleft";
	public static final String BOTTOMLEFT = "bottomleft";
	public static final String TOPRIGHT = "topright";
	public static final String BOTTOMRIGHT = "bottomright";
	
	public String TLText = "";
	public String BLText = "";
	public String TRText = "";
	public String BRText = "";
	public float TLScale = 1;
	public float BLScale = 1;
	public float TRScale = 1;
	public float BRScale = 1;

	
	public HudInformation toResult(IUIElementManager elements) {
		return new HudInformation(TLText, TLScale, BLText, BLScale, TRText, TRScale, BRText, BRScale,
				elements.toUIElementArray());
	}
	
	protected void addString(String text, String pos) {
		switch (pos) {
			case TOPLEFT: TLText+=text; break;
			case BOTTOMLEFT: BLText+=text; break;
			case TOPRIGHT: TRText+=text; break;
			case BOTTOMRIGHT: BRText+=text; break;
			default: break;
		}
	}
}
