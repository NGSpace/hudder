package dev.ngspace.hudder.variables.data;

import java.util.Queue;

import dev.ngspace.hudder.mixin.BossHealthOverlayAccessor;
import dev.ngspace.hudder.mixin.LevelRendererAccess;
import dev.ngspace.hudder.mixin.ParticleManagerAccessor;
import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import dev.ngspace.hudder.variables.advanced.BossBarData;
import dev.ngspace.hudder.variables.advanced.PlayerInformation;
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
		registerString(_->{
		    var server = ins.getCurrentServer();
		    return (server == null) ? null : server.name;
		}, "server_name");

		registerString(_->{
		    var server = ins.getCurrentServer();
		    return (server == null) ? null : server.ip;
		}, "server_ip");

		registerString(_->{
		    var server = ins.getCurrentServer();
		    return (server == null) ? null : server.motd.getString();
		}, "server_motd");
	}

	private static void registerChunkVariables() {
		/* Player chunk information */
		
		registerNumber(_->ins.player.getBlockX() & 0xF, "subchunkx");
		registerNumber(_->ins.player.getBlockY() & 0xF, "subchunky");
		registerNumber(_->ins.player.getBlockZ() & 0xF, "subchunkz");

		registerNumber(_->ins.player.chunkPosition().x(), "chunkx");
		registerNumber(_->ins.player.chunkPosition().z(), "chunkz");

		/* Camera chunk information */
		
		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getX() & 0xF, "cam_subchunkx");
		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getY() & 0xF, "cam_subchunky");
		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getZ() & 0xF, "cam_subchunkz")
		;
		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getX() >> 4, "cam_chunkx");
		registerNumber(_->ins.gameRenderer.mainCamera().blockPosition().getZ() >> 4, "cam_chunkz");
	}
	
	private static void registerRenderingVariables() {
		/* World Rendering */

		registerNumber(_->((LevelRendererAccess) ins.levelRenderer)
	                .getLevelRenderState()
	                .entityRenderStates
	                .size(),
		    "entites", "entities");

		registerNumber(_->((ParticleManagerAccessor) ins.particleEngine)
	                .getParticles()
	                .values()
	                .stream()
	                .mapToInt(Queue::size)
	                .sum(),
		    "particles");

		registerNumber(_->ins.levelExtractor.countRenderedSections(), "chunks");



		/* Light */
		/* At player */
		registerNumber(_->ins.level.getMaxLocalRawBrightness(ins.player.blockPosition()), "light");
		registerNumber(_->ins.level.getBrightness(LightLayer.BLOCK, ins.player.blockPosition()),
		    "blocklight", "block_light");
		registerNumber(_->ins.level.getBrightness(LightLayer.SKY, ins.player.blockPosition()),
		    "skylight", "sky_light");

		/* At camera */
		registerNumber(_->ins.level.getMaxLocalRawBrightness(ins.gameRenderer.mainCamera().blockPosition()),
		    "cam_light");
		registerNumber(_->ins.level.getBrightness(LightLayer.BLOCK, ins.gameRenderer.mainCamera().blockPosition()),
		    "cam_blocklight", "cam_block_light");
		registerNumber(_->ins.level.getBrightness(LightLayer.SKY, ins.gameRenderer.mainCamera().blockPosition()),
		    "cam_skylight", "cam_sky_light");
		
		// Boss bars
		registerObject(_->{
			var obj = ((BossHealthOverlayAccessor)ins.gui.hud.getBossOverlay()).events();
			return obj.entrySet()
					.stream()
					.map(e->new BossBarData(e.getValue()))
					.toList();
		}, "boss_bars");
	}
	
	private static void registerOtherVariables() {
		// Biome / dimension
		registerString(_->ins.level.getBiome(ins.player.blockPosition()).getRegisteredName(),
		    "biome");

		registerString(_->ins.level.getBiome(ins.gameRenderer.mainCamera().blockPosition()).getRegisteredName(),
		    "cam_biome");

		registerString(_->ins.level.dimension().identifier().toString(),
		    "dimension");

		// World name (singleplayer only)
		registerString(_->{
		    var server = ins.getSingleplayerServer();
		    return (server == null) ? null : server.getWorldData().getLevelName();
		}, "world_name");

		// Time
		registerNumber(_->ins.level.getGameTime(), "worldtime", "world_time");
		registerNumber(_->ins.level.getGameTime() / 24000d, "daytime", "day_time");

		// Slime chunk
		registerBoolean(_->{
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
		}, "isslime", "is_slime");
		
		// Weather
		registerBoolean(_->ins.player.level().isRaining(), "is_raining");
		registerBoolean(_->ins.player.level().isThundering(), "is_thundering");
		registerBoolean(_->ins.player.level().canHaveWeather(), "can_have_weather");
		
		// Difficulty
		registerString(_->ins.player.level().getDifficulty().getSerializedName(), "difficulty");
		
		// PlayerList
		registerObject(_->ins.getConnection().getListedOnlinePlayers()
				.stream()
				.map(p -> new PlayerInformation(p.getProfile().name(), p.getProfile().id(),
						p.getTabListDisplayName(), p.getTabListOrder(),
						p.getTeam() != null ? p.getTeam().getName() : "", p.getGameMode().toString()))
				.toList(),
			"players_list");
		
		// Is Singleplayer
		registerBoolean(_->ins.hasSingleplayerServer(), "is_singleplayer");
	}
}
