package dev.ngspace.hudder.hudderv3.instructions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.instructions.variables.FunctionCallVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.SystemVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
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
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.ComparisionVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.LogicalAndVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.LogicalOrVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.NegateVariableVisitor;
import dev.ngspace.hudder.utils.HudderUtils;

public class ImplV3VariableProcessor implements V3VariableProcessor {
	
	public VariableVisitor parseVariable(String valuee, AV3Compiler comp, TextPos pos) throws CompileException {
		
		String value = valuee.trim();
		
		// Empty variable
		if (value.isBlank())
			throw new CompileException("Empty variable", pos);
		
		// Is wrapped in parenthesses? get rid of em!
		if (value.startsWith("(") && value.endsWith(")")) {
			
			// Assume that it's good
			boolean isSafe = true;
			
			boolean quotes = false;
			boolean backslash = false;
			
			// Count how deep the parenthesses
			// NOTE it is initalized at 0 but since the first char is always going to be (,
			// it will become one.
			int layers = 0;
			
			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				
				if (quotes) {
					if (!backslash) {
						if (c == '"')
							quotes = false;
						if (c == '\\') {
							backslash = !backslash;
							continue;
						}
					}
					backslash = false;
					continue;
				}
				
				if (c == '"')
					quotes = true;
				if (c == '(')
					layers++; // Layer up
				if (c == ')')
					layers--; // Layer down
					
				if (layers == 0) { // We reached the closing parenthesses of the first (.
					
					// Is there more chars to read? if so then the string is not wrapped and
					// therefore shouldn't be unwrapped.
					isSafe = i + 1 == value.length();
					break;
				}
			}
			// if it is wrapped then remove the first and last chars to unwrap and reprocess
			// them.
			if (isSafe) {
				return parseVariable(value.substring(1, value.length() - 1), comp, pos);
			}
		}
		
		if (value.startsWith("if ")) {
			StringBuilder condition = new StringBuilder();
			int index = 3;
			int parentheses = 0;
			for (; index < value.length(); index++) {
				char c = value.charAt(index);
				if (c == '"') {
					boolean escape = false;
					condition.append(c);
					index++;
					for (; index < value.length(); index++) {
						c = value.charAt(index);
						condition.append(c);
						if (c == '"' && !escape) {
							break;
						}
						if (c == '\\' && !escape) {
							escape = true;
						}
					}
					continue;
				}
				if (parentheses == 0 && value.indexOf(" then ", index) == index)
					break;
				if (c == '(')
					parentheses++;
				if (c == ')')
					parentheses--;
				
				condition.append(c);
			}
			index += 5;
			parentheses = 0;
			StringBuilder firstvalue = new StringBuilder();
			for (; index < value.length(); index++) {
				char c = value.charAt(index);
				if (c == '"') {
					boolean escape = false;
					firstvalue.append(c);
					index++;
					for (; index < value.length(); index++) {
						c = value.charAt(index);
						firstvalue.append(c);
						if (c == '"' && !escape) {
							break;
						}
						if (c == '\\' && !escape) {
							escape = true;
						}
					}
					continue;
				}
				if (parentheses == 0 && value.indexOf(" else ", index) == index)
					break;
				if (c == '(')
					parentheses++;
				if (c == ')')
					parentheses--;
				
				firstvalue.append(c);
			}
			index += 6;
			return new TernaryVariableVisitor(comp, condition.toString(), firstvalue.toString(), value.substring(index),
					pos);
		}
		
		// Double constant
		// Accepts the following formats: "0x(0-F)+", "#(0-F)+", "(0-9)+",
		// "(0-9)*.(0-9)+"
		if (value.matches("((0x|#)[\\daAbBcCdDeEfF]+|[-+]?\\d*(\\.?(\\d+)?))")) {
			return new NumberVariableVisitor(comp, value, pos);
		}
		
		// Boolean constants
		if (value.equalsIgnoreCase("false")) {
			return new BooleanVariableVisitor(comp, false, pos);
		}
		if (value.equalsIgnoreCase("true")) {
			return new BooleanVariableVisitor(comp, true, pos);
		}
		
		// String constant
		String temp = string(value);
		if (temp != null) {
			return new StringVariableVisitor(comp, temp, pos);
		}
		
		// Array constant
		// Accepts the follow format: "[(any char)]"
		if (value.matches("\\[[\\s\\S]*\\]")) {
			
			// Sends the text between the square brackets to HudderUtils.processParemeters
				// to tokenize the values.
			
			return new ArrayConstantVariableVisitor(
					HudderUtils.processParemeters(value.substring(1, value.length() - 1).replace("\n", "")), comp, pos);
		}
		
		// Set variable
		String[] setValues = value.split("=", 2);// Split at the first '='
		// Make sure it's not a condition!
		if (setValues.length == 2 && !isCondition(value)) {
			
			boolean valid = true;
			
			boolean escaped = false;
			for (int i = 0; i < setValues[0].length(); i++) {
				char c = setValues[0].charAt(i);
				if (c == '\\') {
					escaped = true;
				}
				if (escaped)
					continue;
				if (c == '"') {
					valid = false;
					break;
				}
			}
			if (valid) {
				return new SetVariableVisitor(comp, setValues[0], setValues[1], pos);
			}
		}
		
		// Is it a variable name that does not start with _?
		boolean matchesVariableRegex = value.matches("[A-Za-z\\d][A-Za-z\\d_]*");
		
		// System variable
		if (comp.system_variables && matchesVariableRegex
				&& DataVariableRegistry.hasVariable(value.toLowerCase())) {
			return new SystemVariableVisitor(comp, value.toLowerCase(), pos);
		}
		
		// Dynamic variable
		if (matchesVariableRegex) {
			return new DynamicVariableVisitor(comp, value.toLowerCase(), pos);
		}
		
		// Temp dynamic variable
		// Is it a variable name that starts with _?
		if (value.matches("_[A-Za-z\\d_]*"))
			return new TemporaryVariableVisitor(comp, value, pos);
			
		// Read Array
		// Accepts the following format: "(any char)+(space)?[(any char)]".
		if (value.matches(".+ *\\[.+\\]"))
			return new ArrayReadVariableVisitor(comp, value, pos);
		
		// Function variable
		if (!value.startsWith("(") && value.endsWith(")")) {
			// Same thing as before except we start reading at the first instance of a '('
			// char instead of at index 0.
			int argStart = value.indexOf("(");
			boolean isSafe = false;
			boolean quotes = false;
			int parenthesses = 0;
			if (argStart != -1) {
				isSafe = true;
				for (int i = argStart; i < value.length(); i++) {
					char c = value.charAt(i);
					if (c == '"') {
						quotes = !quotes;
					}
					if (quotes) {
						continue;
					}
					if (c == '(')
						parenthesses++;
					if (c == ')')
						parenthesses--;
					if (parenthesses == 0) {
						isSafe = i + 1 == value.length();
						break;
					}
				}
				if (isSafe) {
					String funcName = value.substring(0, argStart);
					if (funcName.matches("^[a-zA-Z0-9]+[a-zA-Z0-9_-]*$")) {
						String parametersString = value.substring(argStart + 1, value.length() - 1);
						return new FunctionCallVariableVisitor(funcName, comp,
								HudderUtils.processParemeters(parametersString), pos);
					}
				}
			}
		}
		
		// Logical OR operator
		List<VariableVisitor> orValues = logicalOperator('|', value, comp, pos);
		if (orValues.size() > 1)
			return new LogicalOrVariableVisitor(orValues, comp, pos);
		
		// Logical AND operator
		List<VariableVisitor> andvalues = logicalOperator('&', value, comp, pos);
		if (andvalues.size() > 1)
			return new LogicalAndVariableVisitor(andvalues, comp, pos);
		
		// Comparing values
		String operator = getOperator(value);
		if (operator != null) {
			int parenthesses = 0;
			boolean quotes = false;
			boolean backslash = false;
			String[] v = value.split(operator, 2);
			for (char c : v[0].trim().toCharArray()) {
				if (quotes) {
					if (!backslash) {
						if (c == '"')
							quotes = false;
						if (c == '\\')
							backslash = true;
					}
				} else {
					if (c == '"')
						quotes = true;
					if (c == '(')
						parenthesses++;
					if (c == ')')
						parenthesses--;
				}
			}
			if (parenthesses == 0) {
				return new ComparisionVariableVisitor(comp, v[0], v[1], operator, pos);
			}
		}
		
		// Post Increase and Decrease Operator
		if (value.matches("[\\s\\S]+(\\+\\+|--)")) {
			return new PostIncDecVariableVisitor(value.substring(0, value.length() - 2), comp,
					"+".equals(value.substring(value.length() - 1)), pos);
		}
		
		// Pre Increase and Decrease Operator
		if (value.matches("(\\+\\+|--)[\\s\\S]+")) {
			return new PreIncDecVariableVisitor(value.substring(2), comp, "+".equals(value.substring(0, 1)), pos);
		}
		
		// Math operation
		List<String> values = new ArrayList<String>();
		StringBuilder mathvalue = new StringBuilder();
		ArrayList<Character> operations = new ArrayList<Character>();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '"' && mathvalue.isEmpty()) {
				boolean safe = false;
				i++;
				mathvalue.append(c);
				for (; i < value.length(); i++) {
					c = value.charAt(i);
					if (c == '\\' && !safe) {
						safe = true;
						mathvalue.append(c);
					} else {
						safe = false;
						mathvalue.append(c);
						if (c == '"' && !safe)
							break;
					}
				}
				continue;
			}
			if (c == '(') {
				int parentheses = 1;
				mathvalue.append(c);
				i++;
				for (; i < value.length(); i++) {
					c = value.charAt(i);
					if (c == '(')
						parentheses++;
					if (c == ')') {
						parentheses--;
						if (parentheses == 0) {
							mathvalue.append(c);
							break;
						}
					}
					mathvalue.append(c);
				}
				continue;
			}
			if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
				if (mathvalue.toString().isBlank() && c == '-') {
					mathvalue.append(c);
					continue;
				}
				if (mathvalue.toString().isBlank()) {// Do not trigger
					values = null;
					break;
				}
				values.add(mathvalue.toString());
				operations.add(c);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		if (values!=null&&!values.isEmpty()) {
			values.add(mathvalue.toString());
			return new MathVariableVisitor(values, operations, comp, pos);
		}
		
		// Class
		String classyobjname = "";
		String functionOrObject = "";
		for (int i = 1; i < value.length(); i++) {
			char c = value.charAt(value.length() - i);
			if (c == ')') {
				int parentheses = 0;
				for (; i < value.length() + 1; i++) {
					c = value.charAt(value.length() - i);
					if (c == ')')
						parentheses++;
					if (c == '(')
						parentheses--;
					functionOrObject = c + functionOrObject;
					if (parentheses == 0)
						break;
				}
				continue;
			}
			
			if (c == '"') {
				boolean isnotescaped = false;
				for (; i < value.length() + 1; i++) {
					c = value.charAt(value.length() - i);
					functionOrObject = c + functionOrObject;
					if (i + 2 < value.length() + 1)
						isnotescaped = value.charAt(value.length() - i) == '\\';
					if (c == '"' && !(i + 1 < value.length() + 1 && value.charAt(value.length() - i) == '\\')
							&& isnotescaped)
						break;
				}
				continue;
			}
			
			if (c == '.') {
				classyobjname = value.substring(0, value.length() - i);
				for (int j = 1; j < classyobjname.length(); j++) {
					char cc = classyobjname.charAt(classyobjname.length() - j);
					if (Character.isDigit(cc))
						continue;
					if (cc == '*' || cc == '+' || cc == '-' || cc == '/' || cc == '%') {
						classyobjname = "";
						functionOrObject = "";
						break;
					} else {
						break;
					}
				}
				break;
			}
			functionOrObject = c + functionOrObject;
		}
		
		if (!Objects.equals(functionOrObject, value) && !"".equals(classyobjname)) {
			return new ClassAccessVariableVisitor(comp, classyobjname, functionOrObject, pos);
		}
		
		// ! Operator
		if (value.matches("![\\s\\S]+"))
			return new NegateVariableVisitor(comp, value.substring(1), pos);
		
		// Fallback
		throw new CompileException("Untokenizable variable: " + value, pos);
	}
	
	private List<VariableVisitor> logicalOperator(char op, String value, AV3Compiler comp, TextPos pos)
			throws CompileException {
		List<VariableVisitor> values = new ArrayList<VariableVisitor>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '"' && builder.isEmpty()) {
				boolean safe = false;
				i++;
				builder.append(c);
				for (; i < value.length(); i++) {
					c = value.charAt(i);
					if (c == '\\' && !safe)
						safe = true;
					else {
						safe = false;
						builder.append(c);
						if (c == '"' && !safe)
							break;
					}
				}
				continue;
			}
			if (c == '(' && builder.isEmpty()) {
				int parentheses = 1;
				i++;
				for (; i < value.length(); i++) {
					c = value.charAt(i);
					if (c == '(')
						parentheses++;
					if (c == ')')
						parentheses--;
					if (parentheses == 0)
						break;
					builder.append(c);
				}
				continue;
			}
			if (c == op && i + 1 < value.length() && value.charAt(i + 1) == op) {
				i++;
				values.add(parseVariable(builder.toString(), comp, pos));
				builder.setLength(0);
				continue;
			}
			
			builder.append(c);
		}
		if (!Objects.equals(value, builder.toString())) {
			values.add(parseVariable(builder.toString(), comp, pos));
			return values;
		} else
			return values;
	}
	
	private static String string(String value) {
		// Maybe String :)
		if (!value.startsWith("\"") || !value.endsWith("\""))
			return null;
		
		// Probably String :D
		value = value.substring(1, value.length() - 1);
		StringBuilder string = new StringBuilder();
		
		boolean safe = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == 'n' && safe) {
				string.append('\n');
				continue;
			}
			if (c == '\\' && !safe)
				safe = true;
			else {
				if (c == '"' && !safe)
					return null; // Not String ;_;
				safe = false;
				string.append(c);
			}
		}
		// String! :D
		return string.toString();
	}
	
	private static String getOperator(String condString) {
		if (condString.contains("=="))
			return "==";
		if (condString.contains("!="))
			return "!=";
		if (condString.contains(">="))
			return ">=";
		if (condString.contains("<="))
			return "<=";
		if (condString.contains(">"))
			return ">";
		if (condString.contains("<"))
			return "<";
		return null;
	}
	
	private static boolean isCondition(String key) {
		int i = key.indexOf('=');
		if (i == -1 && !key.contains(">") && !key.contains("<"))
			return false;
		if (i == key.length() - 1)
			return false;
		if (i == 0)
			return false;
		char pre = key.charAt(i - 1);
		return pre == '<' || pre == '>' || pre == '!' || key.charAt(i + 1) == '=';
	}
}
