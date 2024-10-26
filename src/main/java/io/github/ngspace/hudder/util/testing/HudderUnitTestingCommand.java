package io.github.ngspace.hudder.util.testing;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.FileInputStream;
import java.util.HashMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class HudderUnitTestingCommand implements ClientCommandRegistrationCallback {
	
	public HudderUnitTestingCommand() {
		try {
			Hudder.config.hudderTester.load(getClass().getResourceAsStream(HudFileUtils.ASSETS + "UnitTests.hudder"));
		} catch (Exception e) {
			Hudder.error("Could not load unit tests");
			e.printStackTrace();
		}
	}

	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
			CommandRegistryAccess registryAccess) {

		dispatcher.register(literal("huddertesting")
			.then(literal("test_all").executes(context -> {
				context.getSource().sendFeedback(Hudder.config.hudderTester.testAll(Hudder.config));
				return 1;
			}))
			.then(literal("test").then(argument("testname",StringArgumentType.greedyString())
			.suggests(new HudderUnitTestsSuggestionProvider()).executes(context -> {
				String testname = StringArgumentType.getString(context, "testname");
				context.getSource().sendFeedback(Hudder.config.hudderTester.test(Hudder.config,testname).toText(testname));
				return 1;
			})))
			.then(literal("reload_tests").executes(context -> {
				try {
					Hudder.config.hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
					Hudder.config.hudderTester.load(getClass().getResourceAsStream(HudFileUtils.ASSETS + "UnitTests.hudder"));
					context.getSource().sendFeedback(
							Text.literal("Succesfully reloaded tests").withColor(Colors.GREEN));
				} catch (Exception e) {
					Hudder.error("Could not load unit tests");
					e.printStackTrace();
					context.getSource().sendFeedback(
							Text.literal("Could not reload unit tests").withColor(Colors.RED));
				}
				return 1;
			}))
			.then(literal("load_tests").then(argument("path",StringArgumentType.string()).executes(context -> {
				try {
					Hudder.config.hudderTester.load(new FileInputStream(StringArgumentType.getString(context, "path")));
					context.getSource().sendFeedback(
							Text.literal("Succesfully loaded tests").withColor(Colors.GREEN));
				} catch (Exception e) {
					Hudder.error("Could not load unit tests");
					e.printStackTrace();
					context.getSource().sendFeedback(
							Text.literal("Could not load unit tests").withColor(Colors.RED));
				}
				return 1;
			}))
		));
	}
	
}
