package io.github.ngspace.hudder.compilers;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.util.HudderUtils;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.runtime_elements.BasicConditionV2RuntimeElement;
import io.github.ngspace.hudder.v2runtime.runtime_elements.IfV2RuntimeElement;
import io.github.ngspace.hudder.v2runtime.runtime_elements.MethodV2RuntimeElement;
import io.github.ngspace.hudder.v2runtime.runtime_elements.StringV2RuntimeElement;
import io.github.ngspace.hudder.v2runtime.runtime_elements.VariableV2RuntimeElement;
import io.github.ngspace.hudder.v2runtime.runtime_elements.WhileV2RuntimeElement;

public class HudderV2Compiler extends AV2Compiler {
	
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	public static final int CONDITION_STATE = 2;
	public static final int METHOD_STATE = 3;
	public static final int HASHTAG_STATE = 4;

	@Override public V2Runtime buildRuntime(ConfigInfo info, String text, CharPosition charPosition)
			throws CompileException {
		V2Runtime runtime = new V2Runtime();
		
		StringBuilder elemBuilder = new StringBuilder();

		int bracketscount = 0;

		String[] builder = {};

		boolean quotesafe = false;
		boolean backslashsafe = false;
		boolean condSafe = false;
		boolean safeappend = false;
		int savedind = 0;
		
		boolean cleanup = false;
		int cleanup_amount = ConfigManager.getConfig().methodBuffer;
		
		int compileState = TEXT_STATE;

		for (int ind = 0;ind<text.length();ind++) {
			char c = text.charAt(ind);
			switch (compileState) {
				case TEXT_STATE: {
					if (cleanup&&cleanup_amount>0&&cleanup_amount<10) {
						cleanup_amount--;
						if (c=='\n'||c=='\r') continue;
						else cleanup = false;
					} else cleanup = false;
					if (safeappend) {
						elemBuilder.append(c);
						safeappend = !safeappend;
						continue;
					}
					switch (c) {
						case '%':
							compileState = CONDITION_STATE;
							builder = new String[] {};
							runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false));
							elemBuilder.setLength(0);
							savedind = ind;
							break;
						case '{':
							compileState = VARIABLE_STATE;
							runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false));
							elemBuilder.setLength(0);
							bracketscount = 1;
							savedind = ind;
							break;
						case ';':
							compileState = METHOD_STATE;
							runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), true));
							elemBuilder.setLength(0);
							builder = new String[] {};
							savedind = ind;
							break;
						case '#':
							compileState = HASHTAG_STATE;
							builder = new String[] {};
							runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false));
							elemBuilder.setLength(0);
							savedind = ind;
							break;
						case '&':
							elemBuilder.append('\u00A7');
							break;
						case '\\': safeappend = true;break;
						default:
							elemBuilder.append(c);
					}
					break;
				}
				case VARIABLE_STATE: {
					if (c=='{') {
						bracketscount++;elemBuilder.append(c);
					} else if (c=='}') {
						bracketscount--;
						if (bracketscount==0) {
							var pos = getPosition(charPosition, savedind, text);
							runtime.addRuntimeElement(new VariableV2RuntimeElement(elemBuilder.toString(), this,
									runtime, pos.line, pos.charpos));
							elemBuilder.setLength(0);
							compileState = TEXT_STATE;
						} else elemBuilder.append(c);
					} else elemBuilder.append(c);
					break;
				}
				case CONDITION_STATE: {
					if (c=='\\') {
						if (condSafe) {elemBuilder.append('\\');condSafe = false;}
						else condSafe = true;
						continue;
					}
					if (quotesafe&&c!='"') {elemBuilder.append(c);continue;}
					if (condSafe&&c=='"') {elemBuilder.append('\\');elemBuilder.append(c);condSafe=false;continue;}
					if (condSafe) {elemBuilder.append(c);condSafe=false;continue;}
					switch (c) {
						case '%':
							compileState = TEXT_STATE;
							builder = addToArray(builder,elemBuilder.toString().trim());
							var pos = getPosition(charPosition, savedind, text);
							runtime.addRuntimeElement(new BasicConditionV2RuntimeElement(builder, this, info, runtime,
									pos.line, pos.charpos));
							elemBuilder.setLength(0);
							break;
						case '"':
							quotesafe = !quotesafe;
							elemBuilder.append(c);
							break;
						case ',':
							builder = addToArray(builder,elemBuilder.toString().trim());
							elemBuilder.setLength(0);
							break;
						default: elemBuilder.append(c);break;
					}	
					
					break;
				}
				case METHOD_STATE: {
					if (backslashsafe) {
						backslashsafe = false;
						elemBuilder.append(c);
						continue;
					}
					switch (c) {
						case '\\':
							backslashsafe = true;
							elemBuilder.append(c);
							break;
						case '"':
							quotesafe = !quotesafe;
							elemBuilder.append(c);
							break;
						case ';':
							if (!quotesafe) compileState = TEXT_STATE;
							else elemBuilder.append(c);
							break;
						default: elemBuilder.append(c);break;
					}
					if (compileState!=METHOD_STATE) {
						builder = HudderUtils.processParemeters(elemBuilder.toString());
						var pos = getPosition(charPosition, savedind, text);
						int line = pos.line;
						int charpos = pos.charpos;
						runtime.addRuntimeElement(new MethodV2RuntimeElement(builder,this,info,runtime,line,charpos));
						elemBuilder.setLength(0);
						builder = new String[0];
						cleanup = true;
						cleanup_amount = ConfigManager.getConfig().methodBuffer/2;
					}
					break;
				}
				case HASHTAG_STATE: {
					boolean isWhile = false;
					boolean isDef = false;
					compileState = TEXT_STATE;
					boolean hasbeendefined = false;
					for (;ind<text.length();ind++) {
						if ((c = text.charAt(ind))=='\n') break;
						if (!hasbeendefined) {
							if(c==' '&&elemBuilder.toString().equals("while")) {hasbeendefined=true;isWhile=true;}
							else if(c==' '&&elemBuilder.toString().equals("if")) {hasbeendefined=true;}
							else if(c==' '&&elemBuilder.toString().equals("def")){isDef=true;hasbeendefined=true;}
							if (hasbeendefined) {elemBuilder.setLength(0);continue;}
						}
						elemBuilder.append(c);
					}
					String cond = elemBuilder.toString();
					elemBuilder.setLength(0);
					StringBuilder instructions = new StringBuilder();
					ind++;
					if (ind<text.length()&&text.charAt(ind)=='\t') {
						ind++;
						for (;ind<text.length();ind++) {
							c = text.charAt(ind);
							if ((c=='\n')&&ind+1<text.length()) {
								if (text.charAt(ind+1)=='\t') {ind++;}
								else {instructions.append('\n');break;}
								
							}
							instructions.append(c);
						}
					} else ind--;
					String cmds = instructions.toString();
					if (isDef) {
						builder = new String[0];
						for (int i = 0;i<cond.length();i++) {
							char ch = cond.charAt(i);
							if (ch==',') {
								builder = addToArray(builder, elemBuilder.toString().trim().toLowerCase());
								elemBuilder.setLength(0);
							} else elemBuilder = elemBuilder.append(ch);
						}
						if (!elemBuilder.toString().isBlank())
							builder = addToArray(builder, elemBuilder.toString().trim().toLowerCase());
						String name = builder[0];
						String[] args = Arrays.copyOfRange(builder, 1, builder.length);
						var pos = getPosition(charPosition, savedind+1, "\n"+text);
						methodHandler.register(cmds, args, name, pos.line, pos.charpos);
						elemBuilder.setLength(0);
						break;
					}
					if (isWhile) {
						runtime.addRuntimeElement(new WhileV2RuntimeElement(info, cond, cmds, this, runtime,
								getPosition(charPosition, savedind+1, "\n"+text)));
						break;
					}
					runtime.addRuntimeElement(new IfV2RuntimeElement(info, cond, cmds, this, runtime,
							getPosition(charPosition, savedind+1, "\n"+text)));
					break;
				}
				default: throw new CompileException("Unknown compile state: " + compileState);
			}
		}
		
		runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false, true));
		
		if (compileState!=0) throw new CompileException(getCompilerErrorMessage(compileState));
		
		return runtime;
	}

	protected String getCompilerErrorMessage(int compileState) {
		StringBuilder strb = new StringBuilder();
		strb.append(switch(compileState) {
			case VARIABLE_STATE -> "Expected '}'";
			case CONDITION_STATE -> "Expected '%'";
			case METHOD_STATE -> "Expected ';'";
			case HASHTAG_STATE -> "Expected end of ADVANCED_CONDITION_STATE";
			default -> "An unknown error has occurred";
		});
		return strb.toString();
	}
	
	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}
