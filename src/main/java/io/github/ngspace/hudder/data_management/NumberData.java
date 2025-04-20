package io.github.ngspace.hudder.data_management;

import java.util.Calendar;
import java.util.Queue;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.mixin.ParticleManagerAccessor;
import io.github.ngspace.hudder.mixin.WorldRendererAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;

public class NumberData {private NumberData() {}
	static final double MB = 1024d*1024d;
    static final Runtime runtime = Runtime.getRuntime();
	
	public static Double getNumber(String key) {
		Minecraft ins = Minecraft.getInstance();
		LocalPlayer p = ins.player;
		int fps = Advanced.getFPS(ins);
		HudderConfig config = Hudder.config;
		
		return switch(key) {
			
			/* Performance */
			case "fps": yield (double) fps;
			case "avgfps","avg_fps": yield (double) Advanced.getAverageFPS();
			case "minfps","min_fps": yield (double) Advanced.getMinimumFPS();
			case "maxfps","max_fps": yield (double) Advanced.getMaximumFPS();
			case "ping": yield (double) ins.getConnection().getPlayerInfo(p.getName().getString()).getLatency();
			case "tps": yield (double) getTPS(ins);
			
			case "gpu_d", "dgpu": yield Advanced.gpuUsage;
			case "gpu": yield (double) ((int)Advanced.gpuUsage);
			case "cpu_d": yield Advanced.CPU.get()* 100d;
			case "cpu": yield (double) (int) (Advanced.CPU.get()* 100d);
			
			case "delta": yield (double) Advanced.delta;
			
			
			/* Memory */
			case "totalmemory","maxmemory","totalram","maxram": yield runtime.maxMemory() / MB;
			case "usedmemory","usedram": yield (runtime.totalMemory() - runtime.freeMemory()) / MB;
			case "freememory","freeram": yield runtime.freeMemory() / MB;
			case "usedmemory_percentage","usedram_percentage":
				double usedmem = ((double)runtime.totalMemory() - (double)runtime.freeMemory()) / MB;
				double totalmem = (runtime.maxMemory())/MB;
				yield (double) ((int)(usedmem/totalmem*100));
			case "freememory_percentage","freeram_percentage": yield (double) runtime.freeMemory() / runtime.maxMemory();
			
			
			
			/* time */
			case "time": yield (double) System.currentTimeMillis();
			case "milliseconds": yield (double) Calendar.getInstance().get(Calendar.MILLISECOND);
			case "seconds": yield (double) Calendar.getInstance().get(Calendar.SECOND);
			case "minutes": yield (double) Calendar.getInstance().get(Calendar.MINUTE);
			case "hour": yield (double) Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			case "day": yield (double) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			case "month": yield (double) Calendar.getInstance().get(Calendar.MONTH);
			case "year": yield (double) Calendar.getInstance().get(Calendar.YEAR);
			
			
			
			/* Food and health */
			case "saturation": yield (double) p.getFoodData().getSaturationLevel();
			case "hunger": yield (double) p.getFoodData().getFoodLevel();
			case "health", "hp": yield (double) p.getHealth();
			case "maxhealth", "maxhp": yield (double) p.getMaxHealth();
			
			
			
			/* Other Player related information */
			case "selectedslot": yield (double) p.getInventory().selected;
			case "xplevel": yield (double) p.experienceLevel;
			case "xp": yield (double) p.totalExperience;
			case "armor": yield (double) p.getArmorValue();
			case "falldistance": yield (double) p.fallDistance;

			case "playerspeed": {
				var ent = p.getVehicle() == null ? p : p.getVehicle();

			    double speed = (Math.sqrt(Math.pow(ent.getX() - ent.xOld, 2) +
			    		Math.pow(ent.getY() - ent.yOld , 2) + Math.pow(ent.getZ() - ent.zOld , 2)) * 20);
			    yield speed;
			}
			case "horizontal_playerspeed": {
				var ent = p.getVehicle() == null ? p : p.getVehicle();

			    double speed = (Math.sqrt(Math.pow(ent.getX() - ent.xOld, 2) + Math.pow(ent.getZ() - ent.zOld , 2)) * 20);
			    yield speed;
			}
			
			
			
			/* Player position */
			case "dxpos","dx": yield p.getX();
			case "dypos","dy": yield p.getY();
			case "dzpos","dz": yield p.getZ();
			case "xpos","x": yield (double) p.getBlockX();
			case "ypos","y": yield (double) p.getBlockY();
			case "zpos","z": yield (double) p.getBlockZ();
			
			
			
			/* Chunk information */
			case "subchunkx": yield (double) (p.getBlockX() & 0xF);
			case "subchunky": yield (double) (p.getBlockY() & 0xF);
			case "subchunkz": yield (double) (p.getBlockZ() & 0xF);
			case "chunkx": yield (double) p.chunkPosition().x;
			case "chunkz": yield (double) p.chunkPosition().z;
			
			
			
			/* Player roation*/
			case "dpitch": yield (double) p.getXRot();
			case "dyaw": yield p.getYHeadRot() % 360d;
			case "pitch": yield (double) (int) p.getXRot();
			case "yaw": yield (double) (int) p.getYHeadRot() % 360;
			
			

			/* World Rendering */
			case "entites", "entities": yield (double) ((WorldRendererAccess)ins.levelRenderer).getVisibleEntityCount();
			case "particles": yield (double) ((ParticleManagerAccessor)ins.particleEngine)
				.getParticles().values().stream().mapToInt(Queue::size).sum();
			case "chunks": yield (double) ins.levelRenderer.countRenderedSections();
			
			
			
			/* World */
			case "light": yield (double) ins.level.getMaxLocalRawBrightness(p.blockPosition());
			case "blocklight", "block_light": yield (double) ins.level.getBrightness(LightLayer.BLOCK,p.blockPosition());
			case "skylight", "sky_light": yield (double) ins.level.getBrightness(LightLayer.SKY,p.blockPosition());
			case "worldtime", "world_time": yield (double) ins.level.getDayTime();
			case "daytime", "day_time": yield ins.level.getDayTime()/24000d;
			
			
			
			/* Hudder rendering */
			case "width": yield (double) ins.getWindow().getGuiScaledWidth();
			case "height": yield (double) ins.getWindow().getGuiScaledHeight();
			case "guiscale": yield ins.getWindow().getGuiScale();

			case "scale": yield (double) config.scale;
			case "color": yield (double) config.color;
			case "yoffset": yield (double) config.yoffset;
			case "xoffset": yield (double) config.xoffset;
			case "lineheight": yield (double) config.lineHeight;
			case "methodbuffer": yield (double) config.methodBuffer;
			case "backgroundcolor": yield (double) config.backgroundcolor;
			
			
			
			/* Item Durabilities V3.0.0 */
			case "held_item_durability","helmet_durability","chestplate_durability","leggings_durability",
			"boots_durability","offhand_durability": {
				ItemStack stack = getStack(key, p.getInventory());
				yield (double) stack.getMaxDamage() - stack.getDamageValue();
			}
			case"held_item_max_durability","helmet_max_durability","chestplate_max_durability","leggings_max_durability",
			"boots_max_durability","offhand_max_durability":yield (double)getStack(key, p.getInventory()).getMaxDamage();
			
			
			
			case "rebeccapurple": yield (double) 0xFF663399;
			
			default: yield null;
		};
	}
	public static float getTPS(Minecraft client) {
        IntegratedServer server = client.getSingleplayerServer();
        return server == null ? -1f : server.tickRateManager().tickrate();
	}
	public static ItemStack getStack(String type, Inventory inv) {
		//I took some short cuts for a tiny performance increase. Probably not even calculatable.
		if (type.startsWith("held")) return inv.getItem(inv.selected);//held_item
		if (type.startsWith("helm")) return inv.getArmor(3);//helmet
		if (type.startsWith("c")) return inv.getArmor(2);//chestplate
		if (type.startsWith("l")) return inv.getArmor(1);//leggings
		if (type.startsWith("b")) return inv.getArmor(0);//boots
		if (type.startsWith("o")) return inv.offhand.get(0);//offhand
		throw new IllegalArgumentException("Unexpected value: " + type);
	}
}
