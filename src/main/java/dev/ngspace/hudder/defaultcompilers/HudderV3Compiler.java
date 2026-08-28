package dev.ngspace.hudder.defaultcompilers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.api.compilers.utils.TextPosTracker;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.instructions.compiler.ConditionInstruction;
import dev.ngspace.hudder.hudderv3.instructions.compiler.DefineInstruction;
import dev.ngspace.hudder.hudderv3.instructions.compiler.ForInstruction;
import dev.ngspace.hudder.hudderv3.instructions.compiler.IfElseInstuction;
import dev.ngspace.hudder.hudderv3.instructions.compiler.IfElseInstuction.Statement;
import dev.ngspace.hudder.hudderv3.instructions.compiler.MethodExecutionInstruction;
import dev.ngspace.hudder.hudderv3.instructions.compiler.VariableInstruction;
import dev.ngspace.hudder.hudderv3.instructions.compiler.WhileInstruction;
import dev.ngspace.hudder.utils.HudderUtils;
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
	
	public HudderV3Compiler(HudderConfig config) {
		super(config);
	}
	
	@Override
	public TokenizedCodeBlock compile(String text, String filename, TextPos offset) throws CompileException {
		
		TokenizedCodeBlock finalCodeBlock = new TokenizedCodeBlock(this);
		
		TextPosTracker posTracker = new TextPosTracker(text, offset);
		
		StringBuilder elemBuilder = new StringBuilder();
		
		int bracketscount = 0;

		boolean quotesafe = false;
		boolean backslashsafe = false;
		boolean safeappend = false;
		int savedind = 0;
		
		boolean cleanup = false;
		int cleanup_amount = config.methodBuffer();
		
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
					posTracker.nextCharacter();
					switch (c) {
						case '%':
							compileState = CONDITION_STATE;
							finalCodeBlock.appendStringConstant(elemBuilder.toString(), posTracker.get());
							elemBuilder.setLength(0);
							savedind = ind;
							break;
						case '{':
							compileState = VARIABLE_STATE;
							finalCodeBlock.appendStringConstant(elemBuilder.toString(), posTracker.get());
							elemBuilder.setLength(0);
							bracketscount = 1;
							savedind = ind;
							break;
						case ';':
							compileState = METHOD_STATE;
							finalCodeBlock.appendStringConstant(trimMethod(elemBuilder.toString()), posTracker.get());
							elemBuilder.setLength(0);
							savedind = ind;
						    quotesafe = false;
						    backslashsafe = false;
							break;
						case '#':
							compileState = HASHTAG_STATE;
							finalCodeBlock.appendStringConstant(elemBuilder.toString(), posTracker.get());
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
							finalCodeBlock.addInstruction(new ConditionInstruction(filename, conds, this,
									 posTracker.goToAndGet(savedind)));
							compileState = TEXT_STATE;
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
							finalCodeBlock.addInstruction(new VariableInstruction(this, elemBuilder.toString(),
									posTracker.goToAndGet(savedind)));
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
						finalCodeBlock.addInstruction(new MethodExecutionInstruction(builder, this, posTracker.goToAndGet(savedind)));
						elemBuilder.setLength(0);
						cleanup = true;
						cleanup_amount = config.methodBuffer()/2;
					}
					break;
				}
				case HASHTAG_STATE: {
					compileState = TEXT_STATE;
					var pos = posTracker.goToAndGet(savedind);
					
					Instruction instructions = getInstruction(text, ind);
					ind = instructions.ending_index();
					
					CodeBlock codeBlock = getCodeBlock(text, ind);
					ind = codeBlock.ending_index();

					byte instruction_code = instructions.instruction();
					String parameters = instructions.paremeter();
					String block = codeBlock.code();
					
					switch (instruction_code) {
						case FOR_LOOP_INSTRUCTION: {
							String[] split = parameters.split(" in ", 2);
							if (split.length<2) 
								throw new CompileException("Invalid for loop syntax: \"" + parameters + "\"", pos);
							String variablename = split[0];
							String value = split[1];
							elemBuilder.setLength(0);
							finalCodeBlock.addInstruction(new ForInstruction(variablename, value, block, this,
									filename, pos));
							break;
						}
						case DEFINE_INSTRUCTION: {
							String[] builder = HudderUtils.processParemeters(parameters);
							String name = builder[0];
							String[] args = Arrays.copyOfRange(builder, 1, builder.length);
							finalCodeBlock.addInstruction(new DefineInstruction(block, args, name, filename,
									this, pos));
							elemBuilder.setLength(0);
							break;
						}
						case WHILE_LOOP_INSTRUCTION: {
							finalCodeBlock.addInstruction(new WhileInstruction(parameters, block, this,
									filename, pos));
							break;
						}
						case UNDEFINED_INSTRUCTION:
							Hudder.showWarningToast(Component.literal("Undefined # instructions deprecated"),
									Component.literal("Undefined # instructions are deprecated and will be "
											+ "removed in a future version of Hudder, please use #if instead."));
						case IF_INSTRUCTION:// If instuction
							List<Statement> statements = new ArrayList<Statement>();
							statements.add(new Statement(parameters, block));
							for (;ind<text.length();ind++) {
								c = text.charAt(ind);
								if (c=='#') {
									savedind = ind;
									ind++;
									instructions = getInstruction(text, ind);
									ind = instructions.ending_index();
									if (instructions.instruction()!=ELSE_INSTRUCTION
											&&instructions.instruction()!=ELSE_IF_INSTRUCTION) {
										//We've gone deep into another hash instruction, go back.
										ind = savedind;
										break;
									}
									codeBlock = getCodeBlock(text, ind);
									ind = codeBlock.ending_index();
									parameters = instructions.paremeter();
									block = codeBlock.code();
									statements.add(new Statement(parameters, block));
								} else if (!Character.isWhitespace(c)) {
									break;
								}
							}
							ind--;
							finalCodeBlock.addInstruction(new IfElseInstuction(
									statements.toArray(Statement[]::new), filename, this, pos));
							break;
						default:
							throw new CompileException("Detached else/else if statement!", pos);
					}
					break;
				}
				default: {
					var pos = posTracker.goToAndGet(savedind);
					throw new CompileException("Unknown compile state: " + compileState, pos);
				}
			}
		}
		
		finalCodeBlock.appendStringConstant(elemBuilder.toString(), posTracker.goToAndGet(text.length()-1));
		
		if (compileState!=0) {
			throw new CompileException(getCompilerErrorMessage(compileState), posTracker.goToAndGet(text.length()-1));
		}
		
		return finalCodeBlock;
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
						(text.length()>ind+1&&text.charAt(ind+1)=='\n')){instruction=ELSE_INSTRUCTION;}
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
			case CONDITION_STATE -> "Expected '%'";
			case METHOD_STATE -> "Expected ';'";
			case HASHTAG_STATE -> "Expected end of HASHTAG_STATE";
			default -> "An unknown error has occurred";
		});
		return strb.toString();
	}
	public String trimMethod(String string) {
		String str = string;
		int buffer;
		if ((buffer = config.methodBuffer())<10) {
			for (int i = 0; i<buffer;i++) {
				if (str.endsWith("\n")||str.endsWith("\r")) str = str.substring(0, str.length()-1);
			}
		}
		return str;
	}
	
	@Override
	public String[] getSupportedFileFormats() {
		return new String[] {"hud"};
	}
	
	private record CodeBlock(String code, String text, int starting_index, int ending_index) {}
	private record Instruction(byte instruction, String paremeter, int ending_index) {}
}
