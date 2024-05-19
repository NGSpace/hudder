package io.github.ngspace.hudder.data_management;

import static net.minecraft.client.MinecraftClient.getInstance;

import java.util.Optional;

import com.mojang.blaze3d.platform.GlDebugInfo;

import io.github.ngspace.hudder.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class StringData {private StringData() {}
	public static String getString(String key) {
		MinecraftClient ins = getInstance();
		return switch (key) {
			/* Computer info */
			case "cpu_info": yield GlDebugInfo.getCpuInfo();
			case "operating_system": yield Advanced.OS;
			
			case "helditem_name": yield ins.player.getInventory()
				.getStack(ins.player.getInventory().selectedSlot).getName().getString();
			
			case "biome":
				Optional<RegistryKey<Biome>> i = ins.world.getBiome(ins.player.getBlockPos()).getKey();
				if (i.isPresent())
					yield i.get().getValue().toString();
				yield null;
//			case "looking_at","block_in_front": {
//			    HitResult vec = ins.player.raycast(PlayerEntity.getReachDistance(ins.player.isCreative()),0,true);
//			    if (vec.getType()==Type.BLOCK) {
//			    	BlockHitResult res = (BlockHitResult) vec;
//			    	BlockState state = ins.player.getWorld().getBlockState(res.getBlockPos());
//			    	yield Registries.BLOCK.getId(state.getBlock()).toString();
//			    }
//			    yield "";
//			}
//			case "looking_at_pos": {
//				HitResult vec = ins.player.raycast(PlayerEntity.getReachDistance(ins.player.isCreative()),0,true);
//			    if (vec.getType()==Type.BLOCK) {
//			    	BlockPos res = ((BlockHitResult) vec).getBlockPos();
//			    	yield "" + res.getX() + ' ' + res.getY() + ' ' + res.getZ();
//			    }
//			    yield "";
//			}
			
			/* Hudder */
			case "compilertype": yield ConfigManager.getConfig().compilertype;
			case "mainfile": yield ConfigManager.getConfig().mainfile;
			default: yield null;
		};
	}
}
