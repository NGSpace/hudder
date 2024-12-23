package io.github.ngspace.hudder.compilers;

import java.util.Arrays;

import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler;
import net.minecraft.client.MinecraftClient;

public class DefaultCompiler extends TextCompiler {
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	public static final int CONDITION_STATE = 2;
	public static final int META_STATE = 3;
	public static final int ADVANCED_CONDITION_STATE = 4;
	public static final int WHILE_STATE = 5;
	public final MetaCompiler metacomp = new MetaCompiler();
	
	public static MinecraftClient ins = MinecraftClient.getInstance();

	@Override public CompileResult compile(ConfigInfo info, String text) throws CompileException {
		
		StringBuilder resultBuilder = new StringBuilder();
		Meta currentMeta = new Meta(Meta.TOPLEFT);
		
		int line = 0;
		int col = 0;

		StringBuilder varbuilder = new StringBuilder();
		int bracketscount = 0;

		StringBuilder metaBuilder = new StringBuilder();
		String[] metabuilder = {};

		StringBuilder condArgBuilder = new StringBuilder();
		String[] condArgs = {};
		boolean quotesafe = false;
		boolean condSafe = false;
		boolean safeappend = false;
		
		int compileState = TEXT_STATE;
		try {
		for (int ind = 0;ind<text.length();ind++) {
			char c = text.charAt(ind);
			col++;
			if (c=='\n') {line++;col=0;}
			switch (compileState) {
				case TEXT_STATE: {
					if (safeappend) {
						resultBuilder.append(c);
						safeappend = !safeappend;
						continue;
					}
					switch (c) {
						case '%':
							compileState = CONDITION_STATE;
							condArgs = new String[] {};
							condArgBuilder.setLength(0);
							break;
						case '{':
							compileState = VARIABLE_STATE;
							varbuilder.setLength(0);
							bracketscount = 1;
							break;
						case ';':
							compileState = META_STATE;
							metaBuilder.setLength(0);
							break;
						case '\\':
							safeappend = true;
							break;
						case '&':
							resultBuilder.append('\u00A7');
							break;
						case '#': compileState = ADVANCED_CONDITION_STATE;break;
//						case '@': compileState = WHILE_STATE;break;
						default:
							resultBuilder.append(c);
							break;
					}
					break;
				}
				case VARIABLE_STATE: {
					switch (c) {
						case '{':bracketscount++;break;
						case '}':
							bracketscount--;
							if (bracketscount==0) {
								resultBuilder.append(getCleanVariable(varbuilder.toString()));
								varbuilder.setLength(0);
								compileState = TEXT_STATE;
							}
							break;
						default: break;
					}
					varbuilder.append(c);
					break;
				}
				case CONDITION_STATE: {
					if (c=='\\') {
						if (condSafe) condArgBuilder.append('\\');
						else condSafe = true;
						continue;
					}
					if (quotesafe&&c!='"') {condArgBuilder.append(c);continue;}
					if (condSafe) {condArgBuilder.append(c);condSafe=false;continue;}
					switch (c) {
						case '%': 
							compileState = TEXT_STATE;
							CompileResult res = solveCondition(info,
									AddToStringArray(condArgs,condArgBuilder.toString().trim()));
							currentMeta.combineWithResult(res, true);
							break;
						case '"':
							quotesafe = !quotesafe;
							condArgBuilder.append(c);
							break;
						case ',':
							condArgs = AddToStringArray(condArgs,condArgBuilder.toString().trim());
							condArgBuilder.setLength(0);
							break;
						default: condArgBuilder.append(c);break;
					}
					break;
				}
				case META_STATE: {
					switch (c) {
						case ';':
							compileState = TEXT_STATE;
							break;
						case ',':
							metabuilder = AddToStringArray(metabuilder,metaBuilder.toString().trim());
							metaBuilder.setLength(0);
							break;
						default: metaBuilder.append(c);break;
					}
					if (compileState!=META_STATE) {
						metabuilder = AddToStringArray(metabuilder,metaBuilder.toString().trim());
						String command = metabuilder[0].toLowerCase();
						if (command.equals(Meta.TOPLEFT)
								||command.equals(Meta.BOTTOMLEFT)
								||command.equals(Meta.TOPRIGHT)
								||command.equals(Meta.BOTTOMRIGHT)
								||command.equals(Meta.MUTE)) {
							currentMeta.addString(resultBuilder.toString(), true);
							resultBuilder.setLength(0);
						}
						metacomp.execute(info, currentMeta, this, metabuilder);
						metabuilder = new String[0];
					}
					break;
				}
				case ADVANCED_CONDITION_STATE, WHILE_STATE: {
					boolean isWhile = compileState==WHILE_STATE;
					compileState = TEXT_STATE; //This mode is unique because it does it all in one go.
					StringBuilder condBuilder = new StringBuilder();
					currentMeta.addString(resultBuilder.toString(), false);
					resultBuilder.setLength(0);
					for (;ind<text.length();ind++) {
						if ((c = text.charAt(ind))=='\n') break;
						else if(c==' '&&condBuilder.toString().equals("while")) {
							condBuilder.setLength(0);
							isWhile=true;
						} else if(c==' '&&condBuilder.toString().equals("if")) condBuilder.setLength(0);
						else condBuilder.append(c);
					}
					String cond = condBuilder.toString();
					StringBuilder instructions = new StringBuilder();
					boolean condition = conditionCheck(cond);
					ind++;
					if (ind<text.length()&&text.charAt(ind)=='\t') {
						ind++;
						for (;ind<text.length();ind++) {
							c = text.charAt(ind);
							if ((c=='\n')&&ind+1<text.length()) {
								if (text.charAt(ind+1)=='\t') {ind++;}
								else {instructions.append('\n');break;}
								
							}
							if (condition) instructions.append(c);
						}
					} else ind--;
					if (!condition) break;
					if (isWhile) {
						String cmds = instructions.toString();
						while(conditionCheck(cond)) currentMeta.combineWithResult(compile(info, cmds), false);
						break;
					}
					CompileResult result = compile(info, instructions.toString());
					currentMeta.combineWithResult(result, false);
					String resStr = (result.TopLeftText);
					resultBuilder.append(resStr);
					if (resStr.length()>0&&resStr.charAt(resStr.length()-1)!='\n')resultBuilder.append('\n');
					break;
				}
				default: throw new CompileException("Compiler reached an unknown state: "+compileState,line,col);
			}
		}
		} catch (CompileException e) {
			throw new CompileException(e.getMessage(),line+e.line,-1);
		}
		currentMeta.addString(resultBuilder.toString(), true);
		
		if (compileState!=0) throw new CompileException(getErrorMessage(compileState),line,col);
		
		return currentMeta.toResult();
	}
	
	public String getErrorMessage(int compileState) {
		StringBuilder strb = new StringBuilder();
		strb.append(switch(compileState) {
			case VARIABLE_STATE -> "Expected '}'";
			case CONDITION_STATE -> "Expected '#'";
			case META_STATE -> "Expected ';'";
			case ADVANCED_CONDITION_STATE -> "Expected End of ADVANCED_CONDITION_STATE";
			case WHILE_STATE -> "Expected End of WHILE_STATE";
			default -> "An unknown error has occurred";
		});
		return strb.toString();
	}

	protected Object getCleanVariable(String string) throws CompileException {
		Object val = getVariable(string.toLowerCase());
		if (val instanceof Number num&&num.doubleValue()%1==0) return num.longValue();
		return val;
	}
	public static String[] AddToStringArray(String[] arr, String string) {
		String[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = string;
		return newarr;
	}
}
