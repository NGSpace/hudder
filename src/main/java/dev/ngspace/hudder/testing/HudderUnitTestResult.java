package dev.ngspace.hudder.testing;

import dev.ngspace.hudder.Hudder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record HudderUnitTestResult(boolean isSucessful, String expectation, String result, String filename) {
	
	public MutableComponent toText(String name) {
		var message = Component.literal(name + (!isSucessful?" @ "+filename:"") + ": ").withColor(0x0fa1fc)
				.append(Component.literal((isSucessful?"Passed":"Failed")).withColor(isSucessful?0x0fff3f:0xff0000));
		Hudder.log("Test name: "+name);
		Hudder.log("Expectation: "+expectation);
		Hudder.log("Result: "+result);
		Hudder.log("Success: "+isSucessful);
		Hudder.log("");
		if (!isSucessful) message.append(getFailureMessage());
		return message;
	}
	
	public MutableComponent getFailureMessage() {
		var message = Component.literal("");
		message.append(Component.literal("\n  Expected:\n").withColor(0x000cff));
		message.append(Component.literal("    " + expectation.replace("\n", "\n    ")).withColor(0xffffff));
		message.append(Component.literal("\n  Got:\n").withColor(0x000cff));
		message.append(Component.literal("    " + result.replace("\n", "\n    ")).withColor(0xffffff));
		return message;
	}
}
