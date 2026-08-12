package dev.ngspace.hudder.api.functionsandconsumers;

import java.io.IOException;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.uielements.BuiltInTextureElement;
import dev.ngspace.hudder.uielements.ColorVerticesElement;
import dev.ngspace.hudder.uielements.GameHudElement;
import dev.ngspace.hudder.uielements.GameHudElement.GuiType;
import dev.ngspace.hudder.uielements.ItemElement;
import dev.ngspace.hudder.uielements.TextElement;
import dev.ngspace.hudder.uielements.Texture9SliceElement;
import dev.ngspace.hudder.uielements.TextureElement;
import dev.ngspace.hudder.uielements.TextureVerticesElement;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;

public class HudderBuiltInMethods {private HudderBuiltInMethods() {}
	protected static Minecraft mc = Minecraft.getInstance();
	public static void registerMethods(FunctionAndConsumerAPI api) {
		//Vertex

		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ColorVerticesElement(s[0].asFloatArray(),(int) s[1].asLong(),false)),"colorvertices");
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ColorVerticesElement(s[0].asFloatArray(),(int) s[1].asLong(),true)),"colorvertices_con");
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new TextureVerticesElement(
				s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray(), false)), "texturevertices");
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new TextureVerticesElement(
				s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray(), true )), "texturevertices_con");
		
		
		//Textures
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new BuiltInTextureElement(s[0].asIdentifier(),
				s[1].asIdentifier(), s[2].asInt(), s[3].asInt(), s[4].asInt(),s[5].asInt())), "drawTexture", "texture");
		
		api.registerPositionedConsumer((e,_,_,s)-> e.addUIElement(new TextureElement(s[0].asString(),
				s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt())),
				"drawLocalTexture","drawPNG","drawImage","image","png");
		
		api.registerPositionedConsumer((e,_,_,s)-> e.addUIElement(new Texture9SliceElement(s[0].asString(),
				s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt(),s[5].asFloatArray())), "9slicetexture", "nineslicetexture");
		
		//Text
		
		api.registerPositionedConsumer((e,_,_,args) -> {
			int x = args[0].asInt();
			int y = args[1].asInt();

			Component text = args[2].asComponent();
			float scale = (float) (args.length>3 ? args[3].asDouble() : Hudder.config.scale());

			int color = args.length>4 ? (int) args[4].asLong() : Hudder.config.color();
			boolean shadow = args.length>5 ? args[5].asBoolean(): Hudder.config.shadow();
			boolean bg = args.length>6 ? args[6].asBoolean(): Hudder.config.background();
			int bgcolor = args.length>7 ? (int) args[7].asLong() : Hudder.config.backgroundcolor();
			
			e.addUIElement(new TextElement(x, y, text, scale, color, shadow, bg, bgcolor));
		}, "drawText", "text");
		
		//GUI
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.STATUS_BARS)),
				"drawStatusBars", "statusbars");
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.EXP_AND_MOUNT_BAR)),
				"drawExpAndMountBars", "xpbar");
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.HOTBAR)),
				"drawHotbar", "hotbar");
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.ITEM_TOOLTIP)),
				"drawItemTooltip", "helditemtooltip");
		
		//Variables
		
		api.registerDeprecatedPositionedConsumer("Setting variables through a function is deprecated",(_,a,_,s)->{
			if (a instanceof AVarTextCompiler c)
				c.put(s[0].asString(), s[1]);
		}, "set", "setVal", "setVariable");
		api.registerPositionedConsumer((_,_,_,s)->{
			try {
				Hudder.config.putSavedVariable(s[0].asString(),s[1].get());
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new IllegalArgumentException(ex);
			}
		},"writeValue");

		//Items

		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),
				(mc.player.getVehicle() instanceof AbstractHorse horse) ? horse.getBodyArmorItem() : ItemStack.EMPTY, s[2].asFloat(), false)),
				"drawMountArmor", "mountarmor");
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[1].asInt(), s[2].asInt(),new ItemStack(
				BuiltInRegistries.ITEM.getValue(s[0].asIdentifier())),s[3].asFloat(), false)),"drawItem", "item");

		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),mc.player.getInventory()
				.getItem(mc.player.getInventory().getSelectedSlot()),s.length>2 ? s[2].asFloat() : 1, s.length<=3 || s[3].asBoolean())),"hand","selectedslot");

		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),mc.player.getInventory()
				.getItem(39),s.length>2 ? s[2].asFloat() : 1, s.length<=3 || s[3].asBoolean())),"hat", "helmet");
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),mc.player.getInventory()
				.getItem(38),s.length>2 ? s[2].asFloat() : 1, s.length<=3 || s[3].asBoolean())),"chestplate");
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),mc.player.getInventory()
				.getItem(37),s.length>2 ? s[2].asFloat() : 1, s.length<=3 || s[3].asBoolean())),"leggings","pants");
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),mc.player.getInventory()
				.getItem(36),s.length>2 ? s[2].asFloat() : 1, s.length<=3 || s[3].asBoolean())),"boots");
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),mc.player.getOffhandItem(),
				s.length>2 ? s[2].asFloat() : 1, s.length<=3 || s[3].asBoolean())),"offhand");
		
		//Slot
		
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getItem(s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawSlot", "slot");
		
		//Armor
		api.registerPositionedConsumer((e,_,_,s)->e.addUIElement(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getItem(36+s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawArmor", "armor");

		//Logging
		
		api.registerPositionedConsumer((_,_,_,s)->Hudder.alert(String.valueOf(s[0].get())), "alert");
		api.registerPositionedConsumer((_,_,_,s)->Hudder.log  (String.valueOf(s[0].get())), "log"  );
		api.registerPositionedConsumer((_,_,_,s)->Hudder.warn (String.valueOf(s[0].get())), "warn" );
		api.registerPositionedConsumer((_,_,_,s)->Hudder.error(String.valueOf(s[0].get())), "error");
	}
}
