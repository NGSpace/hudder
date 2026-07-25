package dev.ngspace.hudder.compilers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.V3VariableProcessor;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
	
public class HudderV3Compiler extends AVarTextCompiler {
	
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	public static final int CONDITION_STATE = 2;
	
	public V3VariableProcessor variableProcessor = new V3VariableProcessor();
	
	@Override
	public HudInformation execute(HudderConfig info, String text, String filename) throws ExecutionException {
		
		V3ClassWriter classWriter = new V3ClassWriter("dev/ngspace/hudder/hudderv3/GeneratedClass");
		classWriter.createDummyInit();
		V3ExecuteMethodWriter executeMethod = classWriter.createExecuteMethod();
		
		StringBuilder elemBuilder = new StringBuilder();
		
		int bracketscount = 0;
		int conditionsCount = 0;

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
						case '%':
							compileState = CONDITION_STATE;
							executeMethod.appendStringConstant(elemBuilder.toString());
							elemBuilder.setLength(0);
							savedind = ind;
							break;
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
				case CONDITION_STATE: {
					StringBuilder conditionOrValue = new StringBuilder();
					ArrayList<String> conds = new ArrayList<String>();
					
					boolean quotes = false;
					boolean escaped = false;
					
					for (;ind<text.length();ind++) {
						c = text.charAt(ind);
						
						if (quotes) {
							conditionOrValue.append(c);
							if (c=='\\') {
								escaped = true;
								continue;
							}
							if (c=='"'&&!escaped) quotes = false;
							escaped = false;
							continue;
						}
						
						if (c=='"') quotes = true;
						
						if (c==',') {
							conds.add(conditionOrValue.toString());
							conditionOrValue.setLength(0);
						} else if (c=='%') {
							conds.add(conditionOrValue.toString());
							V3MethodWriter writer = classWriter.createMethod("condition"+conditionsCount,
									new Class<?>[0],
									Object.class,
									null,
									new String[0]);
							
							for (int i = 0;i<conds.size();i+=2) {
								Label elseLabel = new Label();
								if (i!=conds.size()) {
									variableProcessor.parseVariable(writer, conds.get(i), this);
									writer.loadConstant(true);// The other value
									
									writer.methodVisitor.visitJumpInsn(Opcodes.IF_ACMPNE, elseLabel);
									
									variableProcessor.parseVariable(writer, conds.get(i+1), this);
								}
								
								writer.addAReturn();
								writer.putLabel(elseLabel);
								
								i++;
							}
							if (conds.size()%2==1)
								variableProcessor.parseVariable(writer, conds.get(conds.size()-1), this);
							else
								writer.loadConstant("");
							writer.end(Opcodes.ARETURN);
							
							executeMethod.aload(0);
							
							executeMethod.callSelf("condition"+conditionsCount, "()Ljava/lang/Object;", false);
							executeMethod.appendToBuilderAndPop();
							
//							runtime.addRuntimeElement(new ConditionV2RuntimeElement(
//									conds.toArray(new String[conds.size()]), this, info, filename));
							
							
							compileState = TEXT_STATE;
							conditionsCount++;
							break;
						} else {
							conditionOrValue.append(c);
						}
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
								variableProcessor.parseVariable(executeMethod, elemBuilder.toString(), this);
								int endresult = executeMethod.astore();
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
