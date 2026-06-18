package dev.ngspace.hudder.variables.data;

import static dev.ngspace.hudder.api.variableregistry.VariableTypes.BOOLEAN;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.NUMBER;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.STRING;

import java.util.Queue;

import dev.ngspace.hudder.mixin.LevelRendererAccess;
import dev.ngspace.hudder.mixin.ParticleManagerAccessor;
import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class WorldData extends HudderBuiltInVariables {
	static Minecraft ins;
	
	public static void registerVariables() {
		ins = Minecraft.getInstance();
		registerServerVariables();
		registerChunkVariables();
		registerRenderingVariables();
		registerOtherVariables();
	}

	private static void registerServerVariables() {
		register(_->{
		    var server = ins.getCurrentServer();
		    return (server == null) ? null : server.name;
		}, STRING, "server_name");

		register(_->{
		    var server = ins.getCurrentServer();
		    return (server == null) ? null : server.ip;
		}, STRING, "server_ip");

		register(_->{
		    var server = ins.getCurrentServer();
		    return (server == null) ? null : server.motd.getString();
		}, STRING, "server_motd");
	}

	private static void registerChunkVariables() {
		/* Player chunk information */
		
		register(_->ins.player.getBlockX() & 0xF, NUMBER, "subchunkx");
		register(_->ins.player.getBlockY() & 0xF, NUMBER, "subchunky");
		register(_->ins.player.getBlockZ() & 0xF, NUMBER, "subchunkz");

		register(_->ins.player.chunkPosition().x(), NUMBER, "chunkx");
		register(_->ins.player.chunkPosition().z(), NUMBER, "chunkz");

		/* Camera chunk information */
		
		register(_->ins.gameRenderer.getMainCamera().blockPosition().getX() & 0xF, NUMBER, "cam_subchunkx");
		register(_->ins.gameRenderer.getMainCamera().blockPosition().getY() & 0xF, NUMBER, "cam_subchunky");
		register(_->ins.gameRenderer.getMainCamera().blockPosition().getZ() & 0xF, NUMBER, "cam_subchunkz")
		;
		register(_->ins.gameRenderer.getMainCamera().blockPosition().getX() >> 4, NUMBER, "cam_chunkx");
		register(_->ins.gameRenderer.getMainCamera().blockPosition().getZ() >> 4, NUMBER, "cam_chunkz");
	}
	
	private static void registerRenderingVariables() {
		/* World Rendering */

		register(_->((LevelRendererAccess) ins.levelRenderer)
		                .getLevelRenderState()
		                .entityRenderStates
		                .size(),
		    NUMBER, "entites", "entities");

		register(_->((ParticleManagerAccessor) ins.particleEngine)
		                .getParticles()
		                .values()
		                .stream()
		                .mapToInt(Queue::size)
		                .sum(),
		    NUMBER, "particles");

		register(_->ins.levelRenderer.countRenderedSections(), NUMBER, "chunks");



		/* Light */
		/* At player */
		register(_->ins.level.getMaxLocalRawBrightness(ins.player.blockPosition()), NUMBER, "light");
		register(_->ins.level.getBrightness(LightLayer.BLOCK, ins.player.blockPosition()),
		    NUMBER, "blocklight", "block_light");
		register(_->ins.level.getBrightness(LightLayer.SKY, ins.player.blockPosition()),
		    NUMBER, "skylight", "sky_light");

		/* At camera */
		register(_->ins.level.getMaxLocalRawBrightness(ins.gameRenderer.getMainCamera().blockPosition()),
		    NUMBER, "cam_light");
		register(_->ins.level.getBrightness(LightLayer.BLOCK, ins.gameRenderer.getMainCamera().blockPosition()),
		    NUMBER, "cam_blocklight", "cam_block_light");
		register(_->ins.level.getBrightness(LightLayer.SKY, ins.gameRenderer.getMainCamera().blockPosition()),
		    NUMBER, "cam_skylight", "cam_sky_light");
	}
	
	private static void registerOtherVariables() {
		// Biome / dimension
		register(_->ins.level.getBiome(ins.player.blockPosition()).getRegisteredName(),
		    STRING, "biome");

		register(_->ins.level.getBiome(ins.gameRenderer.getMainCamera().blockPosition()).getRegisteredName(),
		    STRING, "cam_biome");

		register(_->ins.level.dimension().identifier().toString(),
		    STRING, "dimension");

		// World name (singleplayer only)
		register(_->{
		    var server = ins.getSingleplayerServer();
		    return (server == null) ? null : server.getWorldData().getLevelName();
		}, STRING, "world_name");

		// Time
		register(_->ins.level.getGameTime(), NUMBER, "worldtime", "world_time");
		register(_->ins.level.getGameTime() / 24000d, NUMBER, "daytime", "day_time");

		// Slime chunk
		register(_->{
		    try {
		        var server = ins.getSingleplayerServer();
		        if (server == null) return false;

		        var seed = server.getLevel(ins.level.dimension()).getSeed();
		        int cx = ins.player.getBlockX() >> 4;
		        int cz = ins.player.getBlockZ() >> 4;

		        return WorldgenRandom
		                .seedSlimeChunk(cx, cz, seed, 987234911L)
		                .nextInt(10) == 0;
		    } catch (Exception _) {
		    	// Fallback when there is no permission
		        return false;
		    }
		}, BOOLEAN, "isslime", "is_slime");
		
		// Weather
		register(_->ins.player.level().isRaining(), BOOLEAN, "is_raining");
		register(_->ins.player.level().isThundering(), BOOLEAN, "is_thundering");
		register(_->ins.player.level().canHaveWeather(), BOOLEAN, "can_have_weather");
		
		// Difficulty
		register(_->ins.player.level().getDifficulty().getSerializedName(), STRING, "difficulty");
	}
}
