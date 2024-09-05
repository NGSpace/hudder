package io.github.ngspace.hudder.util.testing;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;

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
			System.out.println(text + " " + res + " " + expectation);
			return new HudderUnitTestResult(res);
		} catch (CompileException e) {
			e.printStackTrace();
			return new HudderUnitTestResult(false);
		}
	}
	public static class HudderUnitTestResult {
		public final boolean b;

		public HudderUnitTestResult(boolean b) {
			this.b = b;
		}
		
	}
}
