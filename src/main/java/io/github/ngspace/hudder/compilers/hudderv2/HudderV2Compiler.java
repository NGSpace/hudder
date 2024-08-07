package io.github.ngspace.hudder.compilers.hudderv2;

import java.util.Arrays;
import java.util.HashMap;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.compilers.TextCompiler;
import io.github.ngspace.hudder.compilers.hudderv2.runtime_elements.BasicConditionV2RuntimeElement;
import io.github.ngspace.hudder.compilers.hudderv2.runtime_elements.MetaV2RuntimeElement;
import io.github.ngspace.hudder.compilers.hudderv2.runtime_elements.StringV2RuntimeElement;
import io.github.ngspace.hudder.compilers.hudderv2.runtime_elements.VariableV2RuntimeElement;
import io.github.ngspace.hudder.compilers.hudderv2.runtime_elements.WhileV2RuntimeElement;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.meta.MetaCompiler;
import net.minecraft.client.MinecraftClient;

public class HudderV2Compiler extends TextCompiler {
	
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	public static final int CONDITION_STATE = 2;
	public static final int META_STATE = 3;
	public static final int ADVANCED_CONDITION_STATE = 4;
	public static final int WHILE_STATE = 5;
	public final MetaCompiler metacomp = new MetaCompiler();
	
	public static MinecraftClient ins = MinecraftClient.getInstance();
	
	HashMap<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();

	@Override public CompileResult compile(ConfigInfo info, String text) throws CompileException {
		V2Runtime runtime = runtimes.get(text);
		if (runtime==null) {
			runtime = new V2Runtime(this);
			
			StringBuilder resultBuilder = new StringBuilder();
			
			int line = 0;
			int col = 0;

			int bracketscount = 0;

			String[] metabuilder = {};

			String[] condArgs = {};
			boolean quotesafe = false;
			boolean condSafe = false;
			boolean safeappend = false;
			
			boolean cleanup = true;
			int cleanup_amount = ConfigManager.getConfig().metaBuffer;
			
			int compileState = TEXT_STATE;

			for (int ind = 0;ind<text.length();ind++) {
				char c = text.charAt(ind);
				col++;
				if (c=='\n') {line++;col=0;}
				switch (compileState) {
					case TEXT_STATE: {
						if (cleanup&&cleanup_amount>0&&cleanup_amount<10) {
							cleanup_amount--;
							if (c=='\n'||c=='\r') continue;
							else cleanup = false;
						} else cleanup = false;
						if (safeappend) {
							resultBuilder.append(c);
							safeappend = !safeappend;
							continue;
						}
						switch (c) {
							case '%':
								compileState = CONDITION_STATE;
								condArgs = new String[] {};
								runtime.addRuntimeElement(new StringV2RuntimeElement(resultBuilder.toString(), false));
								resultBuilder.setLength(0);
								break;
							case '{':
								compileState = VARIABLE_STATE;
								runtime.addRuntimeElement(new StringV2RuntimeElement(resultBuilder.toString(), false));
								resultBuilder.setLength(0);
								bracketscount = 1;
								break;
							case ';':
								compileState = META_STATE;
								runtime.addRuntimeElement(new StringV2RuntimeElement(resultBuilder.toString(), true));
								resultBuilder.setLength(0);
								break;
							case '#':
								compileState = ADVANCED_CONDITION_STATE;
								runtime.addRuntimeElement(new StringV2RuntimeElement(resultBuilder.toString(), true));
								resultBuilder.setLength(0);
								break;
							case '&':
								resultBuilder.append('\u00A7');
								break;
							case '\\': safeappend = true;break;
							default:
								resultBuilder.append(c);
						}
						break;
					}
					case VARIABLE_STATE: {
						switch (c) {
							case '{':bracketscount++;resultBuilder.append(c);break;
							case '}':
								bracketscount--;
								if (bracketscount==0) {
									runtime.addRuntimeElement(
											new VariableV2RuntimeElement(resultBuilder.toString(), runtime));
									resultBuilder.setLength(0);
									compileState = TEXT_STATE;
								} else resultBuilder.append(c);
								break;
							default: resultBuilder.append(c);break;
						}
						
						break;
					}
					case CONDITION_STATE: {
						if (c=='\\') {
							if (condSafe) resultBuilder.append('\\');
							else condSafe = true;
							continue;
						}
						if (quotesafe&&c!='"') {resultBuilder.append(c);continue;}
						if (condSafe) {resultBuilder.append(c);condSafe=false;continue;}
						switch (c) {
							case '%':
								compileState = TEXT_STATE;
								condArgs = addToArray(condArgs,resultBuilder.toString().trim());
								runtime.addRuntimeElement(new BasicConditionV2RuntimeElement(condArgs, runtime, info));
								resultBuilder.setLength(0);
								break;
							case '"':
								quotesafe = !quotesafe;
								resultBuilder.append(c);
								break;
							case ',':
								condArgs = addToArray(condArgs,resultBuilder.toString().trim());
								resultBuilder.setLength(0);
								break;
							default: resultBuilder.append(c);break;
						}
						
						break;
					}
					case META_STATE: {
						switch (c) {
							case ';':
								compileState = TEXT_STATE;
								break;
							case ',':
								metabuilder = addToArray(metabuilder,resultBuilder.toString().trim());
								resultBuilder.setLength(0);
								break;
							default: resultBuilder.append(c);break;
						}
						if (compileState!=META_STATE) {
							metabuilder = addToArray(metabuilder,resultBuilder.toString().trim());
							runtime.addRuntimeElement(new MetaV2RuntimeElement(metabuilder, runtime, info));
							resultBuilder.setLength(0);
							metabuilder = new String[0];
							cleanup = true;
							cleanup_amount = ConfigManager.getConfig().metaBuffer/2;
						}
						break;
					}
					case ADVANCED_CONDITION_STATE, WHILE_STATE: {
						boolean isWhile = compileState==WHILE_STATE;
						compileState = TEXT_STATE; //This mode is unique because it does it all in one go.
						StringBuilder condBuilder = new StringBuilder();
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
							runtime.addRuntimeElement(new WhileV2RuntimeElement(info, cond, cmds, this));
							break;
						}
						//TODO Add if statements
//						CompileResult result = compile(info, instructions.toString());
//						currentMeta.combineWithResult(result, false);
//						String resStr = (result.TopLeftText);
//						resultBuilder.append(resStr);
//						if (resStr.length()>0&&resStr.charAt(resStr.length()-1)!='\n')resultBuilder.append('\n');
						break;
					}
					default: throw new CompileException("Unknown compile state: " + compileState);
				}
			}
			
			runtime.addRuntimeElement(new StringV2RuntimeElement(resultBuilder.toString(), true));
			
			if (compileState!=0) throw new CompileException(getErrorMessage(compileState),line,col);
			
			runtimes.put(text, runtime);
		}
		return runtime.execute().toResult();
	}
	
	public String getErrorMessage(int compileState) {
		StringBuilder strb = new StringBuilder();
		strb.append(switch(compileState) {
			case VARIABLE_STATE -> "Expected '}'";
			case CONDITION_STATE -> "Expected '%'";
			case META_STATE -> "Expected ';'";
			case ADVANCED_CONDITION_STATE -> "Expected End of ADVANCED_CONDITION_STATE";
			case WHILE_STATE -> "Expected End of WHILE_STATE";
			default -> "An unknown error has occurred";
		});
		return strb.toString();
	}

	@Override
	public Object getVariable(String string) throws CompileException {
		Object val = super.getVariable(string.toLowerCase());
		if (val instanceof Number num&&num.doubleValue()%1==0) return num.longValue();
		return val;
	}
	public static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}
