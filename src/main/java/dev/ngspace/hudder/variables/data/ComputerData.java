package dev.ngspace.hudder.variables.data;

import static dev.ngspace.hudder.api.variableregistry.VariableTypes.*;

import java.util.Calendar;
import java.util.Locale;

import com.mojang.blaze3d.platform.GLX;

import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import dev.ngspace.hudder.variables.advanced.Misc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;

import org.lwjgl.glfw.GLFW;

public class ComputerData extends HudderBuiltInVariables {
	static Minecraft ins;
	static final double MB = 1024d*1024d;
    static final Runtime runtime = Runtime.getRuntime();
	
	public static void registerVariables() {
		ins = Minecraft.getInstance();
		registerPerformanceVariables();
		registerMemoryVariables();
		registerTimeVariables();
		registerStringComputerInfo();

		register(_->ins.getConnection().getConnection().getAverageSentPackets(), NUMBER, "sent_packets");
		register(_->ins.getConnection().getConnection().getAverageReceivedPackets(), NUMBER, "received_packets");
	}

	public static void registerPerformanceVariables() {
		register(_->Misc.fps, NUMBER, "fps");
		register(_->Misc.getAverageFPS(), NUMBER, "avgfps", "avg_fps");
		register(_->Misc.getMinimumFPS(), NUMBER, "minfps", "min_fps");
		register(_->Misc.getMaximumFPS(), NUMBER, "maxfps", "max_fps");

		register(_->ins.options.enableVsync().get(), BOOLEAN, "vsync_enabled");
		register(
			_ -> Math.min(
				ins.getFramerateLimitTracker().getFramerateLimit(),
				Boolean.TRUE.equals(ins.options.enableVsync().get()) ? 
					GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).refreshRate() : Integer.MAX_VALUE
        	),
			NUMBER,
			"framerate_limit"
		);
		
		register(_->{
			var playerInfo = ins.player.connection.getPlayerInfo(ins.player.getUUID());
			return playerInfo==null?0:playerInfo.getLatency();
		}, NUMBER, "ping");
		
		register(_->{
	        IntegratedServer server = ins.getSingleplayerServer();
	        return server == null ? -1f : server.tickRateManager().tickrate();
		}, NUMBER, "tps");
		
		register(_->Math.min(ins.getGpuUtilization(), 100.0), NUMBER, "gpu_d", "dgpu");
		register(_->(int) (Math.min(ins.getGpuUtilization(), 100.0)), NUMBER, "gpu");
		register(_->Misc.CPU.get()* 100d, NUMBER, "cpu_d", "dcpu");
		register(_->(int) (Misc.CPU.get()* 100d), NUMBER, "cpu");
		
		register(_->Misc.delta, NUMBER, "delta");
	}
	
	public static void registerMemoryVariables() {
		register(_->runtime.maxMemory() / MB, NUMBER, "totalmemory","maxmemory","totalram","maxram");
		register(_->(runtime.totalMemory() - runtime.freeMemory()) / MB, NUMBER, "usedmemory","usedram");
		register(_->runtime.freeMemory() / MB, NUMBER, "freememory","freeram");
		register(_->runtime.freeMemory() / runtime.maxMemory(), NUMBER, "freememory_percentage","freeram_percentage");
		register(_->{
			double usedmem = ((double)runtime.totalMemory() - (double)runtime.freeMemory()) / MB;
			double totalmem = (runtime.maxMemory())/MB;
			return (int)(usedmem/totalmem*100);
		}, NUMBER, "usedmemory_percentage","usedram_percentage");
	}
	
	public static void registerTimeVariables() {
		register(_->System.currentTimeMillis(), NUMBER, "time");
		register(_->Calendar.getInstance().get(Calendar.MILLISECOND), NUMBER, "milliseconds");
		register(_->Calendar.getInstance().get(Calendar.SECOND), NUMBER, "seconds");
		register(_->Calendar.getInstance().get(Calendar.MINUTE), NUMBER, "minutes");
		register(_->Calendar.getInstance().get(Calendar.HOUR_OF_DAY), NUMBER, "hour");
		register(_->Calendar.getInstance().get(Calendar.DAY_OF_MONTH), NUMBER, "day");
		register(_->Calendar.getInstance().get(Calendar.MONTH)+1, NUMBER, "month");
		register(_->Calendar.getInstance().get(Calendar.YEAR), NUMBER, "year");
		
		register(_->Calendar.getInstance().getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault()), STRING, "month_name");
		register(_->clockify(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % 12),STRING,"hour12");
		register(_->Calendar.getInstance().get(Calendar.HOUR_OF_DAY) / 12 == 1 ? "pm" : "am", STRING, "ampm");
	}
	
	public static void registerStringComputerInfo() {
		register(_->GLX._getCpuInfo(), STRING, "cpu_info");
		register(_->Misc.OS, STRING, "operating_system");
		register(_->Locale.getDefault().getDisplayName(), STRING, "locale");
		register(_->Locale.getDefault().getLanguage(), STRING, "language");
		register(_->Locale.getDefault().getCountry(), STRING, "country");
	}
	
	private static String clockify(int time) {
		return String.valueOf(time < 10 ? "0" + time : time);
	}
}
