package dev.ngspace.hudder.variables.data;

import static dev.ngspace.hudder.api.variableregistry.VariableTypes.*;

import java.util.Calendar;
import java.util.Locale;

import com.mojang.blaze3d.platform.GLX;

import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import dev.ngspace.hudder.variables.advanced.Misc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;

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

		register(k->ins.getConnection().getConnection().getAverageSentPackets(), NUMBER, "sent_packets");
		register(k->ins.getConnection().getConnection().getAverageReceivedPackets(), NUMBER, "received_packets");
	}

	public static void registerPerformanceVariables() {
		register(k->Misc.fps, NUMBER, "fps");
		register(k->Misc.getAverageFPS(), NUMBER, "avgfps", "avg_fps");
		register(k->Misc.getMinimumFPS(), NUMBER, "minfps", "min_fps");
		register(k->Misc.getMaximumFPS(), NUMBER, "maxfps", "max_fps");
		
		register(k->{
			var playerInfo = ins.player.connection.getPlayerInfo(ins.player.getUUID());
			return playerInfo==null?0:playerInfo.getLatency();
		}, NUMBER, "ping");
		
		register(k->{
	        IntegratedServer server = ins.getSingleplayerServer();
	        return server == null ? -1f : server.tickRateManager().tickrate();
		}, NUMBER, "tps");
		
		register(k->Math.min(ins.getGpuUtilization(), 100.0), NUMBER, "gpu_d", "dgpu");
		register(k->(int) (Math.min(ins.getGpuUtilization(), 100.0)), NUMBER, "gpu");
		register(k->Misc.CPU.get()* 100d, NUMBER, "cpu_d", "dcpu");
		register(k->(int) (Misc.CPU.get()* 100d), NUMBER, "cpu");
		
		register(k->Misc.delta, NUMBER, "delta");
	}
	
	public static void registerMemoryVariables() {
		register(k->runtime.maxMemory() / MB, NUMBER, "totalmemory","maxmemory","totalram","maxram");
		register(k->(runtime.totalMemory() - runtime.freeMemory()) / MB, NUMBER, "usedmemory","usedram");
		register(k->runtime.freeMemory() / MB, NUMBER, "freememory","freeram");
		register(k->runtime.freeMemory() / runtime.maxMemory(), NUMBER, "freememory_percentage","freeram_percentage");
		register(k->{
			double usedmem = ((double)runtime.totalMemory() - (double)runtime.freeMemory()) / MB;
			double totalmem = (runtime.maxMemory())/MB;
			return (int)(usedmem/totalmem*100);
		}, NUMBER, "usedmemory_percentage","usedram_percentage");
	}
	
	public static void registerTimeVariables() {
		register(k->System.currentTimeMillis(), NUMBER, "time");
		register(k->Calendar.getInstance().get(Calendar.MILLISECOND), NUMBER, "milliseconds");
		register(k->Calendar.getInstance().get(Calendar.SECOND), NUMBER, "seconds");
		register(k->Calendar.getInstance().get(Calendar.MINUTE), NUMBER, "minutes");
		register(k->Calendar.getInstance().get(Calendar.HOUR_OF_DAY), NUMBER, "hour");
		register(k->Calendar.getInstance().get(Calendar.DAY_OF_MONTH), NUMBER, "day");
		register(k->Calendar.getInstance().get(Calendar.MONTH)+1, NUMBER, "month");
		register(k->Calendar.getInstance().get(Calendar.YEAR), NUMBER, "year");
		
		register(k->Calendar.getInstance().getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault()), STRING, "month_name");
		register(k->clockify(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % 12),STRING,"hour12");
		register(k->Calendar.getInstance().get(Calendar.HOUR_OF_DAY) / 12 == 1 ? "pm" : "am", STRING, "ampm");
	}
	
	public static void registerStringComputerInfo() {
		register(k->GLX._getCpuInfo(), STRING, "cpu_info");
		register(k->Misc.OS, STRING, "operating_system");
		register(k->Locale.getDefault().getDisplayName(), STRING, "locale");
		register(k->Locale.getDefault().getLanguage(), STRING, "language");
		register(k->Locale.getDefault().getCountry(), STRING, "country");
	}
	
	private static String clockify(int time) {
		return String.valueOf(time < 10 ? "0" + time : time);
	}
}
