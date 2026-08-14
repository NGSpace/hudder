package dev.ngspace.hudder.testing;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.testing.HudderTestReader.Result;
import dev.ngspace.hudder.testing.HudderUnitTest.Mode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HudderUnitTester {
	public AVarTextCompiler compiler;
	public Map<String, HudderUnitTest> UnitTests = new HashMap<String, HudderUnitTest>();
	
	public HudderUnitTester(AVarTextCompiler compiler) {this.compiler=compiler;}
	
	public void loadModern(InputStream inputStream, String filename) throws IOException {
		loadModern(IOUtils.toString(inputStream, UTF_8), filename);
	}
	
	public void loadModern(String contents, String filename) {
	    String[] conds = contents.split("\\|TEST:");
	    for (String st : conds) {
	    	if (st.isBlank()) continue;
	    	Result result = HudderTestReader.process(st);
	    	if (UnitTests.containsKey(result.getString("name")))
	    		Hudder.alert("Repeating key: " + result.getString("name"));
	    	UnitTests.put(result.getString("name"), new HudderUnitTest(result.input(), result.output(),
	    			filename, result.metadata(), Mode.valueOf(result.getString("mode").toUpperCase())));
	    }
	}
	
	
	public void updateSuggestions() {
	    HudderUnitTestingCommand.UnitTestsSuggestionProvider.suggestions = new ArrayList<String>(UnitTests.keySet());
	}
	
	
	
	public HudderUnitTestResult test(HudderConfig info, String name) {
		return UnitTests.get(name).test(compiler,info);
	}
	
	
	
	public Map<String, HudderUnitTestResult> testAll(HudderConfig config) {
		Map<String, HudderUnitTestResult> results = new HashMap<String, HudderUnitTestResult>();
		for (var test : UnitTests.entrySet()) {
			results.put(test.getKey(), test(config, test.getKey()));
		}
		return results;
	}
	
	public MutableComponent testAllAndReturnComponent(HudderConfig config) {
		MutableComponent result = Component.literal("All tests:\n");
		boolean failed = false;
		Instant start = Instant.now();
		Map<HudderUnitTestResult,String> failedtests = new HashMap<HudderUnitTestResult, String>();
		for (String name : UnitTests.keySet()) {
			var testresult = test(config, name);
			result.append("\n").append(testresult.toText(name));
			if (!testresult.isSucessful()) {
				failed = true;
				failedtests.put(testresult, name);
			}
		}
		Instant end = Instant.now();
		if (failed) {
			result.append(Component.literal("\n\nFailed the following tests: ").withColor(0xff0000));
			for (var failedtest : failedtests.entrySet()) {
				result.append("\n\n");
				result.append(Component.literal(failedtest.getValue())
						.withColor(0xff0000));
				result.append(Component.literal(" @ ")
						.withColor(0x0fa1fc));
				result.append(Component.literal(failedtest.getKey().filename())
						.withColor(0xFFFFFF));
				result.append(":");
				result.append(failedtest.getKey().getFailureMessage());
			}
		}
		result.append(Component.literal(output(!failed, start, end, failedtests.size(), UnitTests.size())));
		return result;
	}

	protected String output(boolean success, Instant start, Instant end, int failedcount, int testscount) {
		double v = Duration.between(start, end).toNanos()/1000000d;
		double res = (int) (v*1000);
		res/=1000;
		return "\n\n" + (success? "Successful, " : "") + "took "+ res + "ms. Passed "
				+ (testscount-failedcount) + "/" + testscount + " tests using " + Compilers.getNameFromCompiler(compiler);
	}
}