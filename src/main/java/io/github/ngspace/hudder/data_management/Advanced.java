package io.github.ngspace.hudder.data_management;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;

public class Advanced {private Advanced() {}
	public static double gpuUsage = 0;
    private static final List<Integer> list = new ArrayList<>();

    public static int getFPS(MinecraftClient ins) {
    	int fps = ins.getCurrentFps();
        list.add(fps);
        if (list.size() > 800) list.remove(0);
        return fps;
    }
    public static int getMinimumFPS() {
    	int max = list.get(0);
    	for (int i:list) if (i<max) max=i;
    	return max;
    }
    public static int getMaximumFPS() {
    	int max = 0;
    	for (int i:list) if (i>max) max=i;
    	return max;
    }
    public static int getAverageFPS() {
        int sum = 0;
        for (int fps : list) sum += fps;
        return sum / list.size();
    }
}