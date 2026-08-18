package dev.ngspace.hudder.variables.data;


import java.util.Calendar;
import java.util.Locale;


import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GLX;

import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import dev.ngspace.hudder.variables.advanced.Misc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

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

		registerNumber(_->ins.getConnection().getConnection().getAverageSentPackets(), "sent_packets");
		registerNumber(_->ins.getConnection().getConnection().getAverageReceivedPackets(), "received_packets");
	}

	public static void registerPerformanceVariables() {
		registerNumber(_->Misc.fps, "fps");
		registerNumber(_->Misc.getAverageFPS(), "avgfps", "avg_fps");
		registerNumber(_->Misc.getMinimumFPS(), "minfps", "min_fps");
		registerNumber(_->Misc.getMaximumFPS(), "maxfps", "max_fps");

		registerBoolean(_->ins.options.enableVsync().get(), "vsync_enabled");
		registerNumber(
			_ -> Math.min(
				ins.getFramerateLimitTracker().getFramerateLimit(),
				Boolean.TRUE.equals(ins.options.enableVsync().get()) ? 
						ins.getWindow().getActiveVideoMode().getRefreshRate() : Integer.MAX_VALUE
        	),
			"framerate_limit"
		);
		
		registerNumber(_->{
			var playerInfo = ins.player.connection.getPlayerInfo(ins.player.getUUID());
			return playerInfo==null?0:playerInfo.getLatency();
		}, "ping");
		
		registerNumber(_->{
	        IntegratedServer server = ins.getSingleplayerServer();
	        return server == null ? -1f : server.tickRateManager().tickrate();
		}, "tps");
		
		registerNumber(_->Math.min(ins.getGpuUtilization(), 100.0), "gpu_d", "dgpu");
		registerNumber(_->(int) (Math.min(ins.getGpuUtilization(), 100.0)), "gpu");
		registerNumber(_->Misc.CPU.get()* 100d, "cpu_d", "dcpu");
		registerNumber(_->(int) (Misc.CPU.get()* 100d), "cpu");
		
		registerNumber(_->Misc.delta, "delta");
	}
	
	public static void registerMemoryVariables() {
		registerNumber(_->runtime.maxMemory() / MB, "totalmemory","maxmemory","totalram","maxram");
		registerNumber(_->(runtime.totalMemory() - runtime.freeMemory()) / MB, "usedmemory","usedram");
		registerNumber(_->runtime.freeMemory() / MB, "freememory","freeram");
		registerNumber(_->runtime.freeMemory() / runtime.maxMemory(), "freememory_percentage","freeram_percentage");
		registerNumber(_->{
			double usedmem = ((double)runtime.totalMemory() - (double)runtime.freeMemory()) / MB;
			double totalmem = (runtime.maxMemory())/MB;
			return (int)(usedmem/totalmem*100);
		}, "usedmemory_percentage","usedram_percentage");
	}
	
	public static void registerTimeVariables() {
		registerNumber(_->System.currentTimeMillis(), "time");
		registerNumber(_->Calendar.getInstance().get(Calendar.MILLISECOND), "milliseconds");
		registerNumber(_->Calendar.getInstance().get(Calendar.SECOND), "seconds");
		registerNumber(_->Calendar.getInstance().get(Calendar.MINUTE), "minutes");
		registerNumber(_->Calendar.getInstance().get(Calendar.HOUR_OF_DAY), "hour");
		registerNumber(_->Calendar.getInstance().get(Calendar.DAY_OF_MONTH), "day");
		registerNumber(_->Calendar.getInstance().get(Calendar.MONTH)+1, "month");
		registerNumber(_->Calendar.getInstance().get(Calendar.YEAR), "year");
		
		registerString(_->Calendar.getInstance().getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault()), "month_name");
		registerString(_->clockify(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % 12),"hour12");
		registerString(_->Calendar.getInstance().get(Calendar.HOUR_OF_DAY) / 12 == 1 ? "pm" : "am", "ampm");
	}
	
	public static void registerStringComputerInfo() {
		registerString(_->{
			CentralProcessor processor = new SystemInfo().getHardware().getProcessor();
			return String.format(Locale.ROOT, "%dx %s", processor.getLogicalProcessorCount(),
					processor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
		}, "cpu_info");
		registerString(_->Misc.OS, "operating_system");
		registerString(_->Locale.getDefault().getDisplayName(), "locale");
		registerString(_->Locale.getDefault().getLanguage(), "language");
		registerString(_->Locale.getDefault().getCountry(), "country");
	}
	
	private static String clockify(int time) {
		return String.valueOf(time < 10 ? "0" + time : time);
	}
}
