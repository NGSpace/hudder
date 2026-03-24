package dev.ngspace.hudder.variables.data;

import static dev.ngspace.hudder.api.variableregistry.VariableTypes.BOOLEAN;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.NUMBER;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.STRING;

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
		register(_->ins.player.getFoodData().getSaturationLevel(), NUMBER, "saturation");
		register(_->ins.player.getFoodData().getFoodLevel(), NUMBER, "hunger");

		// Health
		register(_->ins.player.getHealth(), NUMBER, "health", "hp");
		register(_->ins.player.getMaxHealth(), NUMBER, "maxhealth", "maxhp");

		// Absorption
		register(_->ins.player.getAbsorptionAmount(), NUMBER, "absorption");
		register(_->ins.player.getMaxAbsorption(), NUMBER, "maxabsorption");

		// XP
		register(_->ins.player.experienceLevel, NUMBER, "xplevel");
		register(_->ins.player.experienceProgress*ins.player.getXpNeededForNextLevel(), NUMBER, "xp");
		register(_->ins.player.getXpNeededForNextLevel(), NUMBER, "next_level_xp_requirement");

		// Armor / movement
		register(_->ins.player.getArmorValue(), NUMBER, "armor");
		register(_->ins.player.fallDistance, NUMBER, "falldistance");

		// Air bubbles
		register(_->getCurrentAirSupplyBubble(Math.clamp(ins.player.getAirSupply(), 0,
				ins.player.getMaxAirSupply()),ins.player.getMaxAirSupply(),0), NUMBER, "airbubbles");

		register(_->getCurrentAirSupplyBubble(ins.player.getMaxAirSupply(),ins.player.getMaxAirSupply(),0),
				NUMBER, "maxairbubbles");
		
		// Attack indicator
		register(_->ins.player.getAttackStrengthScale(0.0F), NUMBER, "attack_cooldown");
	}
	
	private static void registerPlayerMovement() {
		// Movement / state flags
		register(_->ins.player.getAbilities().flying, BOOLEAN, "isflying");
		register(_->ins.player.isFallFlying(), BOOLEAN, "isgliding");
		register(_->ins.player.onClimbable(), BOOLEAN, "isclimbing");
		register(_->ins.player.isVisuallyCrawling(), BOOLEAN, "iscrawling");
		register(_->ins.player.isSwimming(), BOOLEAN, "isswimming");
		register(_->ins.player.isShiftKeyDown(), BOOLEAN, "issneaking");
		register(_->ins.player.isSprinting(), BOOLEAN, "issprinting");

		// Speed
		register(_->{
		    var p = ins.player;
		    var ent = (p.getVehicle() == null) ? p : p.getVehicle();

		    return Math.sqrt(
		            Math.pow(ent.getX() - ent.xOld, 2) +
		            Math.pow(ent.getY() - ent.yOld, 2) +
		            Math.pow(ent.getZ() - ent.zOld, 2)
		    ) * 20;
		}, NUMBER, "playerspeed");

		register(_->{
		    var p = ins.player;
		    var ent = (p.getVehicle() == null) ? p : p.getVehicle();

		    return Math.sqrt(
		            Math.pow(ent.getX() - ent.xOld, 2) +
		            Math.pow(ent.getZ() - ent.zOld, 2)
		    ) * 20;
		}, NUMBER, "horizontal_playerspeed");
	}
	
	private static void registerPlayerStateVariables() {
		// Player state flags
		register(_->ins.player.isAlive(), BOOLEAN, "isalive");
		register(_->ins.player.isBlocking(), BOOLEAN, "isblocking");
		register(_->ins.player.isFreezing(), BOOLEAN, "isfreezing");
		register(_->ins.player.isCurrentlyGlowing(), BOOLEAN, "isglowing");
		register(_->ins.player.fireImmune(), BOOLEAN, "isfireimmune");
		register(_->ins.player.isOnFire(), BOOLEAN, "isonfire");
		register(_->ins.player.onGround(), BOOLEAN, "isonground");
		register(_->ins.player.isInvisible(), BOOLEAN, "isinvisible");
		register(_->ins.player.isInWater(), BOOLEAN, "isdrowning");
		register(_->ins.player.getControlledVehicle() != null, BOOLEAN, "iscontrollingmount");
		register(_->ins.player.getVehicle() != null, BOOLEAN, "isonmount");
		
		register(_->ins.player.level().isRainingAt(ins.player.blockPosition()), BOOLEAN, "is_in_rain");
		register(_->ins.player.level().canSeeSky(ins.player.blockPosition()), BOOLEAN, "is_exposed_to_sky");

		// Game mode flags (with aliases)
		register(_->ins.gameMode.getPlayerMode() == GameType.SURVIVAL, BOOLEAN, "issurvival", "is_survival");
		register(_->ins.gameMode.getPlayerMode() == GameType.CREATIVE, BOOLEAN, "iscreative", "is_creative");
		register(_->ins.gameMode.getPlayerMode() == GameType.ADVENTURE, BOOLEAN, "isadventure", "is_adventure");
		register(_->ins.gameMode.getPlayerMode() == GameType.SPECTATOR, BOOLEAN, "isspectator", "is_spectator");
	}
	
	@SuppressWarnings("deprecation")
	private static void registerMountVariables() {
		// --- Mount numeric stats (nullable) ---

		register(_->(ins.player.getVehicle() instanceof LivingEntity entity) ? entity.getHealth() : null,
		    NUMBER, "mount_health", "mount_hp");

		register(_->(ins.player.getVehicle() instanceof LivingEntity entity) ? entity.getMaxHealth() : null,
		    NUMBER, "mount_maxhealth", "mount_maxhp");

		register(_->(ins.player.getVehicle() instanceof LivingEntity entity)
		        ? entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() : null, NUMBER, "mount_speed");

		register(_->(ins.player.getVehicle() instanceof LivingEntity entity)
		        ? entity.getAttribute(Attributes.JUMP_STRENGTH).getBaseValue() : null, NUMBER, "mount_jump_strength");

		register(_->(ins.player.getVehicle() instanceof AbstractHorse)
				? ins.player.getJumpRidingScale() : null, NUMBER, "mount_jump_scale");

		register(_->(ins.player.getVehicle() instanceof AbstractHorse horse) ? horse.getArmorValue() : null,
		    NUMBER, "mount_armor");

		register(_->(ins.player.getVehicle() instanceof AbstractHorse horse) ? horse.getJumpCooldown() : null,
		    NUMBER, "mount_jump_cooldown");


		// --- Mount string info (nullable) ---

		register(_->{
		    var v = ins.player.getVehicle();
		    return (v == null) ? null : v.getType().builtInRegistryHolder().key().identifier().toString();
		}, STRING, "mount_type");

		register(_->(ins.player.getVehicle() instanceof AbstractHorse horse)
		        ? horse.getBodyArmorItem().getItem().toString() : null, STRING, "mount_armor_type");

		register(_->{
		    var v = ins.player.getVehicle();
		    return (v == null || v.getCustomName() == null) ? null : v.getCustomName().getString();
		}, STRING, "mount_name");


		// --- Mount booleans ---

		register(_->ins.player.getVehicle() instanceof Mob mob && mob.isSaddled(),
		    BOOLEAN, "mount_is_saddled");

		register(_->ins.player.getVehicle() instanceof Mob mob && mob.isWearingBodyArmor(),
		    BOOLEAN, "mount_has_armor");

		register(_->ins.player.getVehicle() instanceof AbstractHorse horse && horse.isTamed(),
		    BOOLEAN, "mount_is_tamed");

		register(_->ins.player.getVehicle() instanceof AbstractChestedHorse horse && horse.hasChest(),
		    BOOLEAN, "mount_has_chest");
	}
	
	private static void registerLookingAtVariables() {
		/* Looking at */

		register(_->hitPos(raycastBlockPlayer(false, 50)), STRING, "looking_at_pos");
		register(_->hitPos(raycastCamera(false)), STRING, "cam_looking_at_pos");

		register(_->{
		    var hit = raycastBlockPlayer(false, 50);
		    return hit == null ? null : BuiltInRegistries.BLOCK
		    		.getKey(ins.level.getBlockState(hit.getBlockPos()).getBlock()).toString();
		}, STRING, "block_in_front");

		register(_->{
		    var hit = raycastCamera(false);
		    return hit == null ? null : BuiltInRegistries.BLOCK
		    		.getKey(ins.level.getBlockState(hit.getBlockPos()).getBlock()).toString();
		}, STRING, "cam_block_in_front");

		register(_->{
		    var hit = raycastBlockPlayer(true, 50);
		    return hit == null ? null : BuiltInRegistries.FLUID
		    		.getKey(ins.level.getFluidState(hit.getBlockPos()).getType()).toString();
		}, STRING, "fluid_in_front");

		register(_->{
		    var hit = raycastCamera(true);
		    return hit == null ? null : BuiltInRegistries.FLUID
		    		.getKey(ins.level.getFluidState(hit.getBlockPos()).getType()).toString();
		}, STRING, "cam_fluid_in_front");

		register(_->ins.crosshairPickEntity == null ? null : BuiltInRegistries.ENTITY_TYPE
				.getKey(ins.crosshairPickEntity.getType()) .toString(), STRING, "entity_in_front");
	}

	private static void registerPositionVariables() {
		/* Player position */

		register(_->ins.player.getX(), NUMBER, "dxpos", "dx");
		register(_->ins.player.getY(), NUMBER, "dypos", "dy");
		register(_->ins.player.getZ(), NUMBER, "dzpos", "dz");

		register(_->ins.player.getBlockX(), NUMBER, "xpos", "x");
		register(_->ins.player.getBlockY(), NUMBER, "ypos", "y");
		register(_->ins.player.getBlockZ(), NUMBER, "zpos", "z");


		/* Camera position */

		register(_->ins.gameRenderer.getMainCamera().position().x, NUMBER, "cam_dxpos");
		register(_->ins.gameRenderer.getMainCamera().position().y, NUMBER, "cam_dypos");
		register(_->ins.gameRenderer.getMainCamera().position().z, NUMBER, "cam_dzpos");

		register(_->ins.gameRenderer.getMainCamera().blockPosition().getX(), NUMBER, "cam_xpos");
		register(_->ins.gameRenderer.getMainCamera().blockPosition().getY(), NUMBER, "cam_ypos");
		register(_->ins.gameRenderer.getMainCamera().blockPosition().getZ(), NUMBER, "cam_zpos");
	}
	
	private static void registerRotationVariables() {
		/* Player rotation */

		// Pitch
		register(_->ins.player.getXRot(), NUMBER, "dpitch");
		register(_->(int) ins.player.getXRot(), NUMBER, "pitch");

		// Yaw (0–360)
		register(_->{
		    float yaw = ins.player.getYHeadRot();
		    return (yaw < 0) ? 360d + (yaw % 360d) : yaw % 360d;
		}, NUMBER, "dyaw");

		register(_->{
		    int yaw = (int) ins.player.getYHeadRot();
		    return (yaw < 0) ? 360 + (yaw % 360) : yaw % 360d;
		}, NUMBER, "yaw");

		// F3-style yaw
		register(_->Mth.wrapDegrees(ins.player.getYHeadRot()), NUMBER, "f3_dyaw");
		register(_->(int) Mth.wrapDegrees(ins.player.getYHeadRot()), NUMBER, "f3_yaw");



		/* Camera rotation */

		// Pitch
		register(_->ins.gameRenderer.getMainCamera().xRot(), NUMBER, "cam_dpitch");
		register(_->(int) ins.gameRenderer.getMainCamera().xRot(), NUMBER, "cam_pitch");

		// Yaw (0–360)
		register(_->{
		    float yaw = ins.gameRenderer.getMainCamera().yRot();
		    return (yaw < 0) ? 360d + (yaw % 360d) : yaw % 360d;
		}, NUMBER, "cam_dyaw");

		register(_->{
		    int yaw = (int) ins.gameRenderer.getMainCamera().yRot();
		    return (yaw < 0) ? 360 + (yaw % 360) : yaw % 360d;
		}, NUMBER, "cam_yaw");

		// F3-style yaw
		register(_->Mth.wrapDegrees(ins.gameRenderer.getMainCamera().yRot()), NUMBER, "cam_f3_dyaw");
		register(_->(int) Mth.wrapDegrees(ins.gameRenderer.getMainCamera().yRot()), NUMBER, "cam_f3_yaw");
	}
	
	private static void registerOtherPlayerVariables() {
		register(_->ins.player.getName().getString(), STRING, "username");
		register(_->ins.player.getStringUUID(), STRING, "uuid");

		register(_->{
		    var src = ins.player.getLastDamageSource();
		    return (src == null) ? null : src.type().msgId();
		}, STRING, "damagetype");

		register(_->ins.player.getInventory().getSelectedSlot(), NUMBER, "selectedslot");

		register(_->ins.player.getInventory()
		        .getItem(ins.player.getInventory().getSelectedSlot())
		        .getDisplayName()
		        .getString(),
		    STRING, "helditem_name");
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
	    var cam = ins.gameRenderer.getMainCamera();
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
