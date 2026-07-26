package dev.ngspace.hudder.compilers;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.V3VariableProcessor;
import dev.ngspace.hudder.hudderv3.v3variableinstructions.VariableVisitor;
import dev.ngspace.hudder.utils.HudderUtils;
import dev.ngspace.hudder.v2runtime.functions.HudderFunctions;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
	
public class HudderV3Compiler extends AVarTextCompiler {
	
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	public static final int CONDITION_STATE = 2;
	public static final int METHOD_STATE = 3;
	
	public V3VariableProcessor variableProcessor = new V3VariableProcessor();
	
	@Override
	public HudInformation execute(HudderConfig info, String text, String filename) throws ExecutionException {
		
		V3ClassWriter classWriter = new V3ClassWriter("dev/ngspace/hudder/hudderv3/GeneratedClass");
		classWriter.createInit();
		V3ExecuteMethodWriter executeMethod = classWriter.createExecuteMethod();
		
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(classWriter);
		HudderFunctions.bindAllAPIFunctions(classWriter);
		
		StringBuilder elemBuilder = new StringBuilder();
		
		int bracketscount = 0;
		int conditionsCount = 0;

		boolean quotesafe = false;
		boolean backslashsafe = false;
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
						case ';':
							compileState = METHOD_STATE;
							executeMethod.appendStringConstant(trimMethod(elemBuilder.toString()));
							elemBuilder.setLength(0);
							savedind = ind;
						    quotesafe = false;
						    backslashsafe = false;
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
									new String[] {
											"dev/ngspace/hudder/exceptions/ExecutionException"
										});
							
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
						String[] builder = HudderUtils.processParemeters(elemBuilder.toString());
						if (builder.length==2&&builder[0].toLowerCase().trim().equals("return")) {
							throw new UnsupportedOperationException("NO METHOD NO RETURN");
//							runtime.addRuntimeElement(new ReturnV2RuntimeElement(builder[1],this,runtime,line,charpos));
						} else {
							executeMethod.loadConstantUnsafe(builder.length-1);
							executeMethod.methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,
									Type.getInternalName(Object.class));
							int array_index = executeMethod.astore();
							for (int i = 1;i<builder.length;i++) {
								variableProcessor.parseVariable(executeMethod, builder[i], this);
								int value_index = executeMethod.astore();
								executeMethod.aload(array_index);
								executeMethod.loadConstantUnsafe(i-1);
								executeMethod.aload(value_index);
								executeMethod.methodVisitor.visitInsn(Opcodes.AASTORE);
							}
							executeMethod.loadConstant(builder[0]);
							executeMethod.aload(0);
							executeMethod.getField("uimanager", ArrayElementManager.class);
							executeMethod.aload(0);
							executeMethod.getField("v3compiler", HudderV3Compiler.class);
							executeMethod.aload(array_index);
							executeMethod.callStatic(HudderV3Helper.class, "callApiConsumer",
									"(Ljava/lang/String;Ldev/ngspace/hudder/api/functionsandconsumers/ArrayElementManager;Ldev/ngspace/hudder/compilers/abstractions/AVarTextCompiler;[Ljava/lang/Object;)V", false);
						}
						elemBuilder.setLength(0);
						cleanup = true;
						cleanup_amount = Hudder.config.methodBuffer()/2;
					}
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
			Object instance = dynamicClass.getDeclaredConstructor(getClass()).newInstance(this);
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
	public String trimMethod(String string) {
		String str = string;
		int buffer;
		if ((buffer = Hudder.config.methodBuffer())<10) {
			for (int i = 0; i<buffer;i++) {
				if (str.endsWith("\n")||str.endsWith("\r")) str = str.substring(0, str.length()-1);
			}
		}
		return str;
	}

	public VariableVisitor parseVariable(String string) throws ExecutionException {
		return variableProcessor.parseVariable(null, string, this);
	}
	
}
