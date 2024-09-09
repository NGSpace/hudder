package io.github.ngspace.hudder.util.testing;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.config.ConfigInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class HudderUnitTest {
	public final ATextCompiler compiler;
	public final String texttocompile;
	public final String expectation;
	public HudderUnitTest(String texttocompile, ATextCompiler compiler, String topleftexpectation) {
		this.compiler = compiler;
		this.texttocompile = texttocompile;
		this.expectation = topleftexpectation;
	}
	public HudderUnitTestResult test(ConfigInfo info) {
		try {
			String text = compiler.compile(info, texttocompile).TopLeftText;
			boolean res = expectation.equals(text);
			return new HudderUnitTestResult(res, expectation, text.replaceAll("(^ )|( $)", "~"));
		} catch (Exception e) {
			e.printStackTrace();
			return new HudderUnitTestResult(false, expectation, e.getMessage());
		}
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
		public MutableText toText(String name) {
			var message = Text.literal(name + ": ").withColor(0x0fa1fc)
					.append(Text.literal((isSucessful?"Passed":"Failed")).withColor(isSucessful?0x0fff3f:0xff0000));
			Hudder.log("Test name: "+name);
			Hudder.log("Expectation: "+expectation);
			Hudder.log("Result: "+result);
			Hudder.log("Success: "+isSucessful);
			Hudder.log("");
			if (!isSucessful) {
				message.append(getFailureMessage());
//				message.append(Text.literal("\n  Expected:\n").withColor(0x000cff));
//				message.append(Text.literal("    " + expectation.replace("\n", "    \n")).withColor(0xffffff));
//				message.append(Text.literal("\n  Got:\n").withColor(0x000cff));
//				message.append(Text.literal("    " + result.replace("\n", "    \n")).withColor(0xffffff));
			}
			return message;
		}
		
		public MutableText getFailureMessage() {
			var message = Text.literal("");
			message.append(Text.literal("\n  Expected:\n").withColor(0x000cff));
			message.append(Text.literal("    " + expectation.replace("\n", "    \n")).withColor(0xffffff));
			message.append(Text.literal("\n  Got:\n").withColor(0x000cff));
			message.append(Text.literal("    " + result.replace("\n", "    \n")).withColor(0xffffff));
			return message;
		}
	}
}