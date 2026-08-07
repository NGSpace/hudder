package dev.ngspace.hudder.testing;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.config.HudderConfig;

public class HudderUnitTest {
	public final String texttocompile;
	public final String expectation;
	public final String filename;
	
	public HudderUnitTest(String texttocompile, String topleftexpectation, String filename) {
		this.texttocompile = texttocompile;
		this.expectation = topleftexpectation.replace('&', '\u00A7');
		this.filename = filename;
	}
	
	public HudderUnitTestResult test(AVarTextCompiler compiler, HudderConfig info) {
		String text = null;
		Hudder.log("Running unit test: " + texttocompile);
		try {
			compiler.resetState();
			compiler.compileFile(texttocompile, "Unit Tests");
			text = compiler.execute(info, texttocompile, "Unit Tests").TopLeftText();
		} catch (Exception e) {
			e.printStackTrace();
			text = e.getMessage();
		}
		return new HudderUnitTestResult(expectation.equals(text), expectation.replaceAll("(^ )|( $)", "~"),
				text.replaceAll("(^ )|( $)", "~"), filename);
	}
}
