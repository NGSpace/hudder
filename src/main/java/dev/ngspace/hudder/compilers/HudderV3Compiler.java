package dev.ngspace.hudder.compilers;

import java.util.ArrayList;
import java.util.Arrays;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler.CodeBlock;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler.Instruction;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.MethodExecutionInstruction;
import dev.ngspace.hudder.hudderv3.instructions.WhileInstruction;
import dev.ngspace.hudder.utils.HudderUtils;
import dev.ngspace.hudder.v2runtime.runtime_elements.WhileV2RuntimeElement;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import net.minecraft.network.chat.Component;
	
public class HudderV3Compiler extends AV3Compiler {
	
	public static final int TEXT_STATE = 0;
	public static final int VARIABLE_STATE = 1;
	public static final int CONDITION_STATE = 2;
	public static final int METHOD_STATE = 3;
	public static final int HASHTAG_STATE = 4;

	public static final byte UNDEFINED_INSTRUCTION = 0x0;
	public static final byte IF_INSTRUCTION = 0x1;
	public static final byte WHILE_LOOP_INSTRUCTION = 0x2;
	public static final byte DEFINE_INSTRUCTION = 0x3;
	public static final byte FOR_LOOP_INSTRUCTION = 0x4;
	public static final byte ELSE_IF_INSTRUCTION = 0x5;
	public static final byte ELSE_INSTRUCTION = 0x6;
	
	@Override
	public boolean compile(V3ExecuteMethodWriter executeMethod, V3ClassWriter classWriter, HudderConfig info,
			String text, String filename, Label breakLabel) throws ExecutionException {
		
		boolean returns_value = false;
		
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
						case '#':
							compileState = HASHTAG_STATE;
							executeMethod.appendStringConstant(elemBuilder.toString());
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
							Label conditionend = new Label();
							
							for (int i = 0;i<conds.size()-1;i++) {
								Label elseLabel = new Label();
								if (i!=conds.size()) {
									parseVariable(conds.get(i)).visitMethod(executeMethod);
									executeMethod.booleanValue();
									executeMethod.methodVisitor.visitJumpInsn(Opcodes.IFEQ, elseLabel);
	
									parseVariable(conds.get(i+1)).visitMethod(executeMethod);
								}
								
								executeMethod.jumpto(conditionend);
								executeMethod.putLabel(elseLabel);
								i++;
							}
							
							if (conds.size()%2==1)
								parseVariable(conds.get(conds.size()-1)).visitMethod(executeMethod);
							else
								executeMethod.loadConstant("");
							
							executeMethod.putLabel(conditionend);
							
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
								executeMethod.jumpto(breakLabel);
							} else {
								parseVariable(elemBuilder.toString()).visitMethod(executeMethod);
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
						String name = builder[0].toLowerCase().trim();
						switch (name) {
							case "return":
								returns_value = true;
								parseVariable(builder[1]).visitMethod(executeMethod);
								executeMethod.astore(executeMethod.return_value_index);
								executeMethod.jumpto(executeMethod.finalLabel);
							case "topleft":
								executeMethod.selected_builder_index = executeMethod.topleft_builder_index;
								if (builder.length>1) {
									parseVariable(builder[1]).visitMethod(executeMethod);
									executeMethod.astore(executeMethod.topleft_scale_index);
								}
								break;
							case "topright":
								executeMethod.selected_builder_index = executeMethod.topright_builder_index;
								if (builder.length>1) {
									parseVariable(builder[1]).visitMethod(executeMethod);
									executeMethod.astore(executeMethod.topright_scale_index);
								}
								break;
							case "bottomleft":
								executeMethod.selected_builder_index = executeMethod.bottomleft_builder_index;
								if (builder.length>1) {
									parseVariable(builder[1]).visitMethod(executeMethod);
									executeMethod.astore(executeMethod.bottomleft_scale_index);
								}
								break;
							case "bottomright":
								executeMethod.selected_builder_index = executeMethod.bottomright_builder_index;
								if (builder.length>1) {
									parseVariable(builder[1]).visitMethod(executeMethod);
									executeMethod.astore(executeMethod.bottomright_scale_index);
								}
								break;
							default:
								new MethodExecutionInstruction(this, builder).visit(executeMethod, classWriter);
						}
						elemBuilder.setLength(0);
						cleanup = true;
						cleanup_amount = Hudder.config.methodBuffer()/2;
					}
					break;
				}
				case HASHTAG_STATE: {
					compileState = TEXT_STATE;
					
					Instruction instructions = getInstruction(text, ind);
					ind = instructions.ending_index();
					
					CodeBlock codeBlock = getCodeBlock(text, ind);
					ind = codeBlock.ending_index();

					byte instruction_code = instructions.instruction();
					String parameters = instructions.paremeter();
					String block = codeBlock.code();
					
					switch (instruction_code) {
//						case FOR_LOOP_INSTRUCTION: {
//							String[] split = parameters.split(" in ", 2);
//							if (split.length<2) 
//								throw new CompileException("Invalid for loop syntax: \"" + parameters + "\"", pos);
//							String variablename = split[0];
//							String value = split[1];
//							elemBuilder.setLength(0);
//							runtime.addRuntimeElement(new ForV2RuntimeElement(info, variablename, value,
//									block, this, runtime, pos, filename));
//							break;
//						}
						case DEFINE_INSTRUCTION: {
							String[] builder = HudderUtils.processParemeters(parameters);
							String name = builder[0];
							String[] args = Arrays.copyOfRange(builder, 1, builder.length);
							defineFunctionOrMethod(classWriter,block, args, info, name,filename);
							elemBuilder.setLength(0);
							break;
						}
						case WHILE_LOOP_INSTRUCTION: {
							new WhileInstruction(parameters, block, this, info, filename)
									.visit(executeMethod, classWriter);
//							runtime.addRuntimeElement(new WhileV2RuntimeElement(info, parameters, block, this,
//									runtime, pos, filename));
							break;
						}
						case UNDEFINED_INSTRUCTION:
							Hudder.showWarningToast(Component.literal("Undefined # instructions deprecated"),
									Component.literal("Undefined # instructions are deprecated and will be "
											+ "removed in a future version of Hudder, please use #if instead."));
//						case IF_INSTRUCTION:// If instuction
//							List<Statement> statements = new ArrayList<Statement>();
//							statements.add(new Statement(parameters, block, pos));
//							for (;ind<text.length();ind++) {
//								c = text.charAt(ind);
//								if (c=='#') {
//									savedind = ind;
//									pos = getPosition(charPosition, ind, "\n"+text);
//									ind++;
//									instructions = getInstruction(text, ind);
//									ind = instructions.ending_index();
//									if (instructions.instruction()!=ELSE_INSTRUCTION
//											&&instructions.instruction()!=ELSE_IF_INSTRUCTION) {
//										//We've gone deep into another hash instruction, go back.
//										ind = savedind;
//										break;
//									}
//									codeBlock = getCodeBlock(text, ind);
//									ind = codeBlock.ending_index();
//									parameters = instructions.paremeter();
//									block = codeBlock.code();
//									statements.add(new Statement(parameters, block, pos));
//								} else if (!Character.isWhitespace(c)) {
//									break;
//								}
//							}
//							ind--;
//							runtime.addRuntimeElement(new IfElseV2RuntimeElement(info,
//									statements.toArray(new Statement[statements.size()]),
//									runtime,
//									filename,
//									this));
//							break;
						default:
							throw new ExecutionException("Detached else/else if statement!", -1, -1);
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
		
		return returns_value;
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		return false;
	}
	
	public CodeBlock getCodeBlock(String text, int index) {
		StringBuilder instructions = new StringBuilder();
		int ind = index;
		if (ind+1<text.length()&&(text.charAt(ind+1)=='\t'||text.charAt(ind+1)==' ')) {
			
			ind++;
			String initalIndent = checkIndentation(text,ind);
			
			for (;ind<text.length();ind++) {
				if (ind+1<text.length()) {
					String indent = checkIndentation(text,ind);
					if (indent.startsWith(initalIndent)) {
						ind+=initalIndent.length();
						for (;ind<text.length();ind++) {
							char c = text.charAt(ind);
							instructions.append(c);
							if (c=='\n') break;
						}
					} else break;
				}
				
			}
			ind--;
		}
		
		if (ind!=text.length()&&text.charAt(ind)!='\n'&&text.charAt(ind)!='\r') ind--;
		
		return new CodeBlock(instructions.toString(), text, index, ind);
	}
	
	public Instruction getInstruction(String text, int index) {
		byte instruction = 0x0;
		char c;
		int ind = index;
		StringBuilder elemBuilder = new StringBuilder();
		for (;ind<text.length();ind++) {
			if ((c = text.charAt(ind))=='\n') break;
			if (instruction==0) {
				if(c==' '&&elemBuilder.toString().equals("while")) {instruction=WHILE_LOOP_INSTRUCTION;}
				else if(c==' '&&elemBuilder.toString().equals("if")) {instruction=IF_INSTRUCTION;}
				else if(c==' '&&elemBuilder.toString().equals("def")){instruction=DEFINE_INSTRUCTION;}
				else if(c==' '&&elemBuilder.toString().equals("for")){instruction=FOR_LOOP_INSTRUCTION;}
				else if(c==' '&&elemBuilder.toString().equals("else if")){instruction=ELSE_IF_INSTRUCTION;}
				else if(c=='e'&&elemBuilder.toString().equals("els")&&
						(text.charAt(ind+1)=='\n')){instruction=ELSE_INSTRUCTION;}
				if (instruction!=UNDEFINED_INSTRUCTION) {elemBuilder.setLength(0);continue;}
			}
			elemBuilder.append(c);
		}
		return new Instruction(instruction, elemBuilder.toString(), ind);
	}

	private String checkIndentation(String text, int index) {
		StringBuilder b = new StringBuilder();
		for (;index<text.length();index++) {
			char c = text.charAt(index);
			if (!(c==' '||c=='\t')) break;
			b.append(c);
		}
		return b.toString();
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
	
}
