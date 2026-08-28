package dev.ngspace.hudder.api.compilers.utils;

public class TextPosTracker {
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
