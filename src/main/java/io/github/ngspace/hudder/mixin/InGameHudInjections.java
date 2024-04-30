package io.github.ngspace.hudder.mixin;

import static io.github.ngspace.hudder.Hudder.config;
import static io.github.ngspace.hudder.Hudder.ins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.JumpingMount;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudInjections {
	
	public boolean shouldDraw() {return (!config.removegui)||(!config.shouldCompile(ins));}
	
	//Mount Jump Bar
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target=
    		"Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/gui/DrawContext;I)V"))
    public void disableMountJumpBar(InGameHud hud, JumpingMount mount, DrawContext context, int x) {
    	if (shouldDraw()) hud.renderMountJumpBar(mount, context, x);
    }
	
	//EXP
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target=
    		"Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V"))
    public void disableExperienceBar(InGameHud hud, DrawContext context, int x) {
    	if (shouldDraw()) hud.renderExperienceBar(context, x);
    }
	
	//Hotbar
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target=
    		"Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"))
    public void disabledHotbar(InGameHud hud, float tickdelta, DrawContext context) {
    	if (shouldDraw()) ((InGameHudAccessor)hud).callRenderHotbar(tickdelta, context);
    }
	
	//Status Bars
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target=
    		"Lnet/minecraft/client/gui/hud/InGameHud;renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void disableStatusBars(InGameHud hud, DrawContext context) {
    	if (shouldDraw()) ((InGameHudAccessor)hud).callRenderStatusBars(context);
    }
	
	//Mount Health
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target=
    		"Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void disableMountHealth(InGameHud hud, DrawContext context) {
    	if (shouldDraw()) ((InGameHudAccessor)hud).callRenderMountHealth(context);
    }

	//Mount Health
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target=
    		"Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void renderHeldItemTooltip(InGameHud hud, DrawContext context) {
    	if (shouldDraw()) hud.renderHeldItemTooltip(context);
    }
}
