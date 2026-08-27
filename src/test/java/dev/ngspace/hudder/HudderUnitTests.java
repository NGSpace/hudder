package dev.ngspace.hudder;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.HudderBuiltInFunctions;
import dev.ngspace.hudder.api.functionsandconsumers.HudderBuiltInMethods;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.testing.HudderTestsHandler;
import dev.ngspace.hudder.testing.HudderUnitTest;
import dev.ngspace.hudder.testing.HudderUnitTestResult;
import dev.ngspace.hudder.utils.HudFileUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HudderUnitTests {
	
	@TempDir
	static Path tempDir;
	HudderTestsHandler handler;
	HudderConfig config;

	@BeforeAll
	void prepareHudder() throws IOException {
		HudFileUtils.FABRIC_CONFIG_FOLDER = tempDir.toString();
		HudFileUtils.FOLDER = HudFileUtils.FABRIC_CONFIG_FOLDER + File.separator + "hudder" + File.separator;
		
		config = new HudderConfig(tempDir.resolve("hudder-test.json").toFile());
		HudFileUtils.makeDefaultHud();
		handler = new HudderTestsHandler(config, config.hudderV3Compiler);
		handler.test_providers.add(handler::loadDefaultTests);
		handler.loadTests();

		HudderBuiltInFunctions.registerFunction(FunctionAndConsumerAPI.getInstance());
		HudderBuiltInMethods.registerMethods(FunctionAndConsumerAPI.getInstance());
	}
	
	Stream<Arguments> testCases() {
	    return handler.hudderTester.UnitTests.entrySet().stream()
	    		.map(e->Arguments.of(e.getKey(), e.getValue().filename, e.getValue()));
	}
	
	@ParameterizedTest
	@MethodSource("testCases")
	void individualTest(String name, String filename, HudderUnitTest test) {
		System.out.println(name + " $ " + filename);
		HudderUnitTestResult result = test.test(config.hudderV3Compiler);
		if (!result.isSucessful()) {
			System.out.println(result.getFailureMessage().toString());
			fail();
		}
	}
}
