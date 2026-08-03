package dev.ngspace.hudder.config;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.UnaryOperator;

import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.config.HudSelectionList.HudEntry;
import dev.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class HudSelectionList extends ObjectSelectionList<HudEntry> {
	
	private HudderUserSettings config;
	public String comp;
	
	// There are more formats but those are the only ones that matter, as far as I am aware minecraft
	// only supports those formats
	private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg");

	public HudSelectionList(Minecraft minecraft, File folder, HudderUserSettings config) {
		super(minecraft, 0, 0, 0, 18);
		this.config = config;
		comp = Compilers.findEntryFromName(config.compilername).orElse(Compilers.getEntryFromName("hudder")).displayname();
		
		addEntry(new TitleEntry(Component.translatable("hudder.mainfile.title"),
				Component.translatable("hudder.mainfile.subtitle")), 24);
		
		for (File hud : folder.listFiles()) {
			if (hud.isDirectory() && isEmptyOrImagesOnly(hud)) {
				continue;
			}
			String name = hud.getName();
			addEntry(name, Compilers.getSupportedCompilersForFilepath(name), name.equals(config.mainfile));
		}
		var selected = getSelected();
		if (selected==null)
			addEntry(config.mainfile, Compilers.getSupportedCompilersForFilepath(config.mainfile), true);
		
		setScrollAmount(0); // I don like how it scrolls for me, disgusting, vile even...
		
	}
	
	private static boolean isEmptyOrImagesOnly(File directory) {
		File[] contents = directory.listFiles();

		// Do not treat an unreadable directory as empty.
		if (contents == null) {
			return false;
		}

		for (File file : contents) {
			if (file.isDirectory()) {
				if (!isEmptyOrImagesOnly(file)) {
					return false;
				}
			} else if (!isImage(file)) {
				return false;
			}
		}

		// Also returns true when the directory is empty.
		return true;
	}

	private static boolean isImage(File file) {
		String name = file.getName();
		int dot = name.lastIndexOf('.');

		if (dot < 0 || dot == name.length() - 1) {
			return false;
		}

		String extension = name.substring(dot + 1).toLowerCase();
		return IMAGE_EXTENSIONS.contains(extension);
	}
	
	public void addEntry(String filepath, String[] compilers, boolean isSelected) {
		HudEntry entry = new HudEntry(filepath, compilers);
		addEntry(entry);
		if (isSelected)
			setSelected(entry);
	}
    
    @Override protected void extractListBackground(GuiGraphicsExtractor guiGraphics) {/* It ugly ;_; */}
    
    @Override
    public int getRowWidth() {
    	return 290;
    }

	public static class HudEntry extends ObjectSelectionList.Entry<HudEntry> {
		
		public MutableComponent component;
		public String filepath;
		public String[] compilers;
		public static UnaryOperator<Style> COMPILER_TEXT_STYLE = t->t.withItalic(true).withColor(ChatFormatting.GRAY);
		
		public HudEntry(String filepath, String[] compilers) {
			if (filepath!=null) {
				this.component = Component.literal(filepath);
				if (compilers.length>0) {
					component.append(Component.literal(" - ").withStyle(COMPILER_TEXT_STYLE));
					for (int i = 0;i<compilers.length;i++) {
						if (i > 0) {
							String separator = i == compilers.length - 1 ? " and " : ", ";
							component.append(Component.literal(separator).withStyle(COMPILER_TEXT_STYLE));
						}
						component.append(Component.literal(
								Compilers.getDisplayNameFromCompilerName(compilers[i])).withStyle(COMPILER_TEXT_STYLE));
					}
				} else {
					component.append(Component.translatable("hudder.mainfile.noknowncompilers").withStyle(COMPILER_TEXT_STYLE));
				}
			}
			this.filepath = filepath;
			this.compilers = compilers;
		}

		@Override
		public Component getNarration() {
			return component;
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
			graphics.text(Minecraft.getInstance().font, component, getX()+4, getY()+4, 0xFFFFFFFF);
		}

		public boolean canSelect() {
			return true;
		}
	}
	
	public static class TitleEntry extends HudEntry {

		public Component subtitle;
		float scale = 1.2f;

		public TitleEntry(Component title, Component subtitle) {
			super(null, null);
			this.component = title.plainCopy().withStyle(t -> t.withBold(true));
			this.subtitle = subtitle.plainCopy().withStyle(t -> t.withItalic(true));
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered,
				float delta) {
			var font = Minecraft.getInstance().font;
			
			float centerX = getX() + getWidth() / 2.0f;
			float scaledLineHeight = font.lineHeight * scale;
			float totalHeight = scaledLineHeight * 2.0f;
			
			float startY = getY() + (getHeight() - totalHeight) / 2.0f;
			
			drawCenteredScaled(graphics, component, centerX, startY, 0xFFFFFFFF, scale);
			
			drawCenteredScaled(graphics, subtitle, centerX, startY + scaledLineHeight, 0xFFC3C3C3, scale-.3f);
		}
		
		public void drawCenteredScaled(GuiGraphicsExtractor graphics, Component text, float centerX, float y,
				int color, float scale) {
			var font = Minecraft.getInstance().font;
			
			float scaledWidth = font.width(text) * scale;
			float x = centerX - scaledWidth / 2.0f;
			
			var pose = graphics.pose();
			pose.pushMatrix();
			
			try {
				// Translation is in normal screen coordinates.
				pose.translate(x, y);
				pose.scale(scale, scale);
				
				// Coordinates are relative to the translated origin.
				graphics.text(font, text, 0, 0, color);
			} finally {
				pose.popMatrix();
			}
		}
		
		@Override
		public boolean canSelect() {
			return false;
		}
	}
	
	@Override
	public void setSelected(HudEntry entry) {
	    if (entry == null || entry.canSelect()) {
	        super.setSelected(entry);
	    }
	}

	public void save() {
		var selected = getSelected();
		if (selected==null)
			return;
		config.mainfile = selected.filepath;
	}
	
	public void reset() {
		setSelected(children().stream()
				.filter(entry->entry.filepath.equals("hud.hud"))
				.findFirst()
				.orElse(children().get(0)));
	}
	
	public Component error() {
		var selected = getSelected();
		if (selected==null)
			return Component.translatable("hudder.mainfile.noselection");
		try {
			if (!HudFileUtils.exists(selected.filepath))
				return Component.translatable("hudder.mainfile.doesntexist", selected.filepath);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Component warning() {
		var selected = getSelected();
		String file = selected!=null?selected.filepath:"";
		return Compilers.getCompilerFromDisplayname(comp).isValidFilePath(file)?null:
			Component.translatable("hudder.mainfile.unsupportedformat",comp,file);
	}
}
