package io.github.ngspace.hudder.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@SuppressWarnings("resource")
public class HudderModMenuImpl implements ModMenuApi {
	@Override public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return FabricLoader.getInstance().isModLoaded("cloth-config2") ? new ConfigMenu() : InstallClothConfig::new;
    }
	
	class InstallClothConfig extends Screen {
	    private final Screen parent;
	    public InstallClothConfig(Screen parent) {
	        super(GameNarrator.NO_TITLE);
	        this.parent = parent;
	    }
	    @Override protected void init() {
	        addRenderableWidget(Button.builder(Component.keybind("OK"), buttonWidget -> Minecraft.getInstance().setScreen(parent))
	                .pos(width/2-100, height-52).size(200, 20).build());
	    }
		@Override public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
	    	Component INSTALLCLOTH = Component.translatable("hudder.noclothapi");
	        super.render(drawContext, mouseX, mouseY, delta);
	        drawContext.drawString(Minecraft.getInstance().font, INSTALLCLOTH,
	        		(width-Minecraft.getInstance().font.width(INSTALLCLOTH))/2, height/3, 0xAA0000);
	        super.render(drawContext, mouseX, mouseY, delta);
	    }
	}
}