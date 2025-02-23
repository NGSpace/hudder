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
import io.github.ngspace.hudder.compilers.utils.functionandmethodapi.FunctionAndMethodAPI;
import io.github.ngspace.hudder.data_management.ObjectDataAPI;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class HudderUnitTestingCommand implements ClientCommandRegistrationCallback {
	
	public HudderUnitTestingCommand() {
		try {
			Hudder.config.hudderTester.load(getClass().getResourceAsStream(HudFileUtils.ASSETS + "UnitTests.hudder"));
		} catch (Exception e) {
			Hudder.error("Could not load unit tests");
			e.printStackTrace();
		}
	}

	@Override public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {

		FunctionAndMethodAPI.getInstance().registerFunction((m,co,a)-> a[0].get(), "FunctionAPITestingFunction");
		FunctionAndMethodAPI.getInstance().registerConsumer((m,co,a)-> co.put("methodvalue",a[0].get()), "MethodAPITestingMethod");
		
		ObjectDataAPI.addObjectGetter(key->!key.equalsIgnoreCase("ObjectDataAPITestVariable") ? null : "Oh jeepers, you found me. Good job, I'm glad you found me, because I have something kind of important to say. beep noise It's about th-the game..Don't, beep noise Uh, Eh. Don't beep noise Don't, just... This is.. This is probably looking pretty ridiculous beep noise Don't tell anyone about this game. You gonna.. Don't, don't bring attention to yourself. Destroy it, destroy the game. Destroy the game. Before, they see it. beep noise What i'm saying is.. is get out of this, while you still can. beep noise Just, don't.. don't know that you probably know i'm not saying that i'm trapped inside the game, no that would be ridiculous. No i'm.. beep noise I can't..this is..i'm not..the game is..kind of..beep noise I got really corrupted. Yeah, I.. beep noise I don't know what to say. Just..Just trust me. We gonna.. beep noise This isn't..This seems..I me-i mean it seems...ohh. *beep noise They'd know I.. They intentionally..that's..I guess..unintelligible They can't tell you, and some...stuff is classified. I can't say it. beep noise I wish I could say more. I can't talk normally. I-it's corrupted. There's..beep noise..Yeah..beep noise Just.. close the program. Destroy it. Never come back. long beep noise");
		
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
					context.getSource().sendFeedback(Component.literal("Succesfully reloaded tests").withColor(CommonColors.GREEN));
				} catch (Exception e) {
					Hudder.error("Could not load unit tests");
					e.printStackTrace();
					context.getSource().sendFeedback(Component.literal("Could not reload unit tests").withColor(CommonColors.RED));
				}
				return 1;
			}))
			
			
			.then(literal("reload_and_test_all").executes(context -> {
				try {
					Hudder.config.hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
					Hudder.config.hudderTester.load(getClass().getResourceAsStream(HudFileUtils.ASSETS + "UnitTests.hudder"));
					context.getSource().sendFeedback(Component.literal("Succesfully reloaded tests").withColor(CommonColors.GREEN));
					context.getSource().sendFeedback(Hudder.config.hudderTester.testAll(Hudder.config));
				} catch (Exception e) {
					Hudder.error("Could not load unit tests");
					e.printStackTrace();
					context.getSource().sendFeedback(Component.literal("Could not reload unit tests").withColor(CommonColors.RED));
				}
				return 1;
			}))
			
			
			
			.then(literal("load_tests").then(argument("path",StringArgumentType.string()).executes(context -> {
				try {
					Hudder.config.hudderTester.load(new FileInputStream(StringArgumentType.getString(context, "path")));
					context.getSource().sendFeedback(
							Component.literal("Succesfully loaded tests").withColor(CommonColors.GREEN));
				} catch (Exception e) {
					Hudder.error("Could not load unit tests");
					e.printStackTrace();
					context.getSource().sendFeedback(Component.literal("Could not load unit tests").withColor(CommonColors.RED));
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
