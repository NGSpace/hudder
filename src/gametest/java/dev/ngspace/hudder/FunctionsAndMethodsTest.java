package dev.ngspace.hudder;

import static dev.ngspace.hudder.FunctionAndConsumerAPITester.runConsumer;
import static dev.ngspace.hudder.FunctionAndConsumerAPITester.testFunction;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.TranslatedItemStack;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FunctionsAndMethodsTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
		try (var _ = context.worldBuilder().create()) {
            
            runConsumer("writeValue", "test_key", 10);
            testFunction("readValue", (t, _) -> ((Number)t).intValue()==10, "test_key");
            
            context.runOnClient(ins->ins.player.getInventory().add(new ItemStack(Items.DIAMOND, 2)));
            
            testFunction("getItem", (i,_)->{
            	if (i instanceof TranslatedItemStack stack) {
            		if (!"[Diamond]".equals(stack.get("name"))) return false;
            		if (2!=number(stack.get("count"))) return false;
            		if (64!=number(stack.get("maxcount"))) return false;
            		if (0!=number(stack.get("durability"))) return false;
            		if (0!=number(stack.get("maxdurability"))) return false;
            		if (!"minecraft:diamond".equals(stack.get("identifier"))) return false;
            		return true;
            	}
            	return false;
            }, 0);

            context.runOnClient(ins->ins.player.getInventory().add(6, new ItemStack(Items.IRON_AXE)));
            
            testFunction("getItem", (i,_)->{
            	if (i instanceof TranslatedItemStack stack) {
            		if (!"[Iron Axe]".equals(stack.get("name"))) return false;
            		if (1!=number(stack.get("count"))) return false;
            		if (1!=number(stack.get("maxcount"))) return false;
            		if (250!=number(stack.get("durability"))) return false;
            		if (250!=number(stack.get("maxdurability"))) return false;
            		if (!"minecraft:iron_axe".equals(stack.get("identifier"))) return false;
            		return true;
            	}
            	return false;
            }, 6);
		}
        
    }

	private double number(Object object) {
		if (object==null) throw new AssertionError("Null value");
		return ((Number)object).doubleValue();
	}
}