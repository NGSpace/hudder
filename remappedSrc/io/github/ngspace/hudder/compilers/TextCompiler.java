package io.github.ngspace.hudder.compilers;

import java.util.Objects;

import io.github.ngspace.hudder.config.ConfigInfo;

public abstract class TextCompiler extends AVarTextCompiler {
	
	@Override public Object getVariable(String key) throws CompileException {
		if (getOperator(key)!=null) return conditionCheck(key);
		String[] values = key.split("=",2);
		if (values.length==1) return super.getVariable(key);
		Object newval = super.getVariable(values[1]);
		put(values[0].trim(), newval);
		return newval;
	}
	/**
	 * Solves an if...else...etc condition and returns it's value.
	 * @param ci - the ConfigInfo object
	 * @param CAR - The conditions and results
	 * @return the result of the conditions
	 * @throws CompileException
	 */
	public CompileResult solveCondition(ConfigInfo ci, String... CAR) throws CompileException {
		for (int i = 0;i<CAR.length;i++) {
			String cond = CAR[i];
			if (i%2!=0||CAR.length==i) continue;
			
			if (conditionCheck(cond)&&CAR.length>i+1) return compile(ci, CAR[i+1].substring(1,CAR[i+1].length()-1));
			if (i+1==CAR.length&&CAR.length%2!=0) return compile(ci, cond.substring(1,cond.length()-1));
		}
		return CompileResult.of("");
	}
	
	public boolean conditionCheck(String condition) throws CompileException {
		String operator = getOperator(condition);
		
		if (operator==null) return Boolean.valueOf(String.valueOf(getVariable(condition.toLowerCase())));
		String[] conditions = condition.split(operator);
		String cond1 = getVariable(conditions[0]).toString();
		String cond2 = getVariable(conditions[1]).toString();
		boolean areNumbers = true;
		double numcond1 = 0;
		double numcond2 = 0;
		try {numcond1 = Double.parseDouble(cond1);} catch (Exception e) {areNumbers = false;}
		if (areNumbers) try {numcond2 = Double.parseDouble(cond2);} catch (Exception e) {areNumbers = false;}
		return switch (operator) {
			case "==" -> areNumbers?numcond1==numcond2:Objects.equals(cond1, cond2);
			case "!=" -> areNumbers?numcond1==numcond2:Objects.equals(cond1, cond2);
			case ">=" -> numcond1>=numcond2;
			case "<=" -> numcond1<=numcond2;
			case ">"  -> numcond1> numcond2;
			case "<"  -> numcond1< numcond2;
			default -> throw new RuntimeException("Unknown condition: " + condition);//Someone is tampering fo sure
		};
	}
	
	/**
	 * Dumbest thing I ever wrote... but it works.
	 */
	public String getOperator(String condString) {
		if (condString.contains("==")) return "==";
		if (condString.contains(">=")) return ">=";
		if (condString.contains("<=")) return "<=";
		if (condString.contains(">")) return ">";
		if (condString.contains("<")) return "<";
		if (condString.contains("!=")) return "!=";
		return null;
	}
}
