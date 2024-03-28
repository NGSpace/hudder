package io.github.ngspace.hudder.data_management;

import static net.minecraft.client.MinecraftClient.getInstance;

import com.mojang.blaze3d.platform.GlDebugInfo;

import io.github.ngspace.hudder.config.ConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class StringData {private StringData() {}
	public static String getString(String key) {
		MinecraftClient ins = getInstance();
		return switch (key) {
			case "cpu_info": yield GlDebugInfo.getCpuInfo();
			case "biome":
				var i = ins.world.getBiome(ins.player.getBlockPos()).getKey();
				if (i.isPresent())
					yield i.get().getValue().toString();
				yield null;
			case "looking_at","block_in_front": {
			    var vec = ins.player.raycast(PlayerEntity.getReachDistance(ins.player.isCreative()),0,true);
			    if (vec.getType()==Type.BLOCK) {
			    	BlockHitResult res = (BlockHitResult) vec;
			    	BlockState state = ins.player.getWorld().getBlockState(res.getBlockPos());
			    	yield Registries.BLOCK.getId(state.getBlock()).toString();
			    }
			    yield "";
			}
			case "looking_at_pos": {
			    var vec = ins.player.raycast(PlayerEntity.getReachDistance(ins.player.isCreative()),0,true);
			    if (vec.getType()==Type.BLOCK) {
			    	BlockPos res = ((BlockHitResult) vec).getBlockPos();
			    	yield "" + res.getX() + ' ' + res.getY() + ' ' + res.getZ();
			    }
			    yield "";
			}
			
			/* Hudder */
			case "compilertype": yield ConfigManager.getConfig().compilertype;
			case "mainfile": yield ConfigManager.getConfig().mainfile;
			default: yield null;
		};
	}
}
