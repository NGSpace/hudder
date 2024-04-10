package io.github.ngspace.hudder.data_management;

import static io.github.ngspace.hudder.data_management.Advanced.isKeyHeld;

import java.util.Optional;

import com.mojang.datafixers.DataFixUtils;

import io.github.ngspace.hudder.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

public class BooleanData {private BooleanData(){}
	public static Boolean getBoolean(String key) {
		MinecraftClient ins = MinecraftClient.getInstance();
		PlayerEntity p = ins.player;
		World betterworld = DataFixUtils.orElse(Optional.ofNullable(ins.getServer()).flatMap(integratedServer ->
			Optional.ofNullable(integratedServer.getWorld(ins.world.getRegistryKey()))), ins.world);
		return switch (key) {
			case "isslime", "is_slime": yield ChunkRandom.getSlimeRandom(p.getBlockX() >> 4, p.getBlockZ() >> 4,
					((StructureWorldAccess)betterworld).getSeed(), 987234911L).nextInt(10) == 0;
			case "hudhidden": yield ins.options.hudHidden;
			case "showdebug": yield ins.getDebugHud().shouldShowDebugHud();
			
			
			/* Hudder */
			case "enabled": yield true; //Duh
			case "shadow": yield ConfigManager.getConfig().shadow;
			case "showinf3": yield ConfigManager.getConfig().showInF3;
			case "javascriptenabled": yield ConfigManager.getConfig().javascript;
			case "globalvariablesenabled": yield ConfigManager.getConfig().globalVariablesEnabled;
			default: int keyheld = isKeyHeld(key); yield keyheld==0 ? null : keyheld==2;
		};
	}
}