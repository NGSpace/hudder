package dev.ngspace.hudder.api.compilers.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.uielements.AUIElement;

/**
 * @deprecated Use V3
 */
@Deprecated(since = "11.1.0", forRemoval = true)
public class CompileState implements IUIElementManager {

	@Deprecated(since = "11.1.0", forRemoval = true)
	public enum Sections {
		@Deprecated(since = "11.1.0", forRemoval = true)
		TOPLEFT("topleft"),
		@Deprecated(since = "11.1.0", forRemoval = true)
		BOTTOMLEFT("bottomleft"),
		@Deprecated(since = "11.1.0", forRemoval = true)
		TOPRIGHT("topright"),
		@Deprecated(since = "11.1.0", forRemoval = true)
		BOTTOMRIGHT("bottomright"),
		@Deprecated(since = "11.1.0", forRemoval = true)
		MUTE("mute");
		
		@Deprecated(since = "11.1.0", forRemoval = true)
		String sectionName;
		
		Sections(String sectionName) {
			this.sectionName = sectionName;
		}
		
		@Deprecated(since = "11.1.0", forRemoval = true)
		public String sectionName() {return sectionName;}
		
		@Deprecated(since = "11.1.0", forRemoval = true)
		public static Sections[] sections() {
			return values();
		}
		
		@Deprecated(since = "11.1.0", forRemoval = true)
		public static String[] sectionNames() {
			return Arrays.stream(values()).map(t->t.sectionName())
					.toArray(String[]::new);
		}
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public Sections section;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public String TLText = "";
	@Deprecated(since = "11.1.0", forRemoval = true)
	public String BLText = "";
	@Deprecated(since = "11.1.0", forRemoval = true)
	public String TRText = "";
	@Deprecated(since = "11.1.0", forRemoval = true)
	public String BRText = "";
	@Deprecated(since = "11.1.0", forRemoval = true)
	public float TLScale = 1;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public float BLScale = 1;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public float TRScale = 1;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public float BRScale = 1;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public boolean hasBroken = false;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public List<AUIElement> elements = new ArrayList<AUIElement>();
	@Deprecated(since = "11.1.0", forRemoval = true)
	public Object returnValue;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public boolean hasReturned;
	
	private final HudderConfig config;

	@Deprecated(since = "11.1.0", forRemoval = true)
	public CompileState(Sections section, HudderConfig config) {
		this.config = config;
		setTextLocation(section, config.scale());
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public void addString(String txt, boolean cleanup) {addString(txt,section,cleanup);}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
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
	
	@Deprecated(since = "11.1.0", forRemoval = true)
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
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public HudInformation toResult() {
		return new HudInformation(TLText, TLScale, BLText, BLScale, TRText, TRScale, BRText, BRScale,
				elements.toArray(new AUIElement[elements.size()]));
	}

	@Deprecated(since = "11.1.0", forRemoval = true)
	public void combineWithResult(HudInformation compile, boolean combineText) {
		if (combineText) {
			addString(compile.TopLeftText(), Sections.TOPLEFT, false);        TLScale = compile.TLScale();
			addString(compile.BottomLeftText(), Sections.BOTTOMLEFT, false);  BLScale = compile.BLScale();
			addString(compile.TopRightText(), Sections.TOPRIGHT, false);      TRScale = compile.TRScale();
			addString(compile.BottomRightText(), Sections.BOTTOMRIGHT, false);BRScale = compile.BRScale();
		}
		Collections.addAll(elements, compile.elements());
	}
	@Deprecated(since = "11.1.0", forRemoval = true)
	@Override public void addUIElement(AUIElement UIElement) {elements.add(UIElement);}
	@Deprecated(since = "11.1.0", forRemoval = true)
	@Override public AUIElement[] toUIElementArray() {return elements.toArray(new AUIElement[elements.size()]);}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public void setReturnValue(Object value) {hasReturned = true;returnValue = value;}
	
}
