package dev.ngspace.hudder.api.functionsandconsumers;

import java.io.IOException;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesManager;
import dev.ngspace.hudder.uielements.minecraft.GameHudElement;
import dev.ngspace.hudder.uielements.minecraft.ItemElement;
import dev.ngspace.hudder.uielements.minecraft.GameHudElement.GuiType;
import dev.ngspace.hudder.uielements.primitives.ColorVerticesElement;
import dev.ngspace.hudder.uielements.primitives.GradientElement;
import dev.ngspace.hudder.uielements.primitives.LineElement;
import dev.ngspace.hudder.uielements.primitives.RectangleElement;
import dev.ngspace.hudder.uielements.primitives.TextElement;
import dev.ngspace.hudder.uielements.textures.BuiltInTextureElement;
import dev.ngspace.hudder.uielements.textures.Texture9SliceElement;
import dev.ngspace.hudder.uielements.textures.TextureElement;
import dev.ngspace.hudder.uielements.textures.TextureVerticesElement;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;

public class HudderBuiltInMethods {
	private HudderBuiltInMethods() {}
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	public static void registerMethods(FunctionAndConsumerAPI api) {
		// Vertex
		
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e
						.addUIElement(new ColorVerticesElement(s[0].asFloatArray(), (int) s[1].asLong(), false)),
				"colorvertices");
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e
						.addUIElement(new ColorVerticesElement(s[0].asFloatArray(), (int) s[1].asLong(), true)),
				"colorvertices_con");
		
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(
						new TextureVerticesElement(s[0].asString(), s[1].asFloatArray(), s[2].asFloatArray(), false)),
				"texturevertices");
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(
						new TextureVerticesElement(s[0].asString(), s[1].asFloatArray(), s[2].asFloatArray(), true)),
				"texturevertices_con");
		
		// Textures
		
		api.registerPositionedConsumer((e,_,_,_,s) -> e.addUIElement(new BuiltInTextureElement(s[0].asIdentifier(),
				s[1].asIdentifier(), s[2].asInt(), s[3].asInt(), s[4].asInt(), s[5].asInt())), "drawTexture",
				"texture");
		
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(
						new TextureElement(s[0].asString(), s[1].asFloat(), s[2].asFloat(), s[3].asFloat(), s[4].asFloat())),
				"drawLocalTexture", "drawPNG", "drawImage", "image", "png");
		
		api.registerPositionedConsumer((e,_,_,_,s) -> e.addUIElement(new Texture9SliceElement(s[0].asString(),
				s[1].asInt(), s[2].asInt(), s[3].asInt(), s[4].asInt(), s[5].asFloatArray())), "9slicetexture",
				"nineslicetexture");
		
		// Rectangles
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(
						new RectangleElement(s[0].asFloat(), s[1].asFloat(), s[2].asFloat(), s[3].asFloat(), s[4].asInt())),
				"rectangle");

		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(
						new GradientElement(s[0].asFloat(), s[1].asFloat(), s[2].asFloat(), s[3].asFloat(),
								s[4].asInt(), s[5].asInt(), false)),
				"gradient");

		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(
						new GradientElement(s[0].asFloat(), s[1].asFloat(), s[2].asFloat(), s[3].asFloat(),
								s[4].asInt(), s[5].asInt(), true)),
				"horizontal_gradient");
		
		// Other Primitive Shapes
		
		api.registerPositionedConsumer((e,_,_,_,s)->
			e.addUIElement(new LineElement(s[0].asFloat(), s[1].asFloat(), s[2].asFloat(),
							s[3].asFloat(), s[4].asInt(), s[5].asInt())), "line");
		
		// Text
		
		api.registerPositionedConsumer((e,_,_,i,args) -> {
			int x = args[0].asInt();
			int y = args[1].asInt();
			
			Component text = args[2].asComponent();
			float scale = (float) (args.length > 3 ? args[3].asDouble() : i.scale());
			
			int color = args.length > 4 ? (int) args[4].asLong() : i.color();
			boolean shadow = args.length > 5 ? args[5].asBoolean() : i.shadow();
			boolean bg = args.length > 6 ? args[6].asBoolean() : i.background();
			int bgcolor = args.length > 7 ? (int) args[7].asLong() : i.backgroundcolor();
			float rotation = args.length > 8 ? (int) args[8].asFloat() : 0;
			
			e.addUIElement(new TextElement(x, y, text, scale, color, shadow, bg, bgcolor, rotation));
		}, "drawText", "text");
		
		// GUI
		
		api.registerPositionedConsumer(
				(e, _, _,_, s) -> e.addUIElement(new GameHudElement(s[0].asFloat(), s[1].asFloat(), GuiType.STATUS_BARS)),
				"drawStatusBars", "statusbars");
		api.registerPositionedConsumer(
				(e, _, _,_, s) -> e
						.addUIElement(new GameHudElement(s[0].asFloat(), s[1].asFloat(), GuiType.EXP_AND_MOUNT_BAR)),
				"drawExpAndMountBars", "xpbar");
		api.registerPositionedConsumer(
				(e, _, _,_, s) -> e.addUIElement(new GameHudElement(s[0].asFloat(), s[1].asFloat(), GuiType.HOTBAR)),
				"drawHotbar", "hotbar");
		api.registerPositionedConsumer(
				(e, _, _,_, s) -> e.addUIElement(new GameHudElement(s[0].asFloat(), s[1].asFloat(), GuiType.ITEM_TOOLTIP)),
				"drawItemTooltip", "helditemtooltip");
		
		// Variables
		
		api.registerDeprecatedPositionedConsumer("Setting variables through a function is deprecated", (_,a,_,_, s) -> {
			if (a instanceof VariablesManager c)
				c.putVariable(s[0].asString(), s[1]);
		}, "set", "setVal", "setVariable");
		api.registerPositionedConsumer((_,_,_,i,s) -> {
			try {
				i.putSavedVariable(s[0].asString(), s[1].get());
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new IllegalArgumentException(ex);
			}
		}, "writeValue");
		
		// Items
		
		api.registerPositionedConsumer((e,_,_,_,s) -> e.addUIElement(new ItemElement(s[0].asFloat(), s[1].asFloat(),
				(mc.player.getVehicle() instanceof AbstractHorse horse) ? horse.getBodyArmorItem() : ItemStack.EMPTY,
				s[2].asFloat(), false)), "drawMountArmor", "mountarmor");
		
		api.registerPositionedConsumer(
				(e,_,_,c,s) -> e.addUIElement(new ItemElement(s[1].asFloat(), s[2].asFloat(),
						new ItemStack(BuiltInRegistries.ITEM.getValue(s[0].asIdentifier())), s.length > 3 ? s[3].asFloat() : c.scale(), false)),
				"drawItem", "item");
		
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(new ItemElement(s[0].asFloat(), s[1].asFloat(),
								mc.player.getInventory().getItem(mc.player.getInventory().getSelectedSlot()),
								s.length > 2 ? s[2].asFloat() : 1, s.length <= 3 || s[3].asBoolean())),
				"hand", "selectedslot");
		
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e
						.addUIElement(new ItemElement(s[0].asFloat(), s[1].asFloat(), mc.player.getInventory().getItem(39),
								s.length > 2 ? s[2].asFloat() : 1, s.length <= 3 || s[3].asBoolean())),
				"hat", "helmet");
		
		api.registerPositionedConsumer((e,_,_,_,s) -> e
				.addUIElement(new ItemElement(s[0].asFloat(), s[1].asFloat(), mc.player.getInventory().getItem(38),
						s.length > 2 ? s[2].asFloat() : 1, s.length <= 3 || s[3].asBoolean())),
				"chestplate");
		
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e
						.addUIElement(new ItemElement(s[0].asFloat(), s[1].asFloat(), mc.player.getInventory().getItem(37),
								s.length > 2 ? s[2].asFloat() : 1, s.length <= 3 || s[3].asBoolean())),
				"leggings", "pants");
		
		api.registerPositionedConsumer((e,_,_,_,s) -> e
				.addUIElement(new ItemElement(s[0].asFloat(), s[1].asFloat(), mc.player.getInventory().getItem(36),
						s.length > 2 ? s[2].asFloat() : 1, s.length <= 3 || s[3].asBoolean())),
				"boots");
		
		api.registerPositionedConsumer((e,_,_,_,s) -> e.addUIElement(new ItemElement(s[0].asFloat(), s[1].asFloat(),
				mc.player.getOffhandItem(), s.length > 2 ? s[2].asFloat() : 1, s.length <= 3 || s[3].asBoolean())),
				"offhand");
		
		// Slot
		
		api.registerPositionedConsumer(
				(e,_,_,c,s) -> e.addUIElement(new ItemElement(s[1].asFloat(), s[2].asFloat(),
								mc.player.getInventory().getItem(s[0].asInt()),
								s.length > 3 ? s[3].asFloat() : c.scale(), s.length <= 4 || s[4].asBoolean())),
				"drawSlot", "slot");
		
		// Armor
		api.registerPositionedConsumer(
				(e,_,_,_,s) -> e.addUIElement(new ItemElement(s[1].asFloat(), s[2].asFloat(),
								mc.player.getInventory().getItem(36 + s[0].asInt()), s[3].asFloat(), s[4].asBoolean())),
				"drawArmor", "armor");
		
		// Logging
		
		api.registerPositionedConsumer((_,_,_,_,s) -> Hudder.alert(String.valueOf(s[0].get())), "alert");
		api.registerPositionedConsumer((_,_,_,_,s) -> Hudder.log(String.valueOf(s[0].get())), "log");
		api.registerPositionedConsumer((_,_,_,_,s) -> Hudder.warn(String.valueOf(s[0].get())), "warn");
		api.registerPositionedConsumer((_,_,_,_,s) -> Hudder.error(String.valueOf(s[0].get())), "error");
	}
}
