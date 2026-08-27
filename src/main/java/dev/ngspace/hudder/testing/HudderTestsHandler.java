package dev.ngspace.hudder.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.ValueGetter;

public class HudderTestsHandler {
	
	public static final String TESTS_FOLDER = HudFileUtils.ASSETS + "tests/";
	
    public List<TestProvider> test_providers = new ArrayList<TestProvider>();
	public HudderUnitTester hudderTester;
	public HudderConfig config;
	
	public HudderTestsHandler(HudderConfig config, AVarTextCompiler compiler) throws IOException {
		this.config = config;
		this.hudderTester = new HudderUnitTester(compiler);
		loadTests();
	}
	
	public void loadTests() throws IOException {
		registerApis();
		if (config.getCompiler() instanceof AVarTextCompiler comp)
			hudderTester.compiler = comp;
		hudderTester.UnitTests.clear();
		for (TestProvider provider : test_providers)
			provider.addTests(hudderTester);
	}
	
	public void registerApis() {
		FunctionAndConsumerAPI.getInstance().registerPositionedFunction((_,_,_,_,a) -> a[0].get(), "FunctionAPITestingFunction");
		FunctionAndConsumerAPI.getInstance().registerPositionedConsumer(
				(_,_,p,_,a) -> {throw new ExecutionException(a[0].asString(), p);}, "MethodAPITestingMethod");
		
		DataVariableRegistry.registerObjectVariable(_ -> new JavaTestObject(), "JavaObjectAccess");
		DataVariableRegistry.registerObjectVariable(_ -> new JavaTestNoAccess(), "JavaTestNoAccess");
		
		DataVariableRegistry.registerStringVariable(_ -> "Value", "string_var");
		DataVariableRegistry.registerStringVariable(k -> k, "string_var2");
		DataVariableRegistry.registerNumberVariable(_ -> 69, "number_var");
		DataVariableRegistry.registerBooleanVariable(_ -> true, "boolean_var");
		DataVariableRegistry.registerObjectVariable(_ -> new Object() {
			@Override public String toString() {return "lol";}
		}, "object_var");
		
		DataVariableRegistry.registerObjectVariable(_ -> ((ValueGetter) k->k), "value_getter");
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

	public void loadDefaultTests(HudderUnitTester e) throws IOException {
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
	}
    
    @FunctionalInterface
    public static interface TestProvider {
    	public void addTests(HudderUnitTester tester) throws IOException;
    }
}
