package io.github.ngspace.hudder.data_management;

import java.util.Calendar;
import java.util.Locale;

import net.minecraft.SharedConstants;

import com.mojang.blaze3d.platform.GLX;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class StringData {private StringData() {}
	@SuppressWarnings("deprecation")
	public static Object getString(String key) {
		Minecraft ins = Minecraft.getInstance();
		LocalPlayer p = ins.player;
		Camera c = ins.gameRenderer.getMainCamera();
		return switch (key) {
			
			case "damagetype": yield p.getLastDamageSource() == null ? V2Runtime.NULL : p.getLastDamageSource().type().toString();
			
			/* Computer info */
			case "cpu_info": yield GLX._getCpuInfo();
			case "operating_system": yield Advanced.OS;
			case "month_name": yield Calendar.getInstance().getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault());
			case "locale": yield Locale.getDefault().getDisplayName();
			case "language": yield Locale.getDefault().getLanguage();
			case "country": yield Locale.getDefault().getCountry();


			/* Game */
			case "version_type": yield ins.getVersionType();
			case "game_version": yield SharedConstants.getCurrentVersion().id();



			/* Player */
			case "username": yield ins.player.getName().getString();
			case "uuid": yield ins.player.getStringUUID();
			


			/* Inventory */
			case "helditem_name": yield ins.player.getInventory()
				.getItem(ins.player.getInventory().getSelectedSlot()).getDisplayName().getString();
			
			
			
			/* GUI */
			case "openguitype": yield Advanced.getScreenType(ins.screen);
			case "openguititle": yield ins.screen == null ? V2Runtime.NULL : ins.screen.getTitle().getString();



			/* Mount information */
			case "mount_type": yield p.getVehicle() == null ? V2Runtime.NULL : p.getVehicle().getType().builtInRegistryHolder().key().location().toString();
			case "mount_armor_type": yield (p.getVehicle() instanceof AbstractHorse horse) ? horse.getBodyArmorItem().getItem().toString() : V2Runtime.NULL;
			case "mount_name": yield p.getVehicle() == null || p.getVehicle().getCustomName() == null ? V2Runtime.NULL : p.getVehicle().getCustomName().getString();



			/* World */
			case "biome":
				yield ins.level.getBiome(ins.player.blockPosition()).getRegisteredName();
			case "cam_biome":
				yield ins.level.getBiome(c.getBlockPosition()).getRegisteredName();
			case "dimension":
				yield ins.level.dimension().toString();
			case "world_name":
				yield ins.getSingleplayerServer() == null ? V2Runtime.NULL : ins.getSingleplayerServer().getWorldData().getLevelName();



			/* Server */
			case "server_name":
				yield ins.getCurrentServer() == null ? V2Runtime.NULL : ins.getCurrentServer().name;
			case "server_ip":
				yield ins.getCurrentServer() == null ? V2Runtime.NULL : ins.getCurrentServer().ip;
			case "server_motd":
				yield ins.getCurrentServer() == null ? V2Runtime.NULL : ins.getCurrentServer().motd.getString();


			
			/* Looking at */
			case "looking_at_pos": {
				BlockHitResult hit = raycast(ins, p.getEyePosition(1.0f), p.getLookAngle(), 50, false);
				yield (hit == null) ? V2Runtime.NULL : hit.getBlockPos().getX() + " " + hit.getBlockPos().getY() + " " + hit.getBlockPos().getZ();
			}

			case "cam_looking_at_pos": {
				Vec3 camPos = c.getPosition();
				Vec3 camLook = new Vec3(c.getLookVector().x(), c.getLookVector().y(), c.getLookVector().z());
				BlockHitResult hit = raycast(ins, camPos, camLook, 50, false);
				yield (hit == null) ? V2Runtime.NULL : hit.getBlockPos().getX() + " " + hit.getBlockPos().getY() + " " + hit.getBlockPos().getZ();
			}

			case "block_in_front": {
				BlockHitResult hit = raycast(ins, p.getEyePosition(1.0f), p.getLookAngle(), 50, false);
				yield (hit == null) ? V2Runtime.NULL : BuiltInRegistries.BLOCK.getKey(ins.level.getBlockState(hit.getBlockPos()).getBlock()).toString();
			}

			case "cam_block_in_front": {
				Vec3 camPos = c.getPosition();
				Vec3 camLook = new Vec3(c.getLookVector().x(), c.getLookVector().y(), c.getLookVector().z());
				BlockHitResult hit = raycast(ins, camPos, camLook, 50, false);
				yield (hit == null) ? V2Runtime.NULL : BuiltInRegistries.BLOCK.getKey(ins.level.getBlockState(hit.getBlockPos()).getBlock()).toString();
			}

			case "fluid_in_front": {
				BlockHitResult hit = raycast(ins, p.getEyePosition(1.0f), p.getLookAngle(), 50, true);
				yield (hit == null) ? V2Runtime.NULL : BuiltInRegistries.FLUID.getKey(ins.level.getFluidState(hit.getBlockPos()).getType()).toString();
			}

			case "cam_fluid_in_front": {
				Vec3 camPos = c.getPosition();
				Vec3 camLook = new Vec3(c.getLookVector().x(), c.getLookVector().y(), c.getLookVector().z());
				BlockHitResult hit = raycast(ins, camPos, camLook, 50, true);
				yield (hit == null) ? V2Runtime.NULL : BuiltInRegistries.FLUID.getKey(ins.level.getFluidState(hit.getBlockPos()).getType()).toString();
			}

			case "entity_in_front":
				yield (ins.crosshairPickEntity == null) ? V2Runtime.NULL : BuiltInRegistries.ENTITY_TYPE.getKey(ins.crosshairPickEntity.getType()).toString();



			/* Hudder */
			case "compilertype": yield Hudder.config.compilertype;
			case "mainfile": yield Hudder.config.mainfile;
			
			case "unset": yield "unset";
			
			default: yield null;
		};
	}
	private static BlockHitResult raycast(Minecraft ins, Vec3 start, Vec3 direction, double reach, boolean fluid) {
		ClipContext.Fluid fluidMode = fluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
		Vec3 end = start.add(direction.scale(reach));

		HitResult hit = ins.level.clip(new ClipContext(
				start,
				end,
				ClipContext.Block.OUTLINE,
				fluidMode,
				ins.player
		));

		return (hit.getType() == HitResult.Type.BLOCK) ? (BlockHitResult) hit : null;
	}
}
