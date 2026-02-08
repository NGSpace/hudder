package io.github.ngspace.hudder.api.functionsandconsumers;

import java.io.IOException;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI;
import io.github.ngspace.hudder.uielements.BuiltInTextureElement;
import io.github.ngspace.hudder.uielements.ColorVerticesElement;
import io.github.ngspace.hudder.uielements.GameHudElement;
import io.github.ngspace.hudder.uielements.GameHudElement.GuiType;
import io.github.ngspace.hudder.uielements.ItemElement;
import io.github.ngspace.hudder.uielements.TextElement;
import io.github.ngspace.hudder.uielements.Texture9SliceElement;
import io.github.ngspace.hudder.uielements.TextureElement;
import io.github.ngspace.hudder.uielements.TextureVerticesElement;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;

public class HudderBuiltInMethods {private HudderBuiltInMethods() {}
	protected static Minecraft mc = Minecraft.getInstance();
	public static void registerMethods(FunctionAndConsumerAPI api) {
		//Vertex

		api.registerConsumer((e,a,s)->e.addUIElement(new ColorVerticesElement(s[0].asFloatArray(),s[1].asInt(),false)),"colorvertices");
		api.registerConsumer((e,a,s)->e.addUIElement(new ColorVerticesElement(s[0].asFloatArray(),s[1].asInt(),true)),"colorvertices_con");
		
		api.registerConsumer((e,a,s)->e.addUIElement(new TextureVerticesElement(
				s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray(), false)), "texturevertices");
		api.registerConsumer((e,a,s)->e.addUIElement(new TextureVerticesElement(
				s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray(), true )), "texturevertices_con");
		
		
		//Textures
		
		api.registerConsumer((e,a,s)->e.addUIElement(new BuiltInTextureElement(Identifier.withDefaultNamespace(
				s[0].asString().trim()), s[1].asInt(), s[2].asInt(), s[3].asInt(),s[4].asInt())), "drawTexture", "texture");
		
		api.registerConsumer((e,a,s)-> e.addUIElement(new TextureElement(s[0].asString(),
				s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt())),
				"drawLocalTexture","drawPNG","drawImage","image","png");
		
		api.registerConsumer((e,a,s)-> e.addUIElement(new Texture9SliceElement(s[0].asString(),
				s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt(),s[5].asFloatArray())), "9slicetexture");
		
		//Text
		
		api.registerConsumer((e,a,args) -> {
			int x = args[0].asInt();
			int y = args[1].asInt();

			String text = args[2].asString();
			float scale = (float) (args.length>3 ? args[3].asDouble() : Hudder.config.scale);

			int color = args.length>4 ? (int) args[4].asLong() : Hudder.config.color;
			boolean shadow = args.length>5 ? args[5].asBoolean(): Hudder.config.shadow;
			boolean bg = args.length>6 ? args[6].asBoolean(): Hudder.config.background;
			int bgcolor = args.length>7 ? args[7].asInt() : Hudder.config.backgroundcolor;
			
			e.addUIElement(new TextElement(x, y, text, scale, color, shadow, bg, bgcolor));
		}, "drawText", "text");
		
		//GUI
		
		api.registerConsumer((e,a,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.STATUS_BARS)),
				"drawStatusBars", "statusbars");
		api.registerConsumer((e,a,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.EXP_AND_MOUNT_BAR)),
				"drawExpAndMountBars", "xpbar");
		api.registerConsumer((e,a,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.HOTBAR)),
				"drawHotbar", "hotbar");
		api.registerConsumer((e,a,s)->e.addUIElement(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.ITEM_TOOLTIP)),
				"drawItemTooltip", "helditemtooltip");
		
		//Variables
		
		api.registerConsumer((e,a,s)->a.put(s[0].asString(), s[1]), "set", "setVal", "setVariable");
		api.registerConsumer((e,a,s)->{
			try {
				a.getConfig().putSavedVariable(s[0].asString(),s[1].get());
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new IllegalArgumentException(ex);
			}
		},"writeValue");

		//Items

		api.registerConsumer((e,a,s)->e.addUIElement(new ItemElement(s[0].asInt(),s[1].asInt(),
				(mc.player.getVehicle() instanceof AbstractHorse horse) ? horse.getBodyArmorItem() : ItemStack.EMPTY, s[2].asFloat(), false)),
				"drawMountArmor", "mountarmor");

		//Logging
		
		api.registerConsumer((e,a,s)->Hudder.alert(String.valueOf(s[0].get())), "alert");
		api.registerConsumer((e,a,s)->Hudder.log  (String.valueOf(s[0].get())), "log"  );
		api.registerConsumer((e,a,s)->Hudder.warn (String.valueOf(s[0].get())), "warn" );
		api.registerConsumer((e,a,s)->Hudder.error(String.valueOf(s[0].get())), "error");
	}
}
