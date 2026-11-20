package dev.ngspace.hudder.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.testing.types.JavaTestInheritedNoAccess;
import dev.ngspace.hudder.testing.types.JavaTestNoAccess;
import dev.ngspace.hudder.testing.types.JavaTestObject;
import dev.ngspace.hudder.testing.types.JavaTestPartialNoAccess;
import dev.ngspace.hudder.testing.types.WrapperTest;

public class HudderTestsHandler {
	
	public static final String TESTS_FOLDER = "/assets/hudder/tests/";
	
    public List<TestProvider> test_providers = new ArrayList<TestProvider>();
	public HudderUnitTester hudderTester;
	public HudderConfig config;
	
	public HudderTestsHandler(HudderConfig config, AHudCompiler<?> compiler) throws IOException {
		this.config = config;
		this.hudderTester = new HudderUnitTester(compiler);
		loadTests();
	}
	
	public void loadTests() throws IOException {
		registerApis();
		if (config.getCompiler() instanceof AHudCompiler<?> comp)
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
		DataVariableRegistry.registerObjectVariable(_ -> new JavaTestInheritedNoAccess(), "JavaTestInheritedNoAccess");
		DataVariableRegistry.registerObjectVariable(_ -> new JavaTestPartialNoAccess(), "JavaTestPartialNoAccess");
		
		DataVariableRegistry.registerStringVariable(_ -> "Value", "string_var");
		DataVariableRegistry.registerStringVariable(k -> k, "string_var2");
		DataVariableRegistry.registerNumberVariable(_ -> 69, "number_var");
		DataVariableRegistry.registerBooleanVariable(_ -> true, "boolean_var");
		DataVariableRegistry.registerObjectVariable(_ -> new Object() {
			@Override public String toString() {return "lol";}
		}, "object_var");
		
		DataVariableRegistry.registerObjectVariable(_->new WrapperTest.WrapperTestWrapper(new WrapperTest()), "value_getter");
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
		"misc.hud",
		"js/js_tests.js"
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
