package dev.ngspace.hudder.compilers.abstractions;

import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.utils.HudFileUtils;

public abstract class ATextCompiler extends AHudCompiler<String> {
	
	
	@Override
	public String processFile(String filepath) throws CompileException {
		String text = HudFileUtils.readFile(filepath);
		compileFile(text, filepath);
		return text;
	}
	
	public void compileFile(String text, String filepath) throws CompileException {}
	
	protected TextPos getPosition(int ind, String string) {
		int line = 0;
		int charpos = 0;
		
		for (int i = 0;i<ind;i++) {
			if (string.charAt(i)=='\n') {
				line++;
				charpos = 0;
				continue;
			}
			charpos++;
		}
		return new TextPos(line, charpos);
	}
	protected TextPos getPosition(TextPos charPosition, int ind, String j) {
		int line = charPosition.line();
		int charpos = charPosition.column();
		if (line==-1||charpos==-1) {
			line = 0;
			charpos = 0;
		}
		
		for (int i = 0;i<ind;i++) {
			if (j.charAt(i)=='\n') {
				line++;
				charpos = 0;
				continue;
			}
			charpos++;
		}
		return new TextPos(line, charpos);
	}
}
