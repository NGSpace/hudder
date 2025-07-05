package io.github.ngspace.hudder.data_management;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.sun.management.OperatingSystemMXBean;

import io.github.ngspace.hudder.v2runtime.V2Runtime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.gui.screens.reporting.ReportPlayerScreen;

public class Advanced {private Advanced() {}
	public static double gpuUsage = 0;
	public static float delta = 1;
	public static LimitedRefreshSpeedData<Double> CPU = new LimitedRefreshSpeedData<Double>(
			((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())::getProcessCpuLoad, 2000);
	
	
	public static String OS = null; static {
		if (OS == null) {
			String OS = System.getProperty("os.name", "generic").toLowerCase();
			if ((OS.contains("mac")) || (OS.contains("darwin"))) OS = "mac";
			else if (OS.contains("nux")) OS = "linux";
			else if (OS.contains("win")) OS = "windows";
			else OS = "other";
		}
	}
	
	
	
	/* FPS */
	
	
	
    private static final List<Integer> fpshistory = new ArrayList<Integer>();

    public static int getFPS(Minecraft ins) {
    	int fps = ins.getFps();
        fpshistory.add(fps);
        if (fpshistory.size()>800) fpshistory.remove(0);
        return fps;
    }
    public static int getMinimumFPS() {int max = fpshistory.get(0);for (int i:fpshistory) if (i<max) max=i;return max;}
    public static int getMaximumFPS() {int max = 0;for (int i:fpshistory) if (i>max) max=i;return max;}
    public static int getAverageFPS() {int sum = 0;for (int fps : fpshistory) sum+=fps;return sum/fpshistory.size();}
    
    
    
    /* Keyboard */
    
    
    
    public static final Map<Integer,Integer> keysheld = new HashMap<Integer,Integer>();

    //Kinda cheating?
    static HashMap<String, Integer> keys = new HashMap<String, Integer>(); static {
	    for (Field field : GLFW.class.getFields()) {
	    	try {
	    		if (field.getName().startsWith("GLFW_KEY_")&&field.canAccess(null))
	    			keys.put(field.getName().substring(9).toLowerCase(),field.getInt(null));
			} catch (Exception e) {e.printStackTrace();}
	    }
    }
	public static int isKeyHeld(String key) {
		if (key.length()>4&&key.length()<18&&key.startsWith("key_")) {
			int keynum = keys.get(key.substring(4));
			if (keynum==0) return 0;
			return keysheld.containsKey(keynum)?2:1;
		}
		return 0;
	}
	
	
	public static Object getScreenType(Screen screen) {
		return switch (screen) {
			case BlastFurnaceScreen scr: yield "Blast Furnace";
			case SmokerScreen scr: yield "Smoker";
			case AbstractFurnaceScreen<?> scr: yield "Furnace";
			case AbstractSignEditScreen scr: yield "Sign Edit";
			case AnvilScreen scr: yield "Anvil";
			case BeaconScreen scr: yield "Beacon";
			case BookEditScreen scr: yield "Book Edit";
			case BrewingStandScreen scr: yield "Brewing Stand";
			case CartographyTableScreen scr: yield "Cartography Table";
			case CrafterScreen scr: yield "Crafter";
			case CraftingScreen scr: yield "Crafting";
			case CreativeModeInventoryScreen scr: yield "Creative Mode Inventory";
			case EnchantmentScreen scr: yield "Enchantment";
			case ContainerScreen scr: yield "Container";
			case GrindstoneScreen scr: yield "Grindstone";
			case HopperScreen scr: yield "Hopper";
			case HorseInventoryScreen scr: yield "Horse Inventory";
			case LecternScreen scr: yield "Lectern";
			case LoomScreen scr: yield "Loom";
			case MerchantScreen scr: yield "Merchant";
			case ReportPlayerScreen scr: yield "Report Player";
			case ShulkerBoxScreen scr: yield "Shulker Box";
			case StonecutterScreen scr: yield "Stonecutter";
			case PauseScreen scr: yield "Pause";
			case ChatScreen scr: yield "Chat";
			case BookViewScreen scr: yield "Anvil";
			case AbstractContainerScreen<?> scr: yield "Generic container";
			
			
			case null:
				yield V2Runtime.NULL;
			default:
				yield screen.getClass().getSimpleName();
		};
	}
}