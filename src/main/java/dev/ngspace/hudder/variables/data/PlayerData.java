package dev.ngspace.hudder.variables.data;

import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlayerData extends HudderBuiltInVariables {
	static Minecraft ins;
	
	public static void registerVariables() {
		ins = Minecraft.getInstance();
		registerPlayerStatusVariables();
		registerPlayerMovement();
		registerPlayerStateVariables();
		registerMountVariables();
		registerLookingAtVariables();
		registerPositionVariables();
		registerRotationVariables();
		registerOtherPlayerVariables();
	}

	private static void registerPlayerStatusVariables() {
		// Food / hunger
		registerNumber(_->ins.player.getFoodData().getSaturationLevel(), "saturation");
		registerNumber(_->ins.player.getFoodData().getFoodLevel(), "hunger");

		// Health
		registerNumber(_->ins.player.getHealth(), "health", "hp");
		registerNumber(_->ins.player.getMaxHealth(), "maxhealth", "maxhp");

		// Absorption
		registerNumber(_->ins.player.getAbsorptionAmount(), "absorption");
		registerNumber(_->ins.player.getMaxAbsorption(), "maxabsorption");

		// XP
		registerNumber(_->ins.player.experienceLevel, "xplevel");
		registerNumber(_->ins.player.experienceProgress*ins.player.getXpNeededForNextLevel(), "xp");
		registerNumber(_->ins.player.getXpNeededForNextLevel(), "next_level_xp_requirement");

		// Armor / movement
		registerNumber(_->ins.player.getArmorValue(), "armor");
		registerNumber(_->ins.player.fallDistance, "falldistance");

		// Air bubbles
		registerNumber(_->getCurrentAirSupplyBubble(Math.clamp(ins.player.getAirSupply(), 0,
				ins.player.getMaxAirSupply()),ins.player.getMaxAirSupply(),0), "airbubbles");

		registerNumber(_->getCurrentAirSupplyBubble(ins.player.getMaxAirSupply(),ins.player.getMaxAirSupply(),0),
				"maxairbubbles");
		
		// Attack indicator
		registerNumber(_->ins.player.getAttackStrengthScale(0.0F), "attack_cooldown");
	}
	
	private static void registerPlayerMovement() {
		// Movement / state flags
		registerBoolean(_->ins.player.getAbilities().flying, "isflying");
		registerBoolean(_->ins.player.isFallFlying(), "isgliding");
		registerBoolean(_->ins.player.onClimbable(), "isclimbing");
		registerBoolean(_->ins.player.isVisuallyCrawling(), "iscrawling");
		registerBoolean(_->ins.player.isSwimming(), "isswimming");
		registerBoolean(_->ins.player.isShiftKeyDown(), "issneaking");
		registerBoolean(_->ins.player.isSprinting(), "issprinting");

		// Speed
		registerNumber(_->{
		    var p = ins.player;
		    var ent = (p.getVehicle() == null) ? p : p.getVehicle();

		    return Math.sqrt(
		            Math.pow(ent.getX() - ent.xOld, 2) +
		            Math.pow(ent.getY() - ent.yOld, 2) +
		            Math.pow(ent.getZ() - ent.zOld, 2)
		    ) * 20;
		}, "playerspeed");

		registerNumber(_->{
		    var p = ins.player;
		    var ent = (p.getVehicle() == null) ? p : p.getVehicle();

		    return Math.sqrt(
		            Math.pow(ent.getX() - ent.xOld, 2) +
		            Math.pow(ent.getZ() - ent.zOld, 2)
		    ) * 20;
		}, "horizontal_playerspeed");
	}
	
	private static void registerPlayerStateVariables() {
		// Player state flags
		registerBoolean(_->ins.player.isAlive(), "isalive");
		registerBoolean(_->ins.player.isBlocking(), "isblocking");
		registerBoolean(_->ins.player.isFreezing(), "isfreezing");
		registerBoolean(_->ins.player.isCurrentlyGlowing(), "isglowing");
		registerBoolean(_->ins.player.fireImmune(), "isfireimmune");
		registerBoolean(_->ins.player.isOnFire(), "isonfire");
		registerBoolean(_->ins.player.onGround(), "isonground");
		registerBoolean(_->ins.player.isInvisible(), "isinvisible");
		registerBoolean(_->ins.player.isInWater(), "isdrowning");
		registerBoolean(_->ins.player.getControlledVehicle() != null, "iscontrollingmount");
		registerBoolean(_->ins.player.getVehicle() != null, "isonmount");
		
		registerBoolean(_->ins.player.level().isRainingAt(ins.player.blockPosition()), "is_in_rain");
		registerBoolean(_->ins.player.level().canSeeSky(ins.player.blockPosition()), "is_exposed_to_sky");

		// Game mode flags (with aliases)
		registerBoolean(_->ins.gameMode.getPlayerMode() == GameType.SURVIVAL, "issurvival", "is_survival");
		registerBoolean(_->ins.gameMode.getPlayerMode() == GameType.CREATIVE, "iscreative", "is_creative");
		registerBoolean(_->ins.gameMode.getPlayerMode() == GameType.ADVENTURE, "isadventure", "is_adventure");
		registerBoolean(_->ins.gameMode.getPlayerMode() == GameType.SPECTATOR, "isspectator", "is_spectator");
	}
	
	@SuppressWarnings("deprecation")
	private static void registerMountVariables() {
		// --- Mount numeric stats (nullable) ---

		registerNumber(_->(ins.player.getVehicle() instanceof LivingEntity entity) ? entity.getHealth() : null,
		    "mount_health", "mount_hp");

		registerNumber(_->(ins.player.getVehicle() instanceof LivingEntity entity) ? entity.getMaxHealth() : null,
		    "mount_maxhealth", "mount_maxhp");

		registerNumber(_->(ins.player.getVehicle() instanceof LivingEntity entity)
		        ? entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() : null, "mount_speed");

		registerNumber(_->(ins.player.getVehicle() instanceof LivingEntity entity)
		        ? entity.getAttribute(Attributes.JUMP_STRENGTH).getBaseValue() : null, "mount_jump_strength");

		registerNumber(_->(ins.player.getVehicle() instanceof AbstractHorse)
				? ins.player.getJumpRidingScale() : null, "mount_jump_scale");

		registerNumber(_->(ins.player.getVehicle() instanceof AbstractHorse horse) ? horse.getArmorValue() : null,
		    "mount_armor");

		registerNumber(_->(ins.player.getVehicle() instanceof AbstractHorse horse) ? horse.getJumpCooldown() : null,
		    "mount_jump_cooldown");


		// --- Mount string info (nullable) ---

		registerString(_->{
		    var v = ins.player.getVehicle();
		    return (v == null) ? null : v.getType().builtInRegistryHolder().key().identifier().toString();
		}, "mount_type");

		registerString(_->(ins.player.getVehicle() instanceof AbstractHorse horse)
		        ? horse.getBodyArmorItem().getItem().toString() : null, "mount_armor_type");

		registerString(_->{
		    var v = ins.player.getVehicle();
		    return (v == null || v.getCustomName() == null) ? null : v.getCustomName().getString();
		}, "mount_name");


		// --- Mount booleans ---

		registerBoolean(_->ins.player.getVehicle() instanceof Mob mob && mob.isSaddled(),
		    "mount_is_saddled");

		registerBoolean(_->ins.player.getVehicle() instanceof Mob mob && mob.isWearingBodyArmor(),
		    "mount_has_armor");

		registerBoolean(_->ins.player.getVehicle() instanceof AbstractHorse horse && horse.isTamed(),
		    "mount_is_tamed");

		registerBoolean(_->ins.player.getVehicle() instanceof AbstractChestedHorse horse && horse.hasChest(),
		    "mount_has_chest");
	}
	
	private static void registerLookingAtVariables() {
		/* Looking at */

		registerString(_->hitPos(raycastBlockPlayer(false, 50)), "looking_at_pos");
		registerString(_->hitPos(raycastCamera(false)), "cam_looking_at_pos");

		registerString(_->{
		    var hit = raycastBlockPlayer(false, 50);
		    return hit == null ? null : BuiltInRegistries.BLOCK
		    		.getKey(ins.level.getBlockState(hit.getBlockPos()).getBlock()).toString();
		}, "block_in_front");

		registerString(_->{
		    var hit = raycastCamera(false);
		    return hit == null ? null : BuiltInRegistries.BLOCK
		    		.getKey(ins.level.getBlockState(hit.getBlockPos()).getBlock()).toString();
		}, "cam_block_in_front");

		registerString(_->{
		    var hit = raycastBlockPlayer(true, 50);
		    return hit == null ? null : BuiltInRegistries.FLUID
		    		.getKey(ins.level.getFluidState(hit.getBlockPos()).getType()).toString();
		}, "fluid_in_front");

		registerString(_->{
		    var hit = raycastCamera(true);
		    return hit == null ? null : BuiltInRegistries.FLUID
		    		.getKey(ins.level.getFluidState(hit.getBlockPos()).getType()).toString();
		}, "cam_fluid_in_front");

		registerString(_->ins.crosshairPickEntity == null ? null : BuiltInRegistries.ENTITY_TYPE
				.getKey(ins.crosshairPickEntity.getType()) .toString(), "entity_in_front");
	}

	private static void registerPositionVariables() {
		/* Player position */

		registerNumber(_->ins.player.getX(), "dxpos", "dx");
		registerNumber(_->ins.player.getY(), "dypos", "dy");
		registerNumber(_->ins.player.getZ(), "dzpos", "dz");

		registerNumber(_->ins.player.getBlockX(), "xpos", "x");
		registerNumber(_->ins.player.getBlockY(), "ypos", "y");
		registerNumber(_->ins.player.getBlockZ(), "zpos", "z");


		/* Camera position */

		registerNumber(_->ins.gameRenderer.mainCamera().position().x, "cam_dxpos");
		registerNumber(_->ins.gameRenderer.mainCamera().position().y, "cam_dypos");
		registerNumber(_->ins.gameRenderer.mainCamera().position().z, "cam_dzpos");

		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getX(), "cam_xpos");
		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getY(), "cam_ypos");
		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getZ(), "cam_zpos");
	}
	
	private static void registerRotationVariables() {
		/* Player rotation */

		// Pitch
		registerNumber(_->ins.player.getXRot(), "dpitch");
		registerNumber(_->(int) ins.player.getXRot(), "pitch");

		// Yaw (0–360)
		registerNumber(_->{
		    float yaw = ins.player.getYHeadRot();
		    return (yaw < 0) ? 360d + (yaw % 360d) : yaw % 360d;
		}, "dyaw");

		registerNumber(_->{
		    int yaw = (int) ins.player.getYHeadRot();
		    return (yaw < 0) ? 360 + (yaw % 360) : yaw % 360d;
		}, "yaw");

		// F3-style yaw
		registerNumber(_->Mth.wrapDegrees(ins.player.getYHeadRot()), "f3_dyaw");
		registerNumber(_->(int) Mth.wrapDegrees(ins.player.getYHeadRot()), "f3_yaw");



		/* Camera rotation */

		// Pitch
		registerNumber(_->ins.gameRenderer.mainCamera().xRot(), "cam_dpitch");
		registerNumber(_->(int) ins.gameRenderer.mainCamera().xRot(), "cam_pitch");

		// Yaw (0–360)
		registerNumber(_->{
		    float yaw = ins.gameRenderer.mainCamera().yRot();
		    return (yaw < 0) ? 360d + (yaw % 360d) : yaw % 360d;
		}, "cam_dyaw");

		registerNumber(_->{
		    int yaw = (int) ins.gameRenderer.mainCamera().yRot();
		    return (yaw < 0) ? 360 + (yaw % 360) : yaw % 360d;
		}, "cam_yaw");

		// F3-style yaw
		registerNumber(_->Mth.wrapDegrees(ins.gameRenderer.mainCamera().yRot()), "cam_f3_dyaw");
		registerNumber(_->(int) Mth.wrapDegrees(ins.gameRenderer.mainCamera().yRot()), "cam_f3_yaw");
	}
	
	private static void registerOtherPlayerVariables() {
		registerString(_->ins.player.getName().getString(), "username");
		registerString(_->ins.player.getStringUUID(), "uuid");

		registerString(_->{
		    var src = ins.player.getLastDamageSource();
		    return (src == null) ? null : src.type().msgId();
		}, "damagetype");

		registerNumber(_->ins.player.getInventory().getSelectedSlot(), "selectedslot");

		registerString(_->ins.player.getInventory()
		        .getItem(ins.player.getInventory().getSelectedSlot())
		        .getDisplayName()
		        .getString(),
		    "helditem_name");
	}
	
	private static BlockHitResult raycast(Minecraft ins,Vec3 start,Vec3 direction,double reach,boolean fluid) {
		ClipContext.Fluid fluidMode = fluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
		Vec3 end = start.add(direction.scale(reach));

		HitResult hit = ins.level.clip(new ClipContext(start,
				end,
				ClipContext.Block.OUTLINE,
				fluidMode,
				ins.player));

		return (hit.getType() == HitResult.Type.BLOCK) ? (BlockHitResult) hit : null;
	}

	private static BlockHitResult raycastBlockPlayer(boolean fluids, double reach) {
	    var p = ins.player;
	    return raycast(ins, p.getEyePosition(1.0f), p.getLookAngle(), reach, fluids);
	}

	private static BlockHitResult raycastCamera(boolean fluids) {
	    var cam = ins.gameRenderer.mainCamera();
	    Vec3 pos = cam.position();
	    Vec3 look = new Vec3(cam.forwardVector().x(), cam.forwardVector().y(), cam.forwardVector().z());
	    return raycast(ins, pos, look, 50, fluids);
	}

	private static String hitPos(BlockHitResult hit) {
	    if (hit == null) return null;
	    var p = hit.getBlockPos();
	    return p.getX() + " " + p.getY() + " " + p.getZ();
	}

	private static int getCurrentAirSupplyBubble(int i, int j, int k) {
		return Mth.ceil((float)((i + k) * 10) / (float)j);
	}
}
