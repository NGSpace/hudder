package dev.ngspace.hudder.testing;

import java.util.Map;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.abstractions.AVarTextCompiler;

public class HudderUnitTest {
	public final String texttocompile;
	public final String expectation;
	public final String filename;
	public final Mode mode;
	public final Map<String, String[]> metadata;
	
	public HudderUnitTest(String texttocompile, String topleftexpectation, String filename,
			Map<String, String[]> metadata, Mode mode) {
		this.texttocompile = texttocompile;
		this.expectation = topleftexpectation.replace('&', '\u00A7');
		this.filename = filename;
		this.metadata = metadata;
		this.mode = mode;
	}
	
	public HudderUnitTestResult test(AVarTextCompiler compiler) {
		Hudder.log("Running unit test: " + texttocompile);
		return switch (mode) {
			case NORMAL: {
				String text = null;
				try {
					compiler.resetState();
					compiler.compileFile(texttocompile, "Unit Tests");
					text = compiler.execute(texttocompile, "Unit Tests").TopLeftText();
				} catch (Exception e) {
					e.printStackTrace();
					text = e.getMessage();
				}
				yield new HudderUnitTestResult(expectation.equals(text), expectation.replaceAll("(^ )|( $)", "~"),
						text.replaceAll("(^ )|( $)", "~"), filename);
			}
			case ERROR: {
				// Stupidest way to do this but I'm tired rn
				String type = metadata.getOrDefault("exception_type", new String[1])[0];
				try {
					compiler.resetState();
					compiler.compileFile(texttocompile, "Unit Tests");
					compiler.execute(texttocompile, "Unit Tests");

					yield new HudderUnitTestResult(false, type, "Error-less execution", filename);
				} catch (Exception e) {
					boolean failed = false;
					if (!expectation.isBlank() && !e.getMessage().equals(expectation))
						failed = true;
					if (type!=null && !e.getClass().getSimpleName().equals(type))
						failed = true;
					yield new HudderUnitTestResult(!failed, type + (!expectation.isBlank()?": " +expectation:""),
							e.toString(), filename);
				}
			}
			case NO_ERROR: {
				try {
					compiler.resetState();
					compiler.compileFile(texttocompile, "Unit Tests");
					var res = compiler.execute(texttocompile, "Unit Tests");
					yield new HudderUnitTestResult(true, "No error to occur",
							res.TopLeftText().replaceAll("(^ )|( $)", "~"), filename);
				} catch (Exception e) {
					e.printStackTrace();
					yield new HudderUnitTestResult(false, "No error to occur", e.toString(), filename);
				}
			}
		};
	}
	
	public enum Mode {
		NORMAL,
		ERROR,
		NO_ERROR,;
	}
}
