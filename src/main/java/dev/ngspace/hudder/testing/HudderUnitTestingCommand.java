package dev.ngspace.hudder.testing;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.config.HudderConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class HudderUnitTestingCommand implements ClientCommandRegistrationCallback {
	
	public HudderConfig config;
	public HudderTestsHandler handler;

    public HudderUnitTestingCommand(HudderConfig config) throws IOException {
		this.config = config;
		handler = new HudderTestsHandler(config, config.hudderV3Compiler);
		handler.test_providers.add(handler::loadDefaultTests);
		handler.test_providers.add(UnitTestsSuggestionProvider::updateSuggestions);
		handler.loadTests();
	}
	
	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(literal("hudderunittesting")
				
				.then(literal("test_all").executes(context -> {
					context.getSource().sendFeedback(handler.hudderTester.testAllAndReturnComponent());
					return 1;
				}))
				
				.then(literal("test").then(argument("name", StringArgumentType.greedyString())
						.suggests(new UnitTestsSuggestionProvider()).executes(context -> {
							String testname = StringArgumentType.getString(context, "name");
							context.getSource().sendFeedback(
									handler.hudderTester.test(testname).toText(testname));
							return 1;
						})))
				
				.then(literal("reload_tests").executes(context -> {
					try {
						handler.hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
						handler.loadTests();
						context.getSource().sendFeedback(
								Component.literal("Succesfully reloaded tests").withColor(CommonColors.GREEN));
					} catch (Exception e) {
						Hudder.error("Could not load unit tests");
						e.printStackTrace();
						context.getSource().sendFeedback(
								Component.literal("Could not reload unit tests").withColor(CommonColors.RED));
					}
					return 1;
				}))
				
				.then(literal("reload_and_test_all").executes(context -> {
					try {
						handler.hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
						handler.loadTests();
						context.getSource().sendFeedback(
								Component.literal("Succesfully reloaded tests").withColor(CommonColors.GREEN));
						context.getSource().sendFeedback(handler.hudderTester.testAllAndReturnComponent());
					} catch (Exception e) {
						Hudder.error("Could not load unit tests");
						e.printStackTrace();
						context.getSource().sendFeedback(
								Component.literal("Could not reload unit tests").withColor(CommonColors.RED));
					}
					return 1;
				})));
	}

	public static class UnitTestsSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
		public static List<String> suggestions;
		
		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context,
				SuggestionsBuilder builder) throws CommandSyntaxException {
			List<String> suggestionscopy = new ArrayList<String>(suggestions);
			for (int i = 0; i < suggestionscopy.size(); i++) {
				String suggestion = suggestionscopy.get(i);
				if (suggestion.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
					builder.suggest(suggestion);
			}
			return builder.buildFuture();
		}
		public static void updateSuggestions(HudderUnitTester tester) {
		    suggestions = new ArrayList<String>(tester.UnitTests.keySet());
		}
	}
	
	
}
