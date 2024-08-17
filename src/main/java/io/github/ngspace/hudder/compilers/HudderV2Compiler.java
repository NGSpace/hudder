package io.github.ngspace.hudder.compilers;

import java.util.Arrays;
import java.util.HashMap;

import io.github.ngspace.hudder.compilers.abstractions.AConditionCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.compilers.v2runtime.V2Runtime;
import io.github.ngspace.hudder.compilers.v2runtime.runtime_elements.BasicConditionV2RuntimeElement;
import io.github.ngspace.hudder.compilers.v2runtime.runtime_elements.IfV2RuntimeElement;
import io.github.ngspace.hudder.compilers.v2runtime.runtime_elements.MethodV2RuntimeElement;
import io.github.ngspace.hudder.compilers.v2runtime.runtime_elements.StringV2RuntimeElement;
import io.github.ngspace.hudder.compilers.v2runtime.runtime_elements.VariableV2RuntimeElement;
import io.github.ngspace.hudder.compilers.v2runtime.runtime_elements.WhileV2RuntimeElement;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;

public class HudderV2Compiler extends AConditionCompiler {
	
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	public static final int CONDITION_STATE = 2;
	public static final int META_STATE = 3;
	public static final int ADVANCED_CONDITION_STATE = 4;
	public static final int WHILE_STATE = 5;
	
	HashMap<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();

	@Override public CompileResult compile(ConfigInfo info, String text) throws CompileException {
		V2Runtime runtime = runtimes.get(text);
		if (runtime==null) {
			runtime = new V2Runtime();
			
			StringBuilder elemBuilder = new StringBuilder();

			int bracketscount = 0;

			String[] methodbuilder = {};

			String[] condArgs = {};
			boolean quotesafe = false;
			boolean condSafe = false;
			boolean safeappend = false;
			
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
								condArgs = new String[] {};
								runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false));
								elemBuilder.setLength(0);
								break;
							case '{':
								compileState = VARIABLE_STATE;
								runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false));
								elemBuilder.setLength(0);
								bracketscount = 1;
								break;
							case ';':
								compileState = META_STATE;
								runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), true));
								elemBuilder.setLength(0);
								break;
							case '#':
								compileState = ADVANCED_CONDITION_STATE;
								runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false));
								elemBuilder.setLength(0);
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
						switch (c) {
							case '{':bracketscount++;elemBuilder.append(c);break;
							case '}':
								bracketscount--;
								if (bracketscount==0) {
									runtime.addRuntimeElement(new VariableV2RuntimeElement
											(elemBuilder.toString(), this));
									elemBuilder.setLength(0);
									compileState = TEXT_STATE;
								} else elemBuilder.append(c);
								break;
							default: elemBuilder.append(c);break;
						}
						
						break;
					}
					case CONDITION_STATE: {
						if (c=='\\') {
							if (condSafe) elemBuilder.append('\\');
							else condSafe = true;
							continue;
						}
						if (quotesafe&&c!='"') {elemBuilder.append(c);continue;}
						if (condSafe) {elemBuilder.append(c);condSafe=false;continue;}
						switch (c) {
							case '%':
								compileState = TEXT_STATE;
								condArgs = addToArray(condArgs,elemBuilder.toString().trim());
								runtime.addRuntimeElement(new BasicConditionV2RuntimeElement(condArgs, this, info));
								elemBuilder.setLength(0);
								break;
							case '"':
								quotesafe = !quotesafe;
								elemBuilder.append(c);
								break;
							case ',':
								condArgs = addToArray(condArgs,elemBuilder.toString().trim());
								elemBuilder.setLength(0);
								break;
							default: elemBuilder.append(c);break;
						}
						
						break;
					}
					case META_STATE: {
						switch (c) {
							case ';':
								compileState = TEXT_STATE;
								break;
							case ',':
								methodbuilder = addToArray(methodbuilder,elemBuilder.toString().trim());
								elemBuilder.setLength(0);
								break;
							default: elemBuilder.append(c);break;
						}
						if (compileState!=META_STATE) {
							methodbuilder = addToArray(methodbuilder,elemBuilder.toString().trim());
							runtime.addRuntimeElement(new MethodV2RuntimeElement(methodbuilder, this, info, runtime));
							elemBuilder.setLength(0);
							methodbuilder = new String[0];
							cleanup = true;
							cleanup_amount = ConfigManager.getConfig().methodBuffer/2;
						}
						break;
					}
					case ADVANCED_CONDITION_STATE, WHILE_STATE: {
						boolean isWhile = compileState==WHILE_STATE;
						compileState = TEXT_STATE;
						for (;ind<text.length();ind++) {
							if ((c = text.charAt(ind))=='\n') break;
							else if(c==' '&&elemBuilder.toString().equals("while")) {
								elemBuilder.setLength(0);
								isWhile=true;
							} else if(c==' '&&elemBuilder.toString().equals("if")) elemBuilder.setLength(0);
							else elemBuilder.append(c);
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
						if (isWhile) {
							runtime.addRuntimeElement(new WhileV2RuntimeElement(info, cond, cmds, this));
							break;
						}
						runtime.addRuntimeElement(new IfV2RuntimeElement(info, cond, cmds, this));
						break;
					}
					default: throw new CompileException("Unknown compile state: " + compileState);
				}
			}
			
			runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false, true));
			
			if (compileState!=0) throw new CompileException(getErrorMessage(compileState));
			
			runtimes.put(text, runtime);
		}
		return runtime.execute().toResult();
	}
	
	private String getErrorMessage(int compileState) {
		StringBuilder strb = new StringBuilder();
		strb.append(switch(compileState) {
			case VARIABLE_STATE -> "Expected '}'";
			case CONDITION_STATE -> "Expected '%'";
			case META_STATE -> "Expected ';'";
			case ADVANCED_CONDITION_STATE -> "Expected end of ADVANCED_CONDITION_STATE";
			case WHILE_STATE -> "Expected end of WHILE_STATE";
			default -> "An unknown error has occurred";
		});
		return strb.toString();
	}
	@Override public Object getVariable(String string) throws CompileException {
		Object val = super.getVariable(string.toLowerCase());
		if (val instanceof Number num&&num.doubleValue()%1==0) return num.longValue();
		return val;
	}
	
	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}
