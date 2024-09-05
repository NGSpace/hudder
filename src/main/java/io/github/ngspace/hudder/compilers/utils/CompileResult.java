package io.github.ngspace.hudder.compilers.utils;

import io.github.ngspace.hudder.methods.elements.AUIElement;

public class CompileResult {
	public String TopLeftText;
	public String BottomLeftText;
	public String TopRightText;
	public String BottomRightText;
	public float TLScale;
	public float BLScale;
	public float TRScale;
	public float BRScale;
	public AUIElement[] elements;
	
	
	/*
	 * Top Left, Scale
	 * Bottom Left, Scale
	 * Top Right, Scale
	 * Bottom Right, Scale
	 * Elements
	 */
	public CompileResult(String TL, float TLScale,
			String BL, float BLScale,
			String TR, float TRScale,
			String BR, float BRScale,
			AUIElement[] elements) {
		this.TopLeftText = TL;
		this.TLScale = TLScale;
		this.BottomLeftText = BL;
		this.TopRightText = TR;
		this.BottomRightText = BR;
		this.elements = elements;
		this.BLScale = BLScale;
		this.TRScale = TRScale;
		this.BRScale = BRScale;
	}
	
	public static CompileResult of(String s) {
		return new CompileResult(s, 1, "", 1, "", 1, "", 1, new AUIElement[0]);
	}
}
