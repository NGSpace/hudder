package dev.ngspace.hudder.hudderv3;

import java.util.Objects;

import dev.ngspace.hudder.exceptions.ExecutionException;

public class HudderV3Helper {
	private HudderV3Helper() {}
	
	public static boolean compare(Object val1, Object val2, String comparisonOperator) throws ExecutionException {
		if (val1==null||val2==null) {
			if (comparisonOperator.equals("=="))
				return val1==val2;
			else if (comparisonOperator.equals("!="))
				return val1!=val2;
			else throw new ExecutionException("Can not compare null values using the "+comparisonOperator+" operator.",
					-1, -1);
		}
		boolean areNums = false;
		double dou1 = 0;
		double dou2 = 0;
//		if (val1 instanceof Number num) {
//			dou1 = num.doubleValue();
//			boolean otherhasval = other.hasValue();
//			if (!otherhasval) dou2 = other.asDouble();
//			if (val2 instanceof Number||!otherhasval) areNums = true;
//		}
		if (val1 instanceof Number num1) {
			dou1 = num1.doubleValue();
			if (val2 instanceof Number num2) {
				dou2 = num2.doubleValue();
				areNums = true;
			}
		}
		return switch (comparisonOperator) {
			case "==" -> areNums ? dou1==dou2 :  Objects.equals(val1, val2);
			case "!=" -> areNums ? dou1!=dou2 : !Objects.equals(val1, val2);
			case ">=" -> dou1>=dou2;
			case "<=" -> dou1<=dou2;
			case ">"  -> dou1> dou2;
			case "<"  -> dou1< dou2;
			default -> throw new IllegalArgumentException("Unknown comparasion operator: " + comparisonOperator);
		};
	}
}
