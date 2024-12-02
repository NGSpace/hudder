package io.github.ngspace.hudder.utils.testing;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.FileInputStream;
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

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.utils.HudFileUtils;
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

	@Override public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

		dispatcher.register(literal("hudderunittesting")
				
				
			.then(literal("test_all").executes(context -> {
				context.getSource().sendFeedback(Hudder.config.hudderTester.testAll(Hudder.config));
				return 1;
			}))
			
			
			.then(literal("test").then(argument("name",StringArgumentType.greedyString())
			.suggests(new UnitTestsSuggestionProvider()).executes(context -> {
				String testname = StringArgumentType.getString(context, "name");
				context.getSource().sendFeedback(Hudder.config.hudderTester.test(Hudder.config,testname).toText(testname));
				return 1;
			})))
			
			
			.then(literal("reload_tests").executes(context -> {
				try {
					Hudder.config.hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
					Hudder.config.hudderTester.load(getClass().getResourceAsStream(HudFileUtils.ASSETS + "UnitTests.hudder"));
					context.getSource().sendFeedback(Text.literal("Succesfully reloaded tests").withColor(Colors.GREEN));
				} catch (Exception e) {
					Hudder.error("Could not load unit tests");
					e.printStackTrace();
					context.getSource().sendFeedback(Text.literal("Could not reload unit tests").withColor(Colors.RED));
				}
				return 1;
			}))
			
			
			.then(literal("reload_and_test_all").executes(context -> {
				try {
					Hudder.config.hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
					Hudder.config.hudderTester.load(getClass().getResourceAsStream(HudFileUtils.ASSETS + "UnitTests.hudder"));
					context.getSource().sendFeedback(Text.literal("Succesfully reloaded tests").withColor(Colors.GREEN));
					context.getSource().sendFeedback(Hudder.config.hudderTester.testAll(Hudder.config));
				} catch (Exception e) {
					Hudder.error("Could not load unit tests");
					e.printStackTrace();
					context.getSource().sendFeedback(Text.literal("Could not reload unit tests").withColor(Colors.RED));
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
					context.getSource().sendFeedback(Text.literal("Could not load unit tests").withColor(Colors.RED));
				}
				return 1;
			}))
		));
	}
	
	
	class UnitTestsSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
		public static List<String> suggestions;
		
		@Override public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context,
				SuggestionsBuilder builder) throws CommandSyntaxException {
			List<String> suggestionscopy = new ArrayList<String>(suggestions);
			for (int i = 0;i<suggestionscopy.size();i++) {
				String suggestion = suggestionscopy.get(i);
				if (suggestion.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) builder.suggest(suggestion);
			}
			return builder.buildFuture();
		}
	}
}
