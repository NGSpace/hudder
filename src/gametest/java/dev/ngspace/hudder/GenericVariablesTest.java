package dev.ngspace.hudder;

import static dev.ngspace.hudder.DataVariableRegistryTester.testString;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class GenericVariablesTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getClientLevel().waitForChunksRender();
            
            testString("username", "Player0");
            context.getInput().pressKey(GLFW.GLFW_KEY_E);// Open the inventory
            testString("openguitype", "Survival Mode Inventory");
            context.getInput().pressKey(GLFW.GLFW_KEY_E);// Close the inventory
        }
        
    }
}
