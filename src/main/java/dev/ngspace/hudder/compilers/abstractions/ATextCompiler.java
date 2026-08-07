package dev.ngspace.hudder.compilers.abstractions;

import java.io.IOException;

import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.utils.HudFileUtils;

public abstract class ATextCompiler extends AHudCompiler<String> {
	
	
	@Override
	public String processFile(String filepath) throws CompileException, IOException {
		String text = HudFileUtils.readFile(filepath);
		compileFile(text, filepath);
		return text;
	}
	
	public void compileFile(String text, String filepath) throws CompileException {}
	
	protected TextPos getPosition(int ind, String string) {
		return getPosition(new TextPos(0, 0), ind, string);
	}
	protected TextPos getPosition(TextPos charPosition, int ind, String text) {
		int line = charPosition.line();
		int charpos = charPosition.column();
		if (line==-1||charpos==-1) {
			line = 0;
			charpos = 0;
		}
		
		for (int i = 0;i<ind;i++) {
			if (text.charAt(i)=='\n') {
				line++;
				charpos = 0;
				continue;
			}
			charpos++;
		}
		return new TextPos(line, charpos);
	}
	
	public static class TextPosTracker {
		public String text;
		public int line = 0;
		public int column = 0;
		public int currentIndex = 0;
		
		public TextPosTracker(String text, TextPos offset) {
			this.text = text;
			this.line = offset.line();
			this.column = offset.column();
			if (line==-1||column==-1) {
				line = 0;
				column = 0;
			}
		}
		
		public TextPos get() {
			return new TextPos(line, column);
		}
		
		public void goToIndex(int newindex) {
			while (currentIndex<newindex)
				nextCharacter();
		}

		public void nextCharacter() {
			char c = text.charAt(currentIndex++);
			if (c=='\n') {
				line++;
				column = 0;
			} else column++;
		}
		
		public TextPos goToAndGet(int newindex) {
			goToIndex(newindex);
			return get();
		}
	}
}
