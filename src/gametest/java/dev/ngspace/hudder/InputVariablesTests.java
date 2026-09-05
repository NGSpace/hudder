package dev.ngspace.hudder;

import static dev.ngspace.hudder.DataVariableRegistryTester.testBoolean;

import org.lwjgl.sdl.SDLMouse;
import org.lwjgl.sdl.SDLScancode;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

public class InputVariablesTests implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
		try (var _ = context.worldBuilder().create()) {
            context.getInput().holdKey(SDLScancode.SDL_SCANCODE_C);
            testBoolean("key_c", true);
    		
            context.getInput().holdKey(SDLScancode.SDL_SCANCODE_TAB);
            testBoolean("key_tab", true);
    		
            context.getInput().holdMouse(SDLMouse.SDL_BUTTON_LEFT);
            testBoolean("mouse_left", true);
    		
            context.getInput().holdMouse(SDLMouse.SDL_BUTTON_RIGHT);
            testBoolean("mouse_right", true);
        }
        
    }
}