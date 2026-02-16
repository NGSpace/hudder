package dev.ngspace.hudder.utils.testing;

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

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.api.variableregistry.VariableTypes;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI;
import dev.ngspace.hudder.utils.HudFileUtils;
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

		FunctionAndConsumerAPI.getInstance().registerFunction((m,co,a)-> a[0].get(), "FunctionAPITestingFunction");
		FunctionAndConsumerAPI.getInstance().registerConsumer((m,co,a)-> co.put("methodvalue",a[0].get()), "MethodAPITestingMethod");

		DataVariableRegistry.registerVariable(k->new JavaTestObject(), "JavaObjectAccess");
		DataVariableRegistry.registerVariable(k->new JavaTestNoAccess(), "JavaTestNoAccess");

		DataVariableRegistry.registerVariable(k->"Value", VariableTypes.STRING, "string_var");
		DataVariableRegistry.registerVariable(k->k, VariableTypes.STRING, "string_var2");
		DataVariableRegistry.registerVariable(k->69, VariableTypes.NUMBER, "number_var");
		DataVariableRegistry.registerVariable(k->true, VariableTypes.BOOLEAN, "boolean_var");
		DataVariableRegistry.registerVariable(k->new Object() {
			@Override
			public String toString() {
				return "lol";
			}
		}, VariableTypes.OBJECT, "object_var");
		
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
