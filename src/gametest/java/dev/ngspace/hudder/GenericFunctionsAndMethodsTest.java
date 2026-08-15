package dev.ngspace.hudder;

import static dev.ngspace.hudder.FunctionAndConsumerAPITester.runConsumer;
import static dev.ngspace.hudder.FunctionAndConsumerAPITester.testFunction;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class GenericFunctionsAndMethodsTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getClientLevel().waitForChunksRender();
            
            runConsumer("writeValue", "test_key", 10);
            testFunction("readValue", (t, _) -> ((Number)t).intValue()==10, "test_key");
        }
        
    }
}