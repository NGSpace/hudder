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
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.api.variableregistry.VariableTypes;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.ValueGetter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class HudderUnitTestingCommand implements ClientCommandRegistrationCallback {
	
	public static final String TESTS_FOLDER = HudFileUtils.ASSETS + "tests/";
    private HudderUnitTester hudderTester = new HudderUnitTester(Compilers.hudderV3Compiler);
    public static List<TestProvider> test_providers = new ArrayList<TestProvider>();
    
    @FunctionalInterface
    public static interface TestProvider {
    	public void addTests(HudderUnitTester tester) throws IOException;
    }
    
    private static final String[] tests = {
		"arithmetic_and_math.hud",
		"booleans_and_conditions.hud",
		"strings_and_text.hud",
		"arrays_and_collections.hud",
		"variables_and_types.hud",
		"control_flow.hud",
		"functions_and_methods.hud",
		"java_and_external_apis.hud",
		"misc.hud"
	};
    static {
		test_providers.add(e->{
			for (String test : tests) {
				boolean shouldTest = false;
				for (String format : e.compiler.getSupportedFileFormats()) {
					if (test.endsWith('.' + format)) {
						shouldTest = true;
					}
				}
				if (shouldTest)
					e.loadModern(Hudder.class.getResourceAsStream(TESTS_FOLDER + test), test);
			}
		});
    }
	
	public HudderUnitTestingCommand() {
		try {
			loadTests();
		} catch (Exception e) {
			Hudder.error("Could not load unit tests");
			e.printStackTrace();
		}
	}
	
	@Override
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		
		FunctionAndConsumerAPI.getInstance().registerPositionedFunction((_,_,_,a) -> a[0].get(), "FunctionAPITestingFunction");
		FunctionAndConsumerAPI.getInstance().registerPositionedConsumer(
				(_,co,_,a) -> ((AVarTextCompiler) co).put("methodvalue", a[0].get()), "MethodAPITestingMethod");
		
		DataVariableRegistry.registerVariable(_ -> new JavaTestObject(), "JavaObjectAccess");
		DataVariableRegistry.registerVariable(_ -> new JavaTestNoAccess(), "JavaTestNoAccess");
		
		DataVariableRegistry.registerVariable(_ -> "Value", VariableTypes.STRING, "string_var");
		DataVariableRegistry.registerVariable(k -> k, VariableTypes.STRING, "string_var2");
		DataVariableRegistry.registerVariable(_ -> 69, VariableTypes.NUMBER, "number_var");
		DataVariableRegistry.registerVariable(_ -> true, VariableTypes.BOOLEAN, "boolean_var");
		DataVariableRegistry.registerVariable(_ -> new Object() {
			@Override
			public String toString() {
				return "lol";
			}
		}, VariableTypes.OBJECT, "object_var");
		
		DataVariableRegistry.registerVariable(_ -> ((ValueGetter) k->k),
				VariableTypes.OBJECT, "value_getter");
		
		dispatcher.register(literal("hudderunittesting")
				
				.then(literal("test_all").executes(context -> {
					context.getSource().sendFeedback(hudderTester.testAll(Hudder.config));
					return 1;
				}))
				
				.then(literal("test").then(argument("name", StringArgumentType.greedyString())
						.suggests(new UnitTestsSuggestionProvider()).executes(context -> {
							String testname = StringArgumentType.getString(context, "name");
							context.getSource().sendFeedback(
									hudderTester.test(Hudder.config, testname).toText(testname));
							return 1;
						})))
				
				.then(literal("reload_tests").executes(context -> {
					try {
						hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
						loadTests();
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
						hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
						loadTests();
						context.getSource().sendFeedback(
								Component.literal("Succesfully reloaded tests").withColor(CommonColors.GREEN));
						context.getSource().sendFeedback(hudderTester.testAll(Hudder.config));
					} catch (Exception e) {
						Hudder.error("Could not load unit tests");
						e.printStackTrace();
						context.getSource().sendFeedback(
								Component.literal("Could not reload unit tests").withColor(CommonColors.RED));
					}
					return 1;
				})));
	}
	
	private void loadTests() throws IOException {
		if (Hudder.config.getCompiler() instanceof AVarTextCompiler comp)
			hudderTester.compiler = comp;
		hudderTester.UnitTests.clear();
		for (TestProvider provider : test_providers)
			provider.addTests(hudderTester);
		hudderTester.updateSuggestions();
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
	}
}
