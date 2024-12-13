package io.github.ngspace.hudder.utils.testing;

import java.util.HashMap;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.hudder.config.HudderConfig;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HudderUnitTest {
	public final ATextCompiler compiler;
	public final String texttocompile;
	public final String expectation;
	public HudderUnitTest(String texttocompile, ATextCompiler compiler, String topleftexpectation) {
		this.compiler = compiler;
		this.texttocompile = texttocompile;
		this.expectation = topleftexpectation.replace('&', '\u00A7');
	}
	
	
	
	public HudderUnitTestResult test(HudderConfig info) {
		String text = null;
		try {
			if (compiler instanceof AV2Compiler v2comp) {
				v2comp.runtimes = new HashMap<String, V2Runtime>();
				v2comp.tempVariables = new HashMap<String, Object>();
			}
			ATextCompiler.variables.clear();
			text = compiler.compile(info, texttocompile, "Unit Tests").TopLeftText;
		} catch (Exception e) {
			e.printStackTrace();
			text = e.getMessage();
		}
		return new HudderUnitTestResult(expectation.equals(text), expectation.replaceAll("(^ )|( $)", "~"),
				text.replaceAll("(^ )|( $)", "~"));
	}
	
	
	
	public static class HudderUnitTestResult {
		public final boolean isSucessful;
		public final String expectation;
		public final String result;

		public HudderUnitTestResult(boolean isSucessful, String expectation, String result) {
			this.isSucessful = isSucessful;
			this.expectation = expectation;
			this.result = result;
		}
		public MutableComponent toText(String name) {
			var message = Component.literal(name + ": ").withColor(0x0fa1fc)
					.append(Component.literal((isSucessful?"Passed":"Failed")).withColor(isSucessful?0x0fff3f:0xff0000));
			Hudder.log("Test name: "+name);
			Hudder.log("Expectation: "+expectation);
			Hudder.log("Result: "+result);
			Hudder.log("Success: "+isSucessful);
			Hudder.log("");
			if (!isSucessful) message.append(getFailureMessage());
			return message;
		}
		
		public MutableComponent getFailureMessage() {
			var message = Component.literal("");
			message.append(Component.literal("\n  Expected:\n").withColor(0x000cff));
			message.append(Component.literal("    " + expectation.replace("\n", "\n    ")).withColor(0xffffff));
			message.append(Component.literal("\n  Got:\n").withColor(0x000cff));
			message.append(Component.literal("    " + result.replace("\n", "\n    ")).withColor(0xffffff));
			return message;
		}
	}
}
