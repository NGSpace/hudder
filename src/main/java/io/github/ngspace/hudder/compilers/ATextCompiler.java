package io.github.ngspace.hudder.compilers;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.config.ConfigInfo;

public abstract class ATextCompiler {

	public static Map<String, Object> variables = new HashMap<String, Object>();
	
	public abstract HudInformation compile(ConfigInfo info, String text, String filename) throws CompileException;
	public abstract Object getVariable(String key) throws CompileException;
	
	public void put(String key, Object value) {variables.put(key, value);}
	public Object get(String key) {return variables.get(key);}
	
	protected CharPosition getPosition(int ind, String string) {
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
		return new CharPosition(line, charpos);
	}
	protected CharPosition getPosition(CharPosition charPosition, int ind, String j) {
		int line = charPosition.line;
		int charpos = charPosition.charpos;
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
		return new CharPosition(line, charpos);
	}
	public static class CharPosition {
		public final int line;
		public final int charpos;
		public CharPosition(int line, int charpos) {
			this.line = line;
			this.charpos = charpos;
		}
	}
	public ConfigInfo getConfig() {
		return Hudder.config;
	}
}
