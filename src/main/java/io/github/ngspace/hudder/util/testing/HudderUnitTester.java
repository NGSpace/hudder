package io.github.ngspace.hudder.util.testing;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.util.testing.HudderUnitTest.HudderUnitTestResult;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
	    HudderUnitTestsSuggestionProvider.suggestions = new ArrayList<String>(UnitTests.keySet());
	}
	public HudderUnitTestResult test(ConfigInfo info, String name) {
		return UnitTests.get(name).test(info);
	}
	public Text testAll(ConfigInfo config) {
		MutableText result = Text.literal("All tests:\n");
		boolean failed = false;
		Map<HudderUnitTestResult,String> failedtests = new HashMap<HudderUnitTestResult, String>();
		for (String name : UnitTests.keySet()) {
			var testresult = test(config, name);
			result.append("\n").append(testresult.toText(name));
			if (!testresult.isSucessful) {
				failed = true;
				failedtests.put(testresult, name);
			}
		}
		if (failed) {
			result.append(Text.literal("\n\nFailed the following tests: ").withColor(0xff0000));
			for (var failedtest : failedtests.entrySet()) {
				result.append("\n\n");
				result.append(Text.literal(failedtest.getValue()).withColor(0xff0000));
				result.append(":");
				result.append(failedtest.getKey().getFailureMessage());
			}
		} else result.append(Text.literal("\n\nSuccessful"));
		return result;
	}
}