package io.github.ngspace.hudder.data_management;

import java.util.Queue;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.data_management.api.DataVariableRegistry;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.mixin.LevelRendererAccess;
import io.github.ngspace.hudder.mixin.ParticleManagerAccessor;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.LightLayer;

public class NumberData {private NumberData() {}
	
	public static Object getNumber(String key) {
		Minecraft ins = Minecraft.getInstance();
		LocalPlayer p = ins.player;
		Camera c = ins.gameRenderer.getMainCamera();
		HudderConfig config = Hudder.config;
		
		return switch(key) {
			
			
			
			/* Food and health */
			case "saturation": yield (double) p.getFoodData().getSaturationLevel();
			case "hunger": yield (double) p.getFoodData().getFoodLevel();
			case "health", "hp": yield (double) p.getHealth();
			case "maxhealth", "maxhp": yield (double) p.getMaxHealth();
			case "absorption": yield (double) p.getAbsorptionAmount();
			case "maxabsorption": yield (double) p.getMaxAbsorption();



			/* Mount information */
			case "mount_health", "mount_hp": yield (p.getVehicle() instanceof LivingEntity entity) ? (double) entity.getHealth() : V2Runtime.NULL;
			case "mount_maxhealth", "mount_maxhp": yield (p.getVehicle() instanceof LivingEntity entity) ? (double) entity.getMaxHealth() : V2Runtime.NULL;
			case "mount_speed": yield (p.getVehicle() instanceof LivingEntity entity) ? entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() : V2Runtime.NULL;
			case "mount_jump_strength": yield (p.getVehicle() instanceof LivingEntity entity) ? entity.getAttribute(Attributes.JUMP_STRENGTH).getBaseValue() : V2Runtime.NULL;
			case "mount_jump_scale": yield (p.getVehicle() instanceof AbstractHorse) ? (double) p.getJumpRidingScale() : V2Runtime.NULL;
			case "mount_armor": yield (p.getVehicle() instanceof AbstractHorse) ? (double) ((AbstractHorse) p.getVehicle()).getArmorValue() : V2Runtime.NULL;
			case "mount_jump_cooldown": yield (p.getVehicle() instanceof AbstractHorse) ? (double) ((AbstractHorse) p.getVehicle()).getJumpCooldown() : V2Runtime.NULL;



			/* Other Player related information */
			case "selectedslot": yield (double) p.getInventory().getSelectedSlot();
			case "xplevel": yield (double) p.experienceLevel;
			case "xp": yield (double) p.totalExperience;
			case "armor": yield (double) p.getArmorValue();
			case "falldistance": yield p.fallDistance;
			case "airbubbles": {
				yield (double) getCurrentAirSupplyBubble(Math.clamp(p.getAirSupply(), 0, p.getMaxAirSupply()), p.getMaxAirSupply(), 0);
			}
			case "maxairbubbles":  {
				yield (double) getCurrentAirSupplyBubble(p.getMaxAirSupply(), p.getMaxAirSupply(), 0);
			}

			case "playerspeed": {
				var ent = p.getVehicle() == null ? p : p.getVehicle();
				if (ent==null) yield 0;

			    double speed = (Math.sqrt(Math.pow(ent.getX() - ent.xOld, 2) +
			    		Math.pow(ent.getY() - ent.yOld , 2) + Math.pow(ent.getZ() - ent.zOld , 2)) * 20);
			    yield speed;
			}
			case "horizontal_playerspeed": {
				var ent = p.getVehicle() == null ? p : p.getVehicle();
				if (ent==null) yield 0;

			    double speed = (Math.sqrt(Math.pow(ent.getX() - ent.xOld, 2) + Math.pow(ent.getZ() - ent.zOld , 2)) * 20);
			    yield speed;
			}
			case "cps": yield Advanced.getLeftCPS() + Advanced.getRightCPS();
			case "cps_left": yield Advanced.getLeftCPS();
			case "cps_right": yield Advanced.getRightCPS();


			
			/* Player position */
			case "dxpos","dx": yield p.getX();
			case "dypos","dy": yield p.getY();
			case "dzpos","dz": yield p.getZ();
			case "xpos","x": yield (double) p.getBlockX();
			case "ypos","y": yield (double) p.getBlockY();
			case "zpos","z": yield (double) p.getBlockZ();



			/* Camera position */
			case "cam_dxpos": yield c.position().x;
			case "cam_dypos": yield c.position().y;
			case "cam_dzpos": yield c.position().z;
			case "cam_xpos": yield (double) c.blockPosition().getX();
			case "cam_ypos": yield (double) c.blockPosition().getY();
			case "cam_zpos": yield (double) c.blockPosition().getZ();
			
			
			
			/* Chunk information */
			case "subchunkx": yield (double) (p.getBlockX() & 0xF);
			case "subchunky": yield (double) (p.getBlockY() & 0xF);
			case "subchunkz": yield (double) (p.getBlockZ() & 0xF);
			case "chunkx": yield (double) p.chunkPosition().x;
			case "chunkz": yield (double) p.chunkPosition().z;



			/* Camera chunk information */
			case "cam_subchunkx": yield (double) (c.blockPosition().getX() & 0xF);
			case "cam_subchunky": yield (double) (c.blockPosition().getY() & 0xF);
			case "cam_subchunkz": yield (double) (c.blockPosition().getZ() & 0xF);
			case "cam_chunkx": yield (double) (c.blockPosition().getX() >> 4);
			case "cam_chunkz": yield (double) (c.blockPosition().getZ() >> 4);



			/* Player roation */
			// Pitch
			case "dpitch": yield (double) p.getXRot();
			case "pitch": yield (double) (int) p.getXRot();
			// Yaw
			case "dyaw": {
				float yaw = p.getYHeadRot();
				if (yaw<0) yield (double) (360d+(yaw % 360d));
				yield yaw % 360d;
			}
			case "yaw":  {
				int yaw = (int) p.getYHeadRot();
				if (yaw<0) yield (double) (360+(yaw % 360));
				yield yaw % 360d;
			}
			// F3 yaw
			case "f3_dyaw": yield (double) Mth.wrapDegrees(p.getYHeadRot());
			case "f3_yaw": yield (double) (int) Mth.wrapDegrees(p.getYHeadRot());



			/* Camera roation */
			// Pitch
			case "cam_dpitch": yield (double) c.xRot();
			case "cam_pitch": yield (double) (int) c.xRot();
			// Yaw
			case "cam_dyaw": {
				float yaw = c.yRot();
				if (yaw<0) yield (double) (360d+(yaw % 360d));
				yield yaw % 360d;
			}
			case "cam_yaw":  {
				int yaw = (int) c.yRot();
				if (yaw<0) yield (double) (360+(yaw % 360));
				yield yaw % 360d;
			}
			// F3 yaw
			case "cam_f3_dyaw": yield (double) Mth.wrapDegrees(c.yRot());
			case "cam_f3_yaw": yield (double) (int) Mth.wrapDegrees(c.yRot());



			/* World Rendering */
			case "entites", "entities": yield ((LevelRendererAccess) ins.levelRenderer).getLevelRenderState()
				.entityRenderStates.size();
			case "particles": yield (double) ((ParticleManagerAccessor)ins.particleEngine)
				.getParticles().values().stream().mapToInt(Queue::size).sum();
			case "chunks": yield (double) ins.levelRenderer.countRenderedSections();
			
			
			
			/* Light */
			/* At player */
			case "light": yield (double) ins.level.getMaxLocalRawBrightness(p.blockPosition());
			case "blocklight", "block_light": yield (double) ins.level.getBrightness(LightLayer.BLOCK,p.blockPosition());
			case "skylight", "sky_light": yield (double) ins.level.getBrightness(LightLayer.SKY,p.blockPosition());
			/* At camera */
			case "cam_light": yield (double) ins.level.getMaxLocalRawBrightness(c.blockPosition());
			case "cam_blocklight", "cam_block_light": yield (double) ins.level.getBrightness(LightLayer.BLOCK,c.blockPosition());
			case "cam_skylight", "cam_sky_light": yield (double) ins.level.getBrightness(LightLayer.SKY,c.blockPosition());
			
			
			
			/* World */
			case "worldtime", "world_time": yield (double) ins.level.getDayTime();
			case "daytime", "day_time": yield ins.level.getDayTime()/24000d;



			/* GUI rendering */
			case "width": yield (double) ins.getWindow().getGuiScaledWidth();
			case "height": yield (double) ins.getWindow().getGuiScaledHeight();
			case "guiscale": yield (double) ins.getWindow().getGuiScale();
			
			
			/* Hudder */
			case "scale": yield (double) config.scale;
			case "color": yield (double) config.color;
			case "yoffset": yield (double) config.yoffset;
			case "xoffset": yield (double) config.xoffset;
			case "lineheight": yield (double) config.lineHeight;
			case "methodbuffer": yield (double) config.methodBuffer;
			case "backgroundcolor": yield (double) config.backgroundcolor;
			
			
			
			case "rebeccapurple": yield (double) 0xFF663399;
			
			default: yield DataVariableRegistry.getNumber(key);
		};
	}

	public static int getCurrentAirSupplyBubble(int i, int j, int k) {
		return Mth.ceil((float)((i + k) * 10) / (float)j);
	}
}
