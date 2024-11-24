package io.github.ngspace.hudder.data_management;

import static net.minecraft.client.MinecraftClient.getInstance;

import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

import com.mojang.blaze3d.platform.GlDebugInfo;

import io.github.ngspace.hudder.config.ConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class StringData {private StringData() {}
	public static String getString(String key) {
		MinecraftClient ins = getInstance();
		ClientPlayerEntity p = ins.player;
		return switch (key) {
			
			case "damagetype": yield p.getRecentDamageSource() == null ? "" : p.getRecentDamageSource().getName();
			
			/* Computer info */
			case "cpu_info": yield GlDebugInfo.getCpuInfo();
			case "operating_system": yield Advanced.OS;
			case "month_name": yield Calendar.getInstance().getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault());
			case "locale": yield Locale.getDefault().getDisplayName();
			case "language": yield Locale.getDefault().getLanguage();
			case "country": yield Locale.getDefault().getCountry();
			
			
			
			/* Inventory */
			case "helditem_name": yield ins.player.getInventory()
				.getStack(ins.player.getInventory().selectedSlot).getName().getString();
			
			
			
			/* World */
			case "biome":
				Optional<RegistryKey<Biome>> i = ins.world.getBiome(ins.player.getBlockPos()).getKey();
				if (i.isPresent()) yield i.get().getValue().toString();
				yield null;
			case "dimension": yield ins.world.getDimension().effects().toString();
			
			
			
			/* Looking at */
			case "block_in_front": {
			    HitResult vec = ins.player.raycast(50,0,true);
			    if (vec.getType()==Type.BLOCK) {
			    	BlockHitResult res = (BlockHitResult) vec;
			    	BlockState state = ins.player.getWorld().getBlockState(res.getBlockPos());
			    	yield Registries.BLOCK.getId(state.getBlock()).toString();
			    }
			    yield "";
			}
			case "looking_at_pos": {
				HitResult vec = ins.player.raycast(50,0,true);
			    if (vec.getType()==Type.BLOCK) {
			    	BlockPos res = ((BlockHitResult) vec).getBlockPos();
			    	yield "" + res.getX() + ' ' + res.getY() + ' ' + res.getZ();
			    }
			    yield "";
			}
			
			
			
			/* Hudder */
			case "compilertype": yield ConfigManager.getConfig().compilertype;
			case "mainfile": yield ConfigManager.getConfig().mainfile;
			
			case "unset": yield "unset";
			
			default: yield null;
		};
	}
}
