package dev.ngspace.hudder;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.HudderBuiltInFunctions;
import dev.ngspace.hudder.api.functionsandconsumers.HudderBuiltInMethods;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.testing.HudderUnitTestingCommand;
import dev.ngspace.hudder.utils.HudFileUtils;

class HudderUnitTests {
	
	@TempDir
	static Path tempDir;
	
	@Test
	void test() throws IOException {

		HudFileUtils.FABRIC_CONFIG_FOLDER = tempDir.toString();
		HudFileUtils.FOLDER = HudFileUtils.FABRIC_CONFIG_FOLDER + File.separator + "hudder" + File.separator;
		
		HudderConfig config = new HudderConfig(tempDir.resolve("hudder-test.json").toFile());
		HudFileUtils.makeDefaultHud();
		HudderV3Helper.config = config;
		HudderUnitTestingCommand command = new HudderUnitTestingCommand(config);
		command.hudderTester.compiler = Compilers.hudderV3Compiler;

		HudderBuiltInFunctions.registerFunction(FunctionAndConsumerAPI.getInstance());
		HudderBuiltInMethods.registerMethods(FunctionAndConsumerAPI.getInstance());
		
		var tests = command.hudderTester.testAll(config);
		for (var test : tests.entrySet()) {
			System.out.println(test.getKey() + " $ " + test.getValue().filename());
			if (!test.getValue().isSucessful()) {
				System.out.println(test.getValue().getFailureMessage().getString());
				fail(test.getValue().getFailureMessage().getString());
			}
		}
	}
}
