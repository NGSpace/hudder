package dev.ngspace.hudder;

import java.util.List;

import dev.ngspace.hudder.tests_mixins.ChatComponentAccessor;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.multiplayer.chat.GuiMessage;

// This is one is so overly complicated but idrc
// Also this marks the third reuse of the HudderTestsHandler.
// The in game command, the regular JUnit tests and now this.
public class HudderUnitTestsRunner implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
		try (var _ = context.worldBuilder().create()) {
			int before = context.computeOnClient(
					client -> ((ChatComponentAccessor) client.gui.hud.getChat()).getMessages().size());
			context.runOnClient(ins->{
				Hudder.config.setCompilerName("hudderv3");
				ins.getConnection().sendCommand("hudderunittesting reload_and_test_all");
    		});

			boolean failed = context.computeOnClient(client -> {
			    List<GuiMessage> messages =
			        ((ChatComponentAccessor) client.gui.hud.getChat()).getMessages();
			    return messages.subList(0, messages.size() - before)
			        .stream()
			        .anyMatch(msg->msg.content().toString().contains("Failed the following tests:"));
			});
			
			if (failed) {
				throw new AssertionError("Failed /hudderunittesting reload_and_test_all");
			}
        }
        
    }
}