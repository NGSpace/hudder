package io.github.ngspace.hudder.util.testing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.config.ConfigInfo;
import net.minecraft.text.Text;

public class HudderUnitTester {
	ATextCompiler compiler;
	
	public HudderUnitTester(ATextCompiler compiler) {
		this.compiler=compiler;
	}
	
	public Map<String, HudderUnitTest> UnitTests = new HashMap<String, HudderUnitTest>();
	
	public void load(InputStream inputStream) throws IOException {
		load(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
	}
	public void load(String contents) {
	    String[] conds = ("\n"+contents).split("\\n\\|\\|INPUT\\|\\|");
	    for (String st : conds) {
	    	if (st.isBlank()) continue;
	    	String[] content = st.split("\n",2);
	    	String[] inputandExpectation = content[1].split("\\n\\|\\|EXPECT\\|\\|\\n");
//	    	System.out.println("Name: "+content[0]);
//	    	System.out.println("Input: "+inputandExpectation[0]);
//	    	System.out.println("Expectation: "+inputandExpectation[1]);
//	    	System.out.println(inputandExpectation[0].equals(inputandExpectation[1]));
	    	UnitTests.put(content[0], new HudderUnitTest(inputandExpectation[0], compiler, inputandExpectation[1]));
	    }
	}
	public Text test(ConfigInfo info, String name) {
		return Text.literal(name+" "+UnitTests.get(name).test(info).b);
	}
	public Text testAll(ConfigInfo config) {
		return Text.literal("NOT IMPLANTED");
	}
}
