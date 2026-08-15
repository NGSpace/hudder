package dev.ngspace.hudder.compilers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.HudderUtils;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.runtime_elements.BreakV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.ConditionV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.ForV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.IfElseV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.IfElseV2RuntimeElement.Statement;
import dev.ngspace.hudder.v2runtime.runtime_elements.MethodV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.ReturnV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.StringV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.VariableV2RuntimeElement;
import dev.ngspace.hudder.v2runtime.runtime_elements.WhileV2RuntimeElement;
import net.minecraft.network.chat.Component;

public class HudderV2Compiler extends AV2Compiler {
	
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

	@Override public V2Runtime buildRuntime(HudderConfig info, String text, TextPos charPosition, String filename,
			V2Runtime scope) throws CompileException, ExecutionException {
		V2Runtime runtime = new V2Runtime(this, info, scope);
		
		StringBuilder elemBuilder = new StringBuilder();
		
		int bracketscount = 0;

		boolean quotesafe = false;
		boolean backslashsafe = false;
		boolean safeappend = false;
		int savedind = 0;
		
		boolean cleanup = false;
		int cleanup_amount = info.methodBuffer();
		
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
							savedind = ind;
						    quotesafe = false;
						    backslashsafe = false;
							break;
						case '#':
							compileState = HASHTAG_STATE;
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
							var pos = getPosition(charPosition, savedind, text);
							if ("break".equalsIgnoreCase(elemBuilder.toString().trim())) {
								runtime.addRuntimeElement(new BreakV2RuntimeElement());
							} else {
								runtime.addRuntimeElement(new VariableV2RuntimeElement(elemBuilder.toString(), this,
									runtime, pos.line(), pos.column()));
							}
							elemBuilder.setLength(0);
							compileState = TEXT_STATE;
						} else elemBuilder.append(c);
					} else elemBuilder.append(c);
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
							var pos = getPosition(charPosition, savedind, text);
							conds.add(conditionOrValue.toString());
							runtime.addRuntimeElement(new ConditionV2RuntimeElement(
									conds.toArray(new String[conds.size()]), this, info,
									runtime, pos.line(), pos.column(),filename));
							compileState = TEXT_STATE;
							break;
						} else {
							conditionOrValue.append(c);
						}
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
						String[] builder = HudderUtils.processParemeters(elemBuilder.toString());
						var pos = getPosition(charPosition, savedind, text);
						int line = pos.line();
						int charpos = pos.column();
						if (builder[0].toLowerCase().trim().equals("no_sys_var")) {
							SYSTEM_VARIABLES_ENABLED = false;
						} else if (builder[0].toLowerCase().trim().equals("sys_var")) {
							SYSTEM_VARIABLES_ENABLED = true;
						} else if (builder.length==2&&builder[0].toLowerCase().trim().equals("return")) {
							runtime.addRuntimeElement(new ReturnV2RuntimeElement(builder[1],this,runtime,line,charpos));
						} else {
							runtime.addRuntimeElement(new MethodV2RuntimeElement(builder,this,info,runtime,line,charpos));
						}
						elemBuilder.setLength(0);
						cleanup = true;
						cleanup_amount = info.methodBuffer()/2;
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
					TextPos pos = getPosition(charPosition, savedind+1, "\n"+text);
					
					switch (instruction_code) {
						case FOR_LOOP_INSTRUCTION: {
							String[] split = parameters.split(" in ", 2);
							if (split.length<2) 
								throw new CompileException("Invalid for loop syntax: \"" + parameters + "\"", pos);
							String variablename = split[0];
							String value = split[1];
							elemBuilder.setLength(0);
							runtime.addRuntimeElement(new ForV2RuntimeElement(info, variablename, value,
									block, this, runtime, pos, filename));
							break;
						}
						case DEFINE_INSTRUCTION: {
							String[] builder = HudderUtils.processParemeters(parameters);
							String name = builder[0];
							String[] args = Arrays.copyOfRange(builder, 1, builder.length);
							defineFunctionOrMethod(block,args,name,pos,filename);
							elemBuilder.setLength(0);
							break;
						}
						case WHILE_LOOP_INSTRUCTION: {
							runtime.addRuntimeElement(new WhileV2RuntimeElement(info, parameters, block, this,
									runtime, pos, filename));
							break;
						}
						case UNDEFINED_INSTRUCTION:
							Hudder.showWarningToast(Component.literal("Undefined # instructions deprecated"),
									Component.literal("Undefined # instructions are deprecated and will be "
											+ "removed in a future version of Hudder, please use #if instead."));
						case IF_INSTRUCTION:// If instuction
							List<Statement> statements = new ArrayList<Statement>();
							statements.add(new Statement(parameters, block, pos));
							for (;ind<text.length();ind++) {
								c = text.charAt(ind);
								if (c=='#') {
									savedind = ind;
									pos = getPosition(charPosition, ind, "\n"+text);
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
									statements.add(new Statement(parameters, block, pos));
								} else if (!Character.isWhitespace(c)) {
									break;
								}
							}
							ind--;
							runtime.addRuntimeElement(new IfElseV2RuntimeElement(info,
									statements.toArray(new Statement[statements.size()]),
									runtime,
									filename,
									this));
							break;
						default:
							throw new CompileException("Detached else/else if statement!", pos);
					}
					break;
				}
				default: {
					var pos = getPosition(charPosition, savedind, text);
					throw new CompileException("Unknown compile state: " + compileState, pos);
				}
			}
		}
		
		runtime.addRuntimeElement(new StringV2RuntimeElement(elemBuilder.toString(), false));
		
		if (compileState!=0) {
			var pos = getPosition(charPosition, savedind, text);
			throw new CompileException(getCompilerErrorMessage(compileState), pos);
		}
		
		return runtime;
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
			case CONDITION_STATE -> "Expected '%'";
			case METHOD_STATE -> "Expected ';'";
			case HASHTAG_STATE -> "Expected end of ADVANCED_CONDITION_STATE";
			default -> "An unknown error has occurred";
		});
		return strb.toString();
	}
	
	@Override
	public String[] getSupportedFileFormats() {
		return new String[] {"hud"};
	}
}