package dev.ngspace.hudder.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.utils.CompilerEntry;
import dev.ngspace.hudder.config.HudSelectionList.HudEntry;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.ResourceReloadListener;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class HudSelectionList extends ObjectSelectionList<HudEntry> implements ResourceReloadListener {
	
	private final HudderUserSettings config;
	private final CompilerRegistry registry;
	public final Path source;
	public String comp;
	
	// There are more formats but those are the only ones that matter, as far as I
	// am aware minecraft only supports those formats
	private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg");
	
	public HudSelectionList(Minecraft minecraft, Path source, HudderUserSettings config, CompilerRegistry registry) {
		super(minecraft, 0, 0, 0, 18);
		this.config = config;
		this.source = source;
		this.registry = registry;
		var entry = registry.findEntryFromId(config.compilername);
		if (entry.isPresent()) {
			comp = entry.get().display_name();
		} else {
			comp = "Auto-detect";
		}
		
		try {
			loadHuds(source);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		HudFileUtils.addReloadResourcesListener(this);
	}
	
	private void loadHuds(Path folder) throws IOException {
		
		addEntry(new TitleEntry(Component.translatable("hudder.mainfile.title"),
				Component.translatable("hudder.mainfile.subtitle")), 24);
		
		for (Path hud : Files.newDirectoryStream(folder)) {
			if (Files.isDirectory(hud) && isEmptyOrImagesOnly(hud)) {
				continue;
			}
			String name = hud.getFileName().toString();
			addEntry(name, hud, registry.getValidCompilersForFilePath(hud), name.equals(config.mainfile));
		}
	}

	private static boolean isEmptyOrImagesOnly(Path directory) {
		try (Stream<Path> contents = Files.list(directory)) {
		    return contents.allMatch(path -> {
		        if (Files.isDirectory(path)) {
		            return isEmptyOrImagesOnly(path);
		        } else {
		            return isImage(path);
		        }
		    });
		} catch (IOException _) {
		    return false;
		}
	}
	
	private static boolean isImage(Path file) {
		String name = file.getFileName().toString();
		int dot = name.lastIndexOf('.');
		
		if (dot < 0 || dot == name.length() - 1) {
			return false;
		}
		
		String extension = name.substring(dot + 1).toLowerCase();
		return IMAGE_EXTENSIONS.contains(extension);
	}
	
	public void addEntry(String filepath, Path file, CompilerEntry[] compilers, boolean isSelected) {
		HudEntry entry = new HudEntry(filepath, compilers, file);
		addEntry(entry);
		if (isSelected)
			setSelected(entry);
	}
	
	@Override
	protected void scrollToEntry(HudEntry entry) {
		/* I don like how it scrolls for me, disgusting, vile even... */}
		
	@Override
	protected void extractListBackground(GuiGraphicsExtractor guiGraphics) {
		/* It ugly ;_; */}
		
	@Override
	public int getRowWidth() {
		return 290;
	}
	
	public class HudEntry extends ObjectSelectionList.Entry<HudEntry> {
		
		public MutableComponent component;
		public String filepath;
		public CompilerEntry[] compilers;
		public Path file;
		public NGSMCConfigButton editbutton;
		
		public static UnaryOperator<Style> COMPILER_TEXT_STYLE = t -> t.withItalic(true).withColor(ChatFormatting.GRAY);
		
		protected HudEntry() {}
		
		public HudEntry(String filepath, CompilerEntry[] compilers, Path file) {
			this.file = file;
			if (filepath != null) {
				this.component = Component.literal(filepath);
				if (compilers.length > 0) {
					component.append(Component.literal(" - ").withStyle(COMPILER_TEXT_STYLE));
					for (int i = 0; i < compilers.length; i++) {
						if (i > 0) {
							String separator = i == compilers.length - 1 ? " and " : ", ";
							component.append(Component.literal(separator).withStyle(COMPILER_TEXT_STYLE));
						}
						component.append(Component.literal(compilers[i].display_name())
								.withStyle(COMPILER_TEXT_STYLE));
					}
				} else {
					component.append(
							Component.translatable("hudder.mainfile.noknowncompilers").withStyle(COMPILER_TEXT_STYLE));
				}
			}
			Component edit = Component.translatable("hudder.mainfile.editbutton");
			
			this.editbutton = new NGSMCConfigButton(0, 0, Minecraft.getInstance().font.width(edit) + 20, 14, edit,
					_ -> {
					}, 0xFFFFFFFF, false);
			editbutton.setIcon(new NGSMCConfigIcon.SpriteIcon("items", "item/writable_book"));
			editbutton.setOutlineColor(0xFFFFFFFF);
			this.filepath = filepath;
			this.compilers = compilers;
			
		}
		
		@Override
		public Component getNarration() {
			return component;
		}
		
		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
			graphics.text(Minecraft.getInstance().font, component, getX() + 4, getY() + 4, 0xFFFFFFFF);
			if (editbutton.isMouseOver(mouseX, mouseY))
				graphics.requestCursor(CursorTypes.POINTING_HAND);
			editbutton.setPosition(getContentRight() - editbutton.getWidth(), getContentY());
			editbutton.extractContents(graphics, mouseX, mouseY, a);
		}
		
		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			double mouseX = event.x();
			double mouseY = event.y();
			
			boolean clickedEdit = event.button() == 0 && filepath != null
					&& editbutton.isMouseOver(mouseX, mouseY);
			
			if (clickedEdit) {
				for (CompilerEntry compiler : compilers) {
					if (compiler.id().equals(config.compilername)) {
						registry.findEntryFromId(config.compilername).orElseThrow().compiler().edit(file);
						return true;
					}
				}
				if ("Auto-detect".equals(comp)) {
			        Optional<CompilerEntry> entry = Arrays.stream(registry.getValidCompilersForFilePath(file))
			                .max(Comparator.comparingInt(CompilerEntry::priority));
			        if (entry.isPresent()) {
			        	entry.get().compiler().edit(file);
			        } else {
			        	Hudder.config.hudderV3Compiler.edit(file);
			        }
				} else {
					registry.findEntryFromDisplayName(comp).orElseThrow().compiler().edit(file);
				}
				return true;
			}
			
			return super.mouseClicked(event, doubleClick);
		}
		
		public boolean canSelect() {
			return true;
		}
	}
	
	public class TitleEntry extends HudEntry {
		
		public Component subtitle;
		float scale = 1.2f;
		
		public TitleEntry(Component title, Component subtitle) {
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
			
			drawCenteredScaled(graphics, subtitle, centerX, startY + scaledLineHeight, 0xFFC3C3C3, scale - .3f);
		}
		
		public void drawCenteredScaled(GuiGraphicsExtractor graphics, Component text, float centerX, float y, int color,
				float scale) {
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
		if (selected == null)
			return;
		config.mainfile = selected.filepath;
		Hudder.config.refreshCompiler();
	}
	
	public void reset() {
		setSelected(children().stream().filter(entry -> "hud.hud".equals(entry.filepath)).findFirst()
				.orElse(children().get(0)));
	}
	
	public Component error() {
		var selected = getSelected();
		if (selected == null)
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
		if (selected==null)
			return Component.translatable("hudder.mainfile.noselection");
		if ("Auto-detect".equals(comp))
			return null;
		return registry.findEntryFromDisplayName(comp).orElseThrow().compiler().isValidFilePath(selected.file) ? null
				: Component.translatable("hudder.mainfile.unsupportedformat", comp, selected.filepath);
	}

	@Override
	public void run() throws IOException {
		clearEntries();
		loadHuds(source);
	}
}
