package io.github.ngspace.hudder.data_management;

import java.util.Collection;

import org.joml.Random;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.mixin.ParticleManagerAccessor;
import io.github.ngspace.hudder.mixin.WorldRendererAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import static io.github.ngspace.hudder.data_management.Advanced.*;

public class NumberData {private NumberData() {}
	static double MB = 1024d*1024d;
    static Runtime runtime = Runtime.getRuntime();
	
	public static Double getNumber(String key) throws CompileException {
		MinecraftClient ins = MinecraftClient.getInstance();
		PlayerEntity p = ins.player;
		WorldRenderer wr = ins.worldRenderer;
		World world = ins.world;
		int fps = getFPS(ins);
		return switch(key) {
			
			/* Performance */
			case "fps": yield (double) fps;
			case "avgfps","avg_fps": yield (double) getAverageFPS();
			case "minfps","min_fps": yield (double) getMinimumFPS();
			case "maxfps","max_fps": yield (double) getMaximumFPS();
			case "ping": yield (double) ins.getNetworkHandler().getPlayerListEntry(p.getName().getString()).getLatency();
			case "tps": yield (double) getTPS(ins);
			case "gpu_d": yield Advanced.gpuUsage;
			case "gpu": yield (double) ((int)Advanced.gpuUsage);
			case "cpu": throw new CompileException("due to my lack of skills and mental abilites cpu is unavaliable");
			
			/* Memory */
			case "totalmemory","maxmemory","totalram","maxram": yield runtime.maxMemory() / MB;
			case "usedmemory","usedram": yield (runtime.totalMemory() - runtime.freeMemory()) / MB;
			case "freememory","freeram": yield runtime.freeMemory() / MB;
			case "usedmemory_percentage","usedram_percentage":
				double usedmem = ((double)runtime.totalMemory() - (double)runtime.freeMemory()) / MB;
				double totalmem = (runtime.maxMemory())/MB;
				yield (double) ((int)(usedmem/totalmem*100));
			case "freememory_percentage","freeram_percentage": yield (double) runtime.freeMemory() / runtime.maxMemory();
			
			
			
			/* Food and health */
			case "saturation": yield (double) p.getHungerManager().getSaturationLevel();
			case "hunger": yield (double) p.getHungerManager().getFoodLevel();
			case "previoushunger": yield (double) p.getHungerManager().getPrevFoodLevel();
			case "exhaustion": yield (double) p.getHungerManager().getExhaustion();
			
			case "health", "hp": yield (double) p.getHealth();
			case "maxhealth", "maxhp": yield (double) p.getMaxHealth();
			
			
			
			/* Other Player related information */
			case "dxpos","dx": yield p.getX();
			case "dypos","dy": yield p.getY();
			case "dzpos","dz": yield p.getZ();
			case "xpos","x": yield (double) p.getBlockX();
			case "ypos","y": yield (double) p.getBlockY();
			case "zpos","z": yield (double) p.getBlockZ();
			
			

			/* World Rendering */
			case "entites": yield (double) ((WorldRendererAccess)wr).getRegularEntityCount();
			case "particles": yield (double) ((ParticleManagerAccessor)ins.particleManager)
				.getParticles().values().stream().mapToInt(Collection::size).sum();
			case "chunks": yield wr.getChunkCount();
			
			
			
			/* World */
			case "light": yield (double) world.getLightLevel(p.getBlockPos());
			case "blocklight", "block_light": yield (double) world.getLightLevel(LightType.BLOCK,p.getBlockPos());
			case "skylight", "sky_light": yield (double) world.getLightLevel(LightType.SKY,p.getBlockPos());
			
			case "random","rng": yield (double) new Random().nextFloat();
			
			/* Hudder rendering */
			case "width": yield (double) ins.getWindow().getScaledWidth();
			case "height": yield (double) ins.getWindow().getScaledHeight();
			case "guiscale": yield ins.getWindow().getScaleFactor();

			case "color": yield (double) ConfigManager.getConfig().color;
			case "scale": yield (double) ConfigManager.getConfig().scale;
			case "yoffset": yield (double) ConfigManager.getConfig().yoffset;
			case "xoffset": yield (double) ConfigManager.getConfig().xoffset;
			case "lineheight": yield (double) ConfigManager.getConfig().lineHeight;
			
			default: yield null;
		};
	}
	public static float getTPS(MinecraftClient client) {
        IntegratedServer server = client.getServer();
        return server == null ? -1f : server.getTickManager().getTickRate();
	}
}
