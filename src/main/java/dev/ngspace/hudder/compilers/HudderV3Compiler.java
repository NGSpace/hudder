package dev.ngspace.hudder.compilers;

import java.lang.reflect.Method;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.V3VariableProcessor;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
	
public class HudderV3Compiler extends AVarTextCompiler {
	
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	
	@Override
	public HudInformation execute(HudderConfig info, String text, String filename) throws ExecutionException {
		
		V3ClassWriter classWriter = new V3ClassWriter("DynamicClass");
		classWriter.createDummyInit();
		V3ExecuteMethodWriter executeMethod = classWriter.createExecuteMethod();
		
		StringBuilder elemBuilder = new StringBuilder();
		
		int bracketscount = 0;

		boolean safeappend = false;
		int savedind = 0;
		
		boolean cleanup = false;
		int cleanup_amount = Hudder.config.methodBuffer();
		
		byte compileState = TEXT_STATE;

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
						case '{':
							compileState = VARIABLE_STATE;
							executeMethod.appendStringConstant(elemBuilder.toString());
							elemBuilder.setLength(0);
							bracketscount = 1;
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
					if (c=='"') {
						char prevchar = '\\';
						for (;ind<text.length();ind++) {
							c = text.charAt(ind);
							if (prevchar!='\\'&&c=='"')
								break;
							elemBuilder.append(c);
							prevchar = c;
						}
					}
					if (c=='{') {
						bracketscount++;elemBuilder.append(c);
					} else if (c=='}') {
						bracketscount--;
						if (bracketscount==0) {
							if ("break".equalsIgnoreCase(elemBuilder.toString().trim())) {
								throw new UnsupportedOperationException("break not supported");
							} else {
								V3VariableProcessor.parseVariable(executeMethod, elemBuilder.toString(), this);
								int endresult = executeMethod.store();
								executeMethod.pop();
								executeMethod.loadBuilder();
								executeMethod.aload(endresult);
								executeMethod.appendToBuilderAndPop();
							}
							elemBuilder.setLength(0);
							compileState = TEXT_STATE;
						} else elemBuilder.append(c);
					} else elemBuilder.append(c);
					break;
				}
				default: {
					var pos = getPosition(savedind, text);
					throw new ExecutionException("Unknown compile state: " + compileState, pos.line(), pos.column());
				}
			}
		}

		executeMethod.appendStringConstant(elemBuilder.toString());
		
		if (compileState!=0) {
			var pos = getPosition(savedind, text);
			throw new ExecutionException(getCompilerErrorMessage(compileState), pos.line(), pos.column());
		}
		
		executeMethod.end();

		Class<?> dynamicClass = classWriter.toClass();
		
		try {
			Object instance = dynamicClass.getDeclaredConstructor().newInstance();
			Method method = dynamicClass.getMethod("execute", HudderConfig.class, String.class, String.class);


			return (HudInformation) method.invoke(instance, info, text, filename);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return HudInformation.of("failed");
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		return false;
	}

	public String getCompilerErrorMessage(int compileState) {
		StringBuilder strb = new StringBuilder();
		strb.append(switch(compileState) {
			case VARIABLE_STATE -> "Expected '}'";
			default -> "An unknown error has occurred";
		});
		return strb.toString();
	}
	
}
