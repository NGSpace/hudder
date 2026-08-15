package dev.ngspace.hudder;

import static dev.ngspace.hudder.FunctionAndConsumerAPITester.runConsumer;
import static dev.ngspace.hudder.FunctionAndConsumerAPITester.testFunction;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.TranslatedItemStack;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class FunctionsAndMethodsTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitFor(ins->ins.player!=null);
			world.getServer().runCommand("give @e minecraft:diamond 2");
            
            runConsumer("writeValue", "test_key", 10);
            testFunction("readValue", (t, _) -> ((Number)t).intValue()==10, "test_key");
            
            testFunction("getItem", (i,_)->{
            	if (i instanceof TranslatedItemStack stack) {
            		if (!"Diamond".equals(stack.get("name"))) return false;
            		if (2!=number(stack.get("count"))) return false;
            		if (64!=number(stack.get("maxcount"))) return false;
            		if (1!=number(stack.get("durability"))) return false;
            		if (1!=number(stack.get("maxdurability"))) return false;
            		if (!"minecraft:diamond".equals(stack.get("identifier"))) return false;
            		return true;
            	}
            	return false;
            }, 0);
		}
        
    }

	private double number(Object object) {
		if (object==null) throw new AssertionError("Null value");
		return ((Number)object).doubleValue();
	}
}