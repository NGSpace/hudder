package dev.ngspace.hudder.data_management;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.sun.management.OperatingSystemMXBean;

import dev.ngspace.hudder.data_management.api.DataVariableRegistry;
import dev.ngspace.hudder.data_management.api.VariableTypes;
import dev.ngspace.hudder.v2runtime.V2Runtime;
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

	public static int fps = 0;
	public static List<Long> clicks_left = new ArrayList<Long>();
	public static List<Long> cps_right = new ArrayList<Long>();

	public static double gpuUsage = 0;
	public static float delta = 1;
	public static LimitedRefreshSpeedData<Double> CPU = new LimitedRefreshSpeedData<Double>(
			((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())::getProcessCpuLoad, 2000);
	
	public static String OS = getOS();

	static String getOS() {
		String OS = System.getProperty("os.name", "generic").toLowerCase();
		if ((OS.contains("mac")) || (OS.contains("darwin"))) OS = "mac";
		else if (OS.contains("nux")) OS = "linux";
		else if (OS.contains("win")) OS = "windows";
		else OS = "other";
		return OS;
	}

	static double oldcps_l = 0;
	static double newcps_l = 0;
	static double oldcps_r = 0;
	static double newcps_r = 0;
	
    public static void updateCPS() {
        long now = System.currentTimeMillis();
        clicks_left.removeIf(age -> age < now - 1000);
        oldcps_l = newcps_l;
        newcps_l = clicks_left.size();
        cps_right.removeIf(age -> age < now - 1000);
        oldcps_r = newcps_r;
        newcps_r = cps_right.size();
    }
    
    public static double getLeftCPS() {
    	return truncate(oldcps_l*.8 + newcps_l*.2, 3);
    }
    
    public static double getRightCPS() {
    	return truncate(oldcps_r*.8 + newcps_r*.2, 3);
    }
	
	
	
	/* FPS */
	
	
	
    private static final List<Integer> fpshistory = new ArrayList<Integer>();

    public static int getFPS(Minecraft ins) {
    	int fps = ins.getFps();
        fpshistory.add(fps);
        if (fpshistory.size()>200) fpshistory.remove(0);
        return fps;
    }
    public static int getMinimumFPS() {int max = fpshistory.get(0);for (int i:fpshistory) if (i<max) max=i;return max;}
    public static int getMaximumFPS() {int max = 0;for (int i:fpshistory) if (i>max) max=i;return max;}
    public static int getAverageFPS() {int sum = 0;for (int fps : fpshistory) sum+=fps;return sum/fpshistory.size();}
    
    
    
    /* Keyboard */
    
    
    
    public static final Map<Integer,Integer> held_keys = new HashMap<Integer,Integer>();
	
	public static void registerKeyVariables() {
		ArrayList<String> keyNames = new ArrayList<String>();
		HashMap<String, Integer> keys = new HashMap<String, Integer>();
		
	    for (Field field : GLFW.class.getFields()) {
	    	try {
	    		if (field.getName().startsWith("GLFW_KEY_")&&field.canAccess(null)) {
	    			String keyname = field.getName().substring(9).toLowerCase();
	    			keyNames.add("key_" + keyname);
	    			keys.put(keyname,field.getInt(null));
	    		}
			} catch (Exception e) {e.printStackTrace();}
	    }
	    
	    DataVariableRegistry.registerVariable(
	    		variable->held_keys.containsKey(keys.get(variable.substring(4).toLowerCase())),
	    		VariableTypes.BOOLEAN, keyNames.toArray(new String[keyNames.size()]));
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
	
	static double truncate(double num, int cutoff) {
		return Math.floor(num*Math.pow(10, cutoff))/Math.pow(10, cutoff);
	}
}