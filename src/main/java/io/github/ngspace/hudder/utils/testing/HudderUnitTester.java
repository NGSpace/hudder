package io.github.ngspace.hudder.utils.testing;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.config.HudderConfig;
import io.github.ngspace.hudder.utils.testing.HudderUnitTest.HudderUnitTestResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HudderUnitTester {
	public ATextCompiler compiler;
	public Map<String, HudderUnitTest> UnitTests = new HashMap<String, HudderUnitTest>();
	
	public HudderUnitTester(ATextCompiler compiler) {this.compiler=compiler;}
	
	public void load(InputStream inputStream) throws IOException {load(IOUtils.toString(inputStream, UTF_8));}
	
	public void load(String contents) {
	    String[] conds = ("\n"+contents).split("\\n\\n\\|\\|INPUT\\|\\|");
	    for (String st : conds) {
	    	if (st.isBlank()) continue;
	    	String[] content = st.split("\n",2);
	    	String[] inputandExpectation = content[1].split("\\n\\|\\|EXPECT\\|\\|\\n");
	    	UnitTests.put(content[0], new HudderUnitTest(inputandExpectation[0], compiler, inputandExpectation[1]));
	    }
	    HudderUnitTestingCommand.UnitTestsSuggestionProvider.suggestions = new ArrayList<String>(UnitTests.keySet());
	}
	
	
	
	public HudderUnitTestResult test(HudderConfig info, String name) {return UnitTests.get(name).test(info);}
	
	
	
	public MutableComponent testAll(HudderConfig config) {
		MutableComponent result = Component.literal("All tests:\n");
		boolean failed = false;
		Instant start = Instant.now();
		Map<HudderUnitTestResult,String> failedtests = new HashMap<HudderUnitTestResult, String>();
		for (String name : UnitTests.keySet()) {
			var testresult = test(config, name);
			result.append("\n").append(testresult.toText(name));
			if (!testresult.isSucessful) {
				failed = true;
				failedtests.put(testresult, name);
			}
		}
		Instant end = Instant.now();
		if (failed) {
			result.append(Component.literal("\n\nFailed the following tests: ").withColor(0xff0000));
			for (var failedtest : failedtests.entrySet()) {
				result.append("\n\n");
				result.append(Component.literal(failedtest.getValue()).withColor(0xff0000));
				result.append(":");
				result.append(failedtest.getKey().getFailureMessage());
			}
			result.append(Component.literal(milliseconds(false, start, end, failedtests.size(), UnitTests.size())));
		} else result.append(Component.literal(milliseconds(true, start, end, failedtests.size(), UnitTests.size())));
		return result;
	}

	private String milliseconds(boolean success, Instant start, Instant end, int failedcount, int testscount) {
		double v = Duration.between(start, end).toNanos()/1000000d;
		double res = (int) (v*1000);
		res/=1000;
		return "\n\n" + (success? "Successful, " : "") + "took "+ res + "ms. Passed "
				+ (testscount-failedcount) + "/" + testscount + " tests.";
	}
}