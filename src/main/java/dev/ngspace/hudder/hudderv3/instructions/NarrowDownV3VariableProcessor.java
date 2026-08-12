package dev.ngspace.hudder.hudderv3.instructions;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.instructions.variables.FunctionCallVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.SystemVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.BooleanVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.NumberVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.StringVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.DynamicVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.SetVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.TemporaryVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.ClassAccessVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.NegateVariableVisitor;
import dev.ngspace.hudder.utils.HudderUtils;

public class NarrowDownV3VariableProcessor implements V3VariableProcessor {

	@Override
	public VariableVisitor parseVariable(String valuee, AV3Compiler comp, TextPos pos)
			throws CompileException {
		
		String value = valuee.trim();
		
		// Empty variable
		if (value.isBlank())
			throw new CompileException("Empty variable", pos);
		
		System.out.println(value);
		
		// Boolean constants
		if (value.equalsIgnoreCase("false"))
			return new BooleanVariableVisitor(comp, false, pos);
		if (value.equalsIgnoreCase("true"))
			return new BooleanVariableVisitor(comp, true, pos);
		
		int len = value.length();
		
		char c = value.charAt(0);
		
		int parenthesses = c=='('?1:0;
		
		boolean can_wrapped = parenthesses==1;
		
		boolean can_dynamic = isAlphaNumeric(c);
		boolean can_temp = c == '_' && len>1;
		
		boolean can_0x = c == '0' && len>2;
		boolean can_hash = c == '#' && len>1;
		boolean can_number = Character.isDigit(c) || c=='.';

		boolean quotes = c == '"';
		boolean can_string = quotes && len>2;
		
		boolean can_set = false;
		int set_index = -1;
		
		boolean can_class = false;
		int class_dot = -1;
		
		boolean can_function = value.charAt(len-1)==')';
		int function_args_index = -1;
		
		for (int i = 1;i<value.length();i++) {
			c = value.charAt(i);
			
			if (c=='"') {
				quotes = !quotes;
				if (i!=len-1) {
					can_string = false;
				}
			}
			
			if (!quotes&&c=='(') {
				if (parenthesses==0) {
					System.out.println("par" + i);
					function_args_index = i;
				}
				parenthesses++;
			}
			if (!quotes&&c==')') {
				parenthesses--;
				if (i!=len-1) {
					can_wrapped=false;
					can_function=false;
				}
			}
			
			if (!quotes&&parenthesses==0&&c=='.') {
				can_class = true;
				class_dot = i;
			}
			
			if (!quotes&&parenthesses==0&&c=='=') {
				can_set = len>2;
				set_index = i;
			}
			
			if (!isAlphaNumeric(c)) {
				can_dynamic = false;
				can_temp = false;
			}
			if (i==1&&c!='x')
				can_0x = false;
			if (!Character.isDigit(c)) {
				if (c!='.')
					can_number = false;
				can_hash = false;
				if (i>1)
					can_0x = false;
			}
		}
		
		if (can_0x||can_hash||can_number)
			return new NumberVariableVisitor(comp, value, pos);
		
		if (can_set)
			return new SetVariableVisitor(comp, value.substring(0, set_index), value.substring(set_index+1), pos);
		
		if (can_class)
			return new ClassAccessVariableVisitor(comp, value.substring(0, class_dot),
					value.substring(class_dot+1), pos);

		if (can_wrapped&&parenthesses==0)
			return parseVariable(value.substring(1, value.length() - 1), comp, pos);
		
		if (can_function&&parenthesses==0)
			return new FunctionCallVariableVisitor(value.substring(0, function_args_index), comp,
					HudderUtils.processParemeters(value.substring(function_args_index)), pos);
		
		if (can_string)
			return new StringVariableVisitor(comp, value.substring(1, value.length()-1), pos);
		
		if (can_dynamic) {
			// System variable
			if (comp.system_variables && DataVariableRegistry.hasVariable(value.toLowerCase()))
				return new SystemVariableVisitor(comp, value.toLowerCase(), pos);
			// Dynamic variable
			return new DynamicVariableVisitor(comp, value.toLowerCase(), pos);
		}
		
		// ! Operator
		if (value.charAt(0)=='!')
			return new NegateVariableVisitor(comp, value.substring(1), pos);
		
		if (can_temp)
			return new TemporaryVariableVisitor(comp, value, pos);
		
		
		// Fallback
		throw new CompileException("Untokenizable variable: " + value, pos);
	}
	
	static boolean isAlphaNumeric(char c) {
		return Character.isAlphabetic(c)||Character.isDigit(c);
	}
}
