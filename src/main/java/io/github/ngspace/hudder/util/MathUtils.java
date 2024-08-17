package io.github.ngspace.hudder.util;

import java.util.HashMap;

public class MathUtils {
	private MathUtils() {}
	static HashMap<String, Double> expressionCache = new HashMap<String,Double>();
	/**
	 * @deprecated - Do not reach a situation where you need to use this, very inefficent.<br>
	 * Try to evaluate an equation.
	 * @param str - the equation to evaluate.
	 * @return the double representation of the string.
	 * @throws NumberFormatException - if supplied with an invalid equation.
	 */
	@Deprecated(since = "3.5.0", forRemoval = true)
	public static double eval(String str) {
		Double has = expressionCache.get(str);
		if (has!=null) return has;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c!='0'&&c!='1'&&c!='2'&&c!='3'&&c!='4'&&c!='5'&&c!='6'&&c!='7'&&c!='8'&&c!='9'&&c!='.'
					&&c!='+'&&c!='-'&&c!='/'&&c!='*'&&c!='^'&&c!='%'&&c!='('&&c!=')')
				throw new NumberFormatException();
		}
		double res = new Object() {
	        int pos = -1;
	        int ch;
	        
	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }
	        
	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }
	        
	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new IllegalArgumentException("Unexpected char: " + (char)ch);
	            return x;
	        }
	        
	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm();
	                else if (eat('-')) x -= parseTerm();
	                else return x;
	            }
	        }
	        
	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor();
	                else if (eat('/')) x /= parseFactor();
	                else if (eat('%')) x %= parseFactor();
	                else return x;
	            }
	        }
	        
	        double parseFactor() {
	            if (eat('+')) return +parseFactor();
	            if (eat('-')) return -parseFactor();
	            
	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                if (!eat(')')) throw new IllegalArgumentException("Missing ')'");
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else throw new IllegalArgumentException("Unexpected char: " + (char)ch);
	            
	            if (eat('^')) x = Math.pow(x, parseFactor());
	            
	            return x;
	        }
	    }.parse();
	    expressionCache.put(str, res);
	    return res;
	}
}
