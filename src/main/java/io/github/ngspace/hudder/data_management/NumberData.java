package io.github.ngspace.hudder.data_management;

import java.util.Calendar;
import java.util.Queue;

import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.mixin.ParticleManagerAccessor;
import io.github.ngspace.hudder.mixin.WorldRendererAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class NumberData {private NumberData() {}
	static double MB = 1024d*1024d;
    static Runtime runtime = Runtime.getRuntime();
	
	public static Double getNumber(String key) {
		MinecraftClient ins = MinecraftClient.getInstance();
		PlayerEntity p = ins.player;
		WorldRenderer wr = ins.worldRenderer;
		World world = ins.world;
		int fps = Advanced.getFPS(ins);
		
		return switch(key) {
			
			/* Performance */
			case "fps": yield (double) fps;
			case "avgfps","avg_fps": yield (double) Advanced.getAverageFPS();
			case "minfps","min_fps": yield (double) Advanced.getMinimumFPS();
			case "maxfps","max_fps": yield (double) Advanced.getMaximumFPS();
			case "ping": yield (double) ins.getNetworkHandler().getPlayerListEntry(p.getName().getString()).getLatency();
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
			case "saturation": yield (double) p.getHungerManager().getSaturationLevel();
			case "hunger": yield (double) p.getHungerManager().getFoodLevel();
			case "health", "hp": yield (double) p.getHealth();
			case "maxhealth", "maxhp": yield (double) p.getMaxHealth();
			
			
			
			/* Other Player related information */
			case "selectedslot": yield (double) p.getInventory().selectedSlot;
			case "xplevel": yield (double) p.experienceLevel;
			case "xp": yield (double) p.totalExperience;

			case "playerspeed": {
				Entity ent = p.getVehicle() == null ? p : p.getVehicle();

			    double speed = (Math.sqrt(Math.pow(ent.getX() - ent.prevX, 2) +
			    		Math.pow(ent.getY() - ent.prevY , 2) + Math.pow(ent.getZ() - ent.prevZ , 2)) * 20);
			    yield speed;
			}
			case "horizontal_playerspeed": {
				Entity ent = p.getVehicle() == null ? p : p.getVehicle();

			    double speed = (Math.sqrt(Math.pow(ent.getX() - ent.prevX, 2) + Math.pow(ent.getZ() - ent.prevZ , 2)) * 20);
			    yield speed;
			}
			case "armor": yield (double) p.getArmor();
			
			
			
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
			case "chunkx": yield (double) p.getChunkPos().x;
			case "chunkz": yield (double) p.getChunkPos().z;
			
			
			
			/* Player roation*/
			case "dpitch": yield (double) p.getPitch();
			case "dyaw": yield p.getYaw() % 360d;
			case "pitch": yield (double) (int) p.getPitch();
			case "yaw": yield (double) (int) p.getYaw() % 360;
			
			

			/* World Rendering */
			case "entites", "entities": yield (double) ((WorldRendererAccess)wr).getRenderedEntitiesCount();
			case "particles": yield (double) ((ParticleManagerAccessor)ins.particleManager)
				.getParticles().values().stream().mapToInt(Queue::size).sum();
			case "chunks": yield wr.getChunkCount();
			
			
			
			/* World */
			case "light": yield (double) world.getLightLevel(p.getBlockPos());
			case "blocklight", "block_light": yield (double) world.getLightLevel(LightType.BLOCK,p.getBlockPos());
			case "skylight", "sky_light": yield (double) world.getLightLevel(LightType.SKY,p.getBlockPos());
			case "worldtime", "world_time": yield (double) world.getTimeOfDay();
			
			
			
			/* Hudder rendering */
			case "width": yield (double) ins.getWindow().getScaledWidth();
			case "height": yield (double) ins.getWindow().getScaledHeight();
			case "guiscale": yield ins.getWindow().getScaleFactor();

			case "scale": yield (double) ConfigManager.getConfig().scale;
			case "color": yield (double) ConfigManager.getConfig().color;
			case "yoffset": yield (double) ConfigManager.getConfig().yoffset;
			case "xoffset": yield (double) ConfigManager.getConfig().xoffset;
			case "lineheight": yield (double) ConfigManager.getConfig().lineHeight;
			case "methodbuffer": yield (double) ConfigManager.getConfig().methodBuffer;
			case "backgroundcolor": yield (double) ConfigManager.getConfig().backgroundcolor;
			
			
			
			/* Item Durabilities V3.0.0 */
			case "held_item_durability","helmet_durability","chestplate_durability","leggings_durability",
			"boots_durability","offhand_durability": {
				ItemStack stack = getStack(key, p.getInventory());
				yield (double) stack.getMaxDamage() - stack.getDamage();
			}
			case"held_item_max_durability","helmet_max_durability","chestplate_max_durability","leggings_max_durability",
			"boots_max_durability","offhand_max_durability":yield (double)getStack(key, p.getInventory()).getMaxDamage();
			
			default: yield null;
		};
	}
	public static float getTPS(MinecraftClient client) {
        IntegratedServer server = client.getServer();
        return server == null ? -1f : server.getTickManager().getTickRate();
	}
	public static ItemStack getStack(String type, PlayerInventory inv) {
		//I took some short cuts for a tiny performance increase. Probably not even calculatable.
		if (type.startsWith("held")) return inv.getStack(inv.selectedSlot);//held_item
		if (type.startsWith("helm")) return inv.getArmorStack(3);//helmet
		if (type.startsWith("c")) return inv.getArmorStack(2);//chestplate
		if (type.startsWith("l")) return inv.getArmorStack(1);//leggings
		if (type.startsWith("b")) return inv.getArmorStack(0);//boots
		if (type.startsWith("o")) return inv.offHand.get(0);//offhand
		throw new IllegalArgumentException("Unexpected value: " + type);
	}
}
