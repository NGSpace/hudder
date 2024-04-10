package io.github.ngspace.hudder.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class HudderModMenuImpl implements ModMenuApi {
	@Override public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return FabricLoader.getInstance().isModLoaded("cloth-config2") ? new ConfigMenu() : InstallClothConfig::new;
    }
	class InstallClothConfig extends Screen {
	    private final Screen parent;
	    public InstallClothConfig(Screen parent) {
	        super(NarratorManager.EMPTY);
	        this.parent = parent;
	    }
	    @Override protected void init() {
	        addDrawableChild(ButtonWidget.builder(ScreenTexts.OK, buttonWidget -> client.setScreen(parent))
	                .position(width/2-100, height-52).size(200, 20).build());
	    }
	    @Override public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
	    	Text INSTALLCLOTH = Text.literal("You need to install Cloth Config 13.0.121 or above to open this menu.");
	        super.render(drawContext, mouseX, mouseY, delta);
	        drawContext.drawTextWithShadow(client.textRenderer, INSTALLCLOTH,
	        		(width-client.textRenderer.getWidth(INSTALLCLOTH))/2, height/3, 0xAA0000);
	        super.render(drawContext, mouseX, mouseY, delta);
	    }
	}
}