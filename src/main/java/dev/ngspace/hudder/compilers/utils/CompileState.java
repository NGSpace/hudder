package dev.ngspace.hudder.compilers.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.uielements.AUIElement;

public class CompileState implements IUIElementManager {

	public enum Sections {
		TOPLEFT("topleft"),
		BOTTOMLEFT("bottomleft"),
		TOPRIGHT("topright"),
		BOTTOMRIGHT("bottomright"),
		MUTE("mute");
		
		String sectionName;
		
		Sections(String sectionName) {
			this.sectionName = sectionName;
		}
		
		public String sectionName() {return sectionName;}
		
		public static Sections[] sections() {
			return values();
		}
		
		public static String[] sectionNames() {
			return Arrays.stream(values()).map(t->t.sectionName())
					.toArray(String[]::new);
		}
	}
	
	public Sections section;
	public String TLText = "";
	public String BLText = "";
	public String TRText = "";
	public String BRText = "";
	public float TLScale = 1;
	public float BLScale = 1;
	public float TRScale = 1;
	public float BRScale = 1;
	public boolean hasBroken = false;
	public List<AUIElement> elements = new ArrayList<AUIElement>();
	public Object returnValue;
	public boolean hasReturned;
	
	private final HudderConfig config;

	public CompileState(Sections section, HudderConfig config) {
		this.config = config;
		setTextLocation(section, config.scale());
	}
	
	public void addString(String txt, boolean cleanup) {addString(txt,section,cleanup);}
	
	protected void addString(String txt, Sections section, boolean cleanup) {
		String text = txt;
		if (cleanup) {
			int buffer = config.methodBuffer();
			if (buffer<10)
				for (int i = 0; i<buffer;i++)
					try {
						if (text.startsWith("\r\n")) text = text.substring(2);
						if (text.endsWith("\r\n")) text = text.substring(0, text.length() - 2);
					} catch (StringIndexOutOfBoundsException _) {
						throw new IllegalArgumentException("Empty section \"" + section + "\"");
					}
			else text = text.trim();
		}
		switch (section) {
			case Sections.TOPLEFT: TLText+=text; break;
			case Sections.BOTTOMLEFT: BLText+=text; break;
			case Sections.TOPRIGHT: TRText+=text; break;
			case Sections.BOTTOMRIGHT: BRText+=text; break;
			case Sections.MUTE: break;
			default: throw new IllegalArgumentException("Unidentifiable meta state \"" + section + "\"");
		}
	}
	
	public void setTextLocation(Sections section, float d) {
		this.section = section;
		switch (section) {
			case Sections.TOPLEFT: TLScale = d; break;
			case Sections.BOTTOMLEFT: BLScale = d; break;
			case Sections.TOPRIGHT: TRScale = d; break;
			case Sections.BOTTOMRIGHT: BRScale = d; break;
			case Sections.MUTE: break;
		}
	}
	
	public HudInformation toResult() {
		return new HudInformation(TLText, TLScale, BLText, BLScale, TRText, TRScale, BRText, BRScale,
				elements.toArray(new AUIElement[elements.size()]));
	}

	public void combineWithResult(HudInformation compile, boolean combineText) {
		if (combineText) {
			addString(compile.TopLeftText(), Sections.TOPLEFT, false);        TLScale = compile.TLScale();
			addString(compile.BottomLeftText(), Sections.BOTTOMLEFT, false);  BLScale = compile.BLScale();
			addString(compile.TopRightText(), Sections.TOPRIGHT, false);      TRScale = compile.TRScale();
			addString(compile.BottomRightText(), Sections.BOTTOMRIGHT, false);BRScale = compile.BRScale();
		}
		Collections.addAll(elements, compile.elements());
	}
	@Override public void addUIElement(AUIElement UIElement) {elements.add(UIElement);}
	@Override public AUIElement[] toUIElementArray() {return elements.toArray(new AUIElement[elements.size()]);}
	
	public void setReturnValue(Object value) {hasReturned = true;returnValue = value;}
	
}
