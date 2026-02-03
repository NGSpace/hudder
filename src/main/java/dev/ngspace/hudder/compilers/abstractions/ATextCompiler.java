package dev.ngspace.hudder.compilers.abstractions;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.CharPosition;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.main.config.HudderConfig;

public abstract class ATextCompiler {

	public static Map<String, Object> variables = new HashMap<String, Object>();
	
	public abstract HudInformation compile(HudderConfig info, String text, String filename) throws CompileException;
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
		int line = charPosition.line();
		int charpos = charPosition.charpos();
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
	public HudderConfig getConfig() {
		return Hudder.config;
	}
}
