package io.github.ngspace.hudder.data_management;

import static io.github.ngspace.hudder.data_management.Advanced.isKeyHeld;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.main.config.HudderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class BooleanData {private BooleanData(){}
	public static Boolean getBoolean(String key) {
		Minecraft ins = Minecraft.getInstance();
		HudderConfig config = Hudder.config;
		LocalPlayer p = ins.player;
		return switch (key) {
			case "isslime", "is_slime": {
				try {
					yield WorldgenRandom.seedSlimeChunk(p.getBlockX() >> 4, p.getBlockZ() >> 4, ins.getSingleplayerServer()
							.getLevel(ins.level.dimension()).getSeed(), 987234911L).nextInt(10) == 0;
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
			case "shadow": yield config.shadow;
			case "showinf3": yield config.showInF3;
			case "javascriptenabled": yield config.javascript;
			case "globalvariablesenabled": yield config.globalVariablesEnabled;
			case "background": yield config.background;
			case "removegui": yield config.removegui;
			case "limitrate": yield config.limitrate;
			default: int keyheld = isKeyHeld(key); yield keyheld==0 ? null : keyheld==2;
		};
	}
}