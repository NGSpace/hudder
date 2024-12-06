package io.github.ngspace.hudder.data_management;

import static io.github.ngspace.hudder.data_management.Advanced.isKeyHeld;

import java.util.Optional;

import com.mojang.datafixers.DataFixUtils;

import io.github.ngspace.hudder.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class BooleanData {private BooleanData(){}
	public static Boolean getBoolean(String key) {
		Minecraft ins = Minecraft.getInstance();
		var p = ins.player;
		@SuppressWarnings("resource")
		var betterworld = DataFixUtils.orElse(Optional.ofNullable(ins.getSingleplayerServer()).flatMap(integratedServer ->
			Optional.ofNullable(integratedServer.getLevel(ins.level.dimension()))), ins.level);
		return switch (key) {
			case "isslime", "is_slime": {
				try {
					yield (WorldgenRandom.seedSlimeChunk(p.getBlockX() >> 4, p.getBlockZ() >> 4,
						((WorldGenLevel)betterworld).getSeed(), 987234911L).nextInt(10) == 0);
				} catch (Exception e) {/* For some reason adding a yield false; here causes runtime errors...*/}
				yield false;
			}
			
			case "hudhidden": yield ins.options.hideGui;
			case "showdebug": yield ins.getDebugOverlay().showDebugScreen();
			
			
			
			/* Player gamemode */
			case "issurvival","is_survival": yield ins.gameMode.getPlayerMode()==GameType.SURVIVAL;
			case "iscreative","is_creative": yield ins.gameMode.getPlayerMode()==GameType.CREATIVE;
			case "isadventure","is_adventure": yield ins.gameMode.getPlayerMode()==GameType.ADVENTURE;
			case "isspectator","is_spectator": yield ins.gameMode.getPlayerMode()==GameType.SPECTATOR;
			
			
			
			/* Player movement */
			case "isgliding": yield p.isFallFlying();
			case "isclimbing": yield p.onClimbable();
			case "iscrawling": yield p.isVisuallyCrawling();
			case "isswimming": yield p.isSwimming();
			case "issneaking": yield p.isShiftKeyDown();
			
			
			
			/* Player information */
			case "isalive": yield p.isAlive();
			case "isblocking": yield p.isBlocking();
			case "isfreezing": yield p.isFreezing();
			case "isglowing": yield p.isCurrentlyGlowing();
			case "isfireimmune": yield p.fireImmune();
			case "isonfire": yield p.isOnFire();
			case "isonground": yield p.onGround();
			case "isinvisible": yield p.isInvisible();
			
			
			
			/* Mouse */
			case "mouse_left": yield ins.mouseHandler.isLeftPressed();
			case "mouse_middle": yield ins.mouseHandler.isMiddlePressed();
			case "mouse_right": yield ins.mouseHandler.isRightPressed();
			
			
			
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