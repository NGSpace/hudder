package io.github.ngspace.hudder.data_management;

import java.util.Calendar;
import java.util.Locale;

import org.joml.Vector3f;

import com.mojang.blaze3d.platform.GLX;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class StringData {private StringData() {}
	@SuppressWarnings("deprecation")
	public static Object getString(String key) {
		Minecraft ins = Minecraft.getInstance();
		LocalPlayer p = ins.player;
		Camera c = ins.gameRenderer.getMainCamera();
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
				.getItem(ins.player.getInventory().getSelectedSlot()).getDisplayName().getString();
			
			
			
			/* GUI */
			case "openguitype": yield Advanced.getScreenType(ins.screen);
			case "openguititle": yield ins.screen == null ? V2Runtime.NULL : ins.screen.getTitle().getString();



			/* Mount information */
			case "mount_type": yield p.getVehicle() == null ? "" : p.getVehicle().getType().builtInRegistryHolder().key().location().toString();
			case "mount_armor_type": yield (p.getVehicle() instanceof AbstractHorse horse) ? horse.getBodyArmorItem().getItem().toString() : null;
			case "mount_name": yield p.getVehicle() == null || p.getVehicle().getCustomName() == null ? "" : p.getVehicle().getCustomName().getString();



			/* World */
			case "biome":
				yield ins.level.getBiome(ins.player.blockPosition()).getRegisteredName();
			case "cam_biome":
				yield ins.level.getBiome(c.getBlockPosition()).getRegisteredName();
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
			case "cam_block_in_front": {
				Vec3 camPos = c.getPosition();
				Vector3f lookDirF = c.getLookVector();
				Vec3 lookDir = new Vec3(lookDirF.x(), lookDirF.y(), lookDirF.z());
				double reachDistance = 50.0;
				Vec3 reachPoint = camPos.add(lookDir.x * reachDistance, lookDir.y * reachDistance, lookDir.z * reachDistance);

				HitResult vec = ins.level.clip(new ClipContext(
						camPos,
						reachPoint,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						p
				));

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
			case "cam_looking_at_pos": {
				Vec3 camPos = c.getPosition();
				Vector3f lookDirF = c.getLookVector();
				Vec3 lookDir = new Vec3(lookDirF.x(), lookDirF.y(), lookDirF.z());
				double reachDistance = 50.0;
				Vec3 reachPoint = camPos.add(lookDir.x * reachDistance, lookDir.y * reachDistance, lookDir.z * reachDistance);

				HitResult vec = ins.level.clip(new ClipContext(
						camPos,
						reachPoint,
						ClipContext.Block.OUTLINE,
						ClipContext.Fluid.NONE,
						p
				));

				if (vec.getType()==Type.BLOCK) {
					BlockPos res = ((BlockHitResult) vec).getBlockPos();
					yield "" + res.getX() + ' ' + res.getY() + ' ' + res.getZ();
				}
				yield "";
			}

			
			
			/* Hudder */
			case "compilertype": yield Hudder.config.getCompilerName();
			case "mainfile": yield Hudder.config.mainfile;
			
			case "unset": yield "unset";
			
			default: yield null;
		};
	}
}
