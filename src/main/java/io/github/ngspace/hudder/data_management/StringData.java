package io.github.ngspace.hudder.data_management;

import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

import com.mojang.blaze3d.platform.GLX;

import io.github.ngspace.hudder.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class StringData {private StringData() {}
	public static String getString(String key) {
		Minecraft ins = Minecraft.getInstance();
		LocalPlayer p = ins.player;
		return switch (key) {
			
			case "damagetype": yield p.getLastDamageSource() == null ? "" : p.getLastDamageSource().type().toString();
			
			/* Computer info */
			case "cpu_info": yield GLX._getCpuInfo();
			case "operating_system": yield Advanced.OS;
			case "month_name": yield Calendar.getInstance().getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault());
			case "locale": yield Locale.getDefault().getDisplayName();
			case "language": yield Locale.getDefault().getLanguage();
			case "country": yield Locale.getDefault().getCountry();
			
			
			
			/* Inventory */
			case "helditem_name": yield ins.player.getInventory()
				.getItem(ins.player.getInventory().selected).getDisplayName().getString();
			
			
			
			/* World */
			case "biome":
				Optional<ResourceKey<Biome>> i = ins.level.getBiome(ins.player.blockPosition()).unwrapKey();
				if (i.isPresent()) yield i.get().registry().toString();
				yield null;
			case "dimension": yield ins.level.dimension().toString();
			
			
			
			/* Looking at */
			case "block_in_front": {
			    HitResult vec = ins.player.pick(50,0,true);
			    if (vec.getType()==Type.BLOCK) {
			    	BlockHitResult res = (BlockHitResult) vec;
			    	BlockState state = ins.level.getBlockState(res.getBlockPos());
			    	yield BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
			    }
			    yield "";
			}
			case "looking_at_pos": {
				HitResult vec = ins.player.pick(50,0,true);
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
