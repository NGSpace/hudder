package dev.ngspace.hudder.hudderv3.instructions;

import java.util.ArrayList;
import java.util.List;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.instructions.variables.FunctionCallVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.SystemVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.ArrayConstantVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.BooleanVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.NumberVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.StringVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.ArrayReadVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.DynamicVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.SetVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.TemporaryVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.ClassAccessVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.MathVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.PostIncDecVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.PreIncDecVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.TernaryVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.ComparisonVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.LogicalAndVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.LogicalOrVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.NegateVariableVisitor;
import dev.ngspace.hudder.utils.HudderUtils;

public class ImplV3ExpressionParser implements V3ExpressionParser {

	@Override
	public ExpressionVisitor parseExpression(String valuee, AV3Compiler comp, TextPos pos)
			throws CompileException {
		
		String value = valuee.trim();
		
		// Empty variable
		if (value.isBlank())
			throw new CompileException("Empty variable", pos);
		
		// Array constant
		// Accepts the follow format: "[(any char)]"
		if (value.matches("\\[[\\s\\S]*\\]"))
			return new ArrayConstantVariableVisitor(
					HudderUtils.processParemeters(value.substring(1, value.length() - 1).replace("\n", "")),
					comp, pos, value);
		
		// Boolean constants
		if (value.equalsIgnoreCase("false"))
			return new BooleanVariableVisitor(comp, false, pos, value);
		if (value.equalsIgnoreCase("true"))
			return new BooleanVariableVisitor(comp, true, pos, value);
		
		int len = value.length();
		
		char c = value.charAt(0);

		int parenthesses = c=='('?1:0;
		int square_parenthesses = c=='['?1:0;
		boolean escaped = false;
		
		boolean can_wrapped = parenthesses==1;
		
		boolean can_dynamic = isAlphaNumeric(c);
		boolean can_temp = c == '_';
		
		boolean can_0x = c == '0' && len>2;
		boolean can_hash = c == '#' && len>1;
		boolean can_number = Character.isDigit(c) || c=='.' || c=='-' || c=='+';

		boolean quotes = c == '"';
		boolean can_string = quotes;
		StringBuilder string = new StringBuilder();
		
		boolean can_set = false;
		int set_index = -1;
		
		boolean can_class = false;
		int class_dot = -1;
		
		boolean can_function = value.charAt(len-1)==')' && isAlphaNumeric(c);
		int function_args_index = -1;
		
		boolean can_math = false;
		List<String> math_values = new ArrayList<String>();
		List<Character> math_operators = new ArrayList<Character>();
		int math_last_index = 0;
		
		boolean can_comparision = false;
		String comparision_operator = "";
		int comparision_index = -1;
		
		boolean can_array_read = false;
		
		boolean can_or = false;
		boolean has_vertical_bar = false;
		List<ExpressionVisitor> or_values = new ArrayList<ExpressionVisitor>();
		int or_last_index = 0;
		
		boolean can_and = false;
		boolean has_ampersand = false;
		List<ExpressionVisitor> and_values = new ArrayList<ExpressionVisitor>();
		int and_last_index = 0;
		
		boolean can_ternary = value.startsWith("if ");
		int ternary_then_index = -1;
		int ternary_else_index = -1;
		
		for (int i = 1;i<value.length();i++) {
			c = value.charAt(i);
			
			if (escaped&&quotes) {
				string.setLength(string.length()-1);
				if (c=='n'&&escaped&&quotes)
					string.append('\n');
				else
					string.append(c);
			} else
				string.append(c);
			
			if (c=='"'&&!escaped) {
				quotes = !quotes;
				if (i!=len-1) {
					can_string = false;
				}
			}
			if (c=='\\'&&!escaped) {
				escaped = true;
			} else {
				escaped = false;
			}

			if (!quotes&&square_parenthesses==0&&c=='(') {
				if (parenthesses==0) {
					function_args_index = i;
				}
				parenthesses++;
			}
			if (!quotes&&square_parenthesses==0&&c==')') {
				parenthesses--;
				if (parenthesses==0&&i!=len-1) {
					can_wrapped=false;
					can_function=false;
				}
			}
			if (!quotes&&parenthesses==0&&c=='[') {
				if (parenthesses==0&&value.charAt(len-1)==']')
					can_array_read = true;
				
				square_parenthesses++;
			}
			if (!quotes&&parenthesses==0&&c==']') {
				square_parenthesses--;
			}

			if (can_ternary) {
				if (!quotes&&parenthesses==0&&square_parenthesses==0) {
					if (ternary_then_index==-1&&value.indexOf(" then ", i)==i) {
						ternary_then_index = i;
						i += " then ".length()-1;
					} else if (ternary_then_index!=-1&&ternary_else_index==-1
							&&value.indexOf(" else ", i)==i) {
						ternary_else_index = i;
						i += " else ".length()-1;
					}
				}
				continue;
			}
			
			if (!quotes&&parenthesses==0&&c=='.') {
				can_class = true;
				class_dot = i;
			}
			
			if (!quotes&&parenthesses==0&&c=='|') {
				if (!has_vertical_bar) {
					has_vertical_bar = true;
				} else {
					can_or = true;
					or_values.add(parseExpression(value.substring(or_last_index, i-1), comp, pos));
					or_last_index = i+1;
					has_vertical_bar = false;
				}
			} else {
				has_vertical_bar = false;
			}
			
			if (!quotes&&parenthesses==0&&c=='&') {
				if (!has_ampersand) {
					has_ampersand = true;
				} else {
					can_and = true;
					and_values.add(parseExpression(value.substring(and_last_index, i-1), comp, pos));
					and_last_index = i+1;
					has_ampersand = false;
				}
			} else {
				has_ampersand = false;
			}
			
			if (!quotes&&parenthesses==0&&((c=='='&&set_index==i-1)
					||(len>i+1&&(c=='!'&&value.charAt(i+1)=='='))||c=='<'||c=='>')) {
				if (len>i+1) {
					can_comparision = true;
					comparision_operator = c + (value.charAt(i+1)=='='||can_set?"=":"");
					comparision_index = can_set? i-1 : i;
				}
				can_set = false;
			}
			
			if (!quotes&&parenthesses==0&&c=='='&&set_index==-1&&!can_comparision) {
				can_set = len>2;
				set_index = i;
			}
			
			if (!quotes&&parenthesses==0&&isMathOperator(c) && i > math_last_index) {
				can_math = true;
				math_operators.add(c);
				math_values.add(value.substring(math_last_index, i));
				math_last_index = i + 1;
			}
			
			if (!quotes&&parenthesses==0&&!isValidFunctionDigit(c)&&i!=len-1) {
				can_function = false;
			}
			
			if (!isAlphaNumeric(c)&&c!='_') {
				can_dynamic = false;
				can_temp = false;
			}
			if (i==1&&c!='x')
				can_0x = false;
			if (!Character.isDigit(c)) {
				if (c!='.')
					can_number = false;
				if (!isHexDigit(c)) {
					can_hash = false;
					if (i>1)
						can_0x = false;
				}
			}
		}
		if (can_ternary&&ternary_then_index!=-1&&ternary_else_index!=-1)
			return new TernaryVariableVisitor(comp,
					value.substring(3, ternary_then_index),
					value.substring(ternary_then_index+" then ".length(), ternary_else_index),
					value.substring(ternary_else_index+" else ".length()), pos, value);
		
		if (can_0x||can_hash||can_number)
			return new NumberVariableVisitor(comp, value, pos, value);
		
		if (can_set)
			return new SetVariableVisitor(comp, value.substring(0, set_index), value.substring(set_index+1),
					pos, value);

		if (can_or) {
			or_values.add(parseExpression(value.substring(or_last_index, len), comp, pos));
			return new LogicalOrVariableVisitor(or_values, comp, pos, value);
		}
		
		if (can_and) {
			and_values.add(parseExpression(value.substring(and_last_index, len), comp, pos));
			return new LogicalAndVariableVisitor(and_values, comp, pos, value);
		}
		
		// ! Operator
		if (value.charAt(0)=='!')
			return new NegateVariableVisitor(comp, value.substring(1), pos, value);
		
		if (can_comparision)
			return new ComparisonVariableVisitor(comp, value.substring(0, comparision_index),
					value.substring(comparision_index+comparision_operator.length()),
					comparision_operator, pos, value);
		
		// Post Increase and Decrease Operator
		if (value.matches("[\\s\\S]+(\\+\\+|--)")) {
			return new PostIncDecVariableVisitor(value.substring(0, value.length() - 2), comp,
					"+".equals(value.substring(value.length() - 1)), pos, value);
		}
		
		// Pre Increase and Decrease Operator
		if (value.matches("(\\+\\+|--)[\\s\\S]+")) {
			return new PreIncDecVariableVisitor(value.substring(2), comp, "+".equals(value.substring(0, 1)),
					pos, value);
		}
		
		if (can_array_read)
			return new ArrayReadVariableVisitor(comp, value, pos, value);
		
		if (can_math) {
			math_values.add(value.substring(math_last_index));
			return new MathVariableVisitor(math_values, math_operators, comp, pos, value);
		}
		
		if (can_class)
			return new ClassAccessVariableVisitor(comp, value.substring(0, class_dot),
					value.substring(class_dot+1), pos, value);

		if (can_wrapped&&parenthesses==0)
			return parseExpression(value.substring(1, value.length() - 1), comp, pos);
		
		if (can_function&&parenthesses==0)
			return new FunctionCallVariableVisitor(value.substring(0, function_args_index), comp,
					HudderUtils.processParemeters(value.substring(function_args_index+1,len-1)), pos, value);
		
		if (can_string&&!quotes)
			return new StringVariableVisitor(comp, string.substring(0, string.length()-1)
					.replace("\\\"", "\""), pos, value);
		
		if (can_temp)
			return new TemporaryVariableVisitor(comp, value, pos, value);
		
		if (can_dynamic) {
			// System variable
			if (comp.system_variables && DataVariableRegistry.hasVariable(value.toLowerCase()))
				return new SystemVariableVisitor(comp, value.toLowerCase(), pos, value);
			// Dynamic variable
			return new DynamicVariableVisitor(comp, value.toLowerCase(), pos, value);
		}
		
		
		// Fallback
		throw new CompileException("Untokenizable variable: " + value, pos);
	}
	
	static boolean isHexDigit(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
	}
	
	static boolean isValidFunctionDigit(char c) {
		return isAlphaNumeric(c)||c=='_'||c=='-';
	}

	static boolean isAlphaNumeric(char c) {
		return Character.isAlphabetic(c)||Character.isDigit(c);
	}
	
	static boolean isMathOperator(char c) {
		return c == '*' || c == '+' || c == '-' || c == '/' || c == '%';
	}
}
