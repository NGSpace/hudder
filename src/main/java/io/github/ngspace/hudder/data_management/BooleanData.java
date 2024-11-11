package io.github.ngspace.hudder.data_management;

import static io.github.ngspace.hudder.data_management.Advanced.isKeyHeld;

import java.util.Optional;

import com.mojang.datafixers.DataFixUtils;

import io.github.ngspace.hudder.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.GameMode;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

public class BooleanData {private BooleanData(){}
	public static Boolean getBoolean(String key) {
		MinecraftClient ins = MinecraftClient.getInstance();
		ClientPlayerEntity p = ins.player;
		@SuppressWarnings("resource")
		World betterworld = DataFixUtils.orElse(Optional.ofNullable(ins.getServer()).flatMap(integratedServer ->
			Optional.ofNullable(integratedServer.getWorld(ins.world.getRegistryKey()))), ins.world);
		return switch (key) {
			case "isslime", "is_slime": yield ChunkRandom.getSlimeRandom(p.getBlockX() >> 4, p.getBlockZ() >> 4,
					((StructureWorldAccess)betterworld).getSeed(), 987234911L).nextInt(10) == 0;
			case "hudhidden": yield ins.options.hudHidden;
			case "showdebug": yield ins.getDebugHud().shouldShowDebugHud();
			
			/* Player */
			case "issurvival","is_survival": yield ins.interactionManager.getCurrentGameMode()==GameMode.SURVIVAL;
			case "iscreative","is_creative": yield ins.interactionManager.getCurrentGameMode()==GameMode.CREATIVE;
			case "isadventure","is_adventure": yield ins.interactionManager.getCurrentGameMode()==GameMode.ADVENTURE;
			case "isspectator","is_spectator": yield ins.interactionManager.getCurrentGameMode()==GameMode.SPECTATOR;
			
			
			/* Mouse */
			case "mouse_left": yield ins.mouse.wasLeftButtonClicked();
			case "mouse_middle": yield ins.mouse.wasMiddleButtonClicked();
			case "mouse_right": yield ins.mouse.wasRightButtonClicked();
			
			/* Hudder */
			case "enabled": yield true; //Duh
			case "shadow": yield ConfigManager.getConfig().shadow;
			case "showinf3": yield ConfigManager.getConfig().showInF3;
			case "javascriptenabled": yield ConfigManager.getConfig().javascript;
			case "globalvariablesenabled": yield ConfigManager.getConfig().globalVariablesEnabled;
			case "background": yield ConfigManager.getConfig().background;
			case "removegui": yield ConfigManager.getConfig().removegui;
			case "limitrate": yield ConfigManager.getConfig().limitrate;
			default: int keyheld = isKeyHeld(key); yield keyheld==0 ? null : keyheld==2;
		};
	}
}