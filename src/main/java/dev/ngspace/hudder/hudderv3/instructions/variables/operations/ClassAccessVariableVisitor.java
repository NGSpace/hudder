package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.asm.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;
import dev.ngspace.hudder.utils.HudderUtils;
import dev.ngspace.hudder.utils.ValueGetter;
import dev.ngspace.hudder.v2runtime.values.operations.V2ClassPropertyCall;

public class ClassAccessVariableVisitor extends ExpressionVisitor {
	
	private ExpressionVisitor classobj;
	private boolean isFunctionCall;
	private ExpressionVisitor[] functionCallArgs;
	private String funcName = "";
	private String fieldName = "";
	private final String classObjectName;

	public ClassAccessVariableVisitor(AV3Compiler comp, String classyobjname, String prop, TextPos pos)
			throws CompileException {
		super(comp, pos);
		this.classObjectName = classyobjname;
		this.classobj = comp.parseVariable(classyobjname, pos);
		if (!prop.startsWith("(")&&prop.endsWith(")")) {
			int argStart = prop.indexOf("(");
			if (argStart!=-1) {
				this.funcName = prop.substring(0, argStart);
				if (funcName.matches("^[a-zA-Z0-9_-]*$")) {
					
					String parametersString = prop.substring(argStart+1, prop.length()-1);
					
					String[] tokenizedArgs = HudderUtils.processParemeters(parametersString);
					functionCallArgs = new ExpressionVisitor[tokenizedArgs.length];
					
					for (int i=0;i<functionCallArgs.length;i++) 
						functionCallArgs[i] = comp.parseVariable(tokenizedArgs[i], pos);
					
					this.isFunctionCall = true;
				}
			}
		}
		if (!isFunctionCall) fieldName = prop;
		for (String forbidden : V2ClassPropertyCall.forbiddenValuesAndFunctions) {
			if (forbidden.equals(funcName)) throw new CompileException("No function named \""+funcName+'"',pos);
			if (forbidden.equals(fieldName)) throw new CompileException("No property named \""+fieldName+'"',pos);
		}
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws CompileException {
		classobj.visit(methodWriter);

		if (isFunctionCall) {
			int classObjectIndex = methodWriter.astore();
			methodWriter.loadConstantUnsafe(functionCallArgs.length);
			methodWriter.newArray(Object.class);
			int parametersIndex = methodWriter.astore();
			for (int i = 0; i < functionCallArgs.length; i++) {
				methodWriter.aload(parametersIndex);
				methodWriter.loadConstantUnsafe(i);
				functionCallArgs[i].visit(methodWriter);
				methodWriter.aastore();
			}
			methodWriter.aload(classObjectIndex);
			methodWriter.loadConstant(classObjectName);
			methodWriter.loadConstant(funcName);
			methodWriter.aload(parametersIndex);
			methodWriter.loadConstantUnsafe(pos.line());
			methodWriter.loadConstantUnsafe(pos.column());
			methodWriter.callStatic(HudderV3Helper.class, "callClassMethod", "("
					+ "Ljava/lang/Object;"
					+ "Ljava/lang/String;"
					+ "Ljava/lang/String;"
					+ "[Ljava/lang/Object;"
					+ "II"
					+ ")Ljava/lang/Object;",
					false);
			return;
		}

		Label value_getter = new Label();
		Label end = new Label();
		
		methodWriter.dup();
		methodWriter.instanceOf(ValueGetter.class);
		methodWriter.ifne(value_getter);
		
		methodWriter.loadConstant(classObjectName);
		methodWriter.loadConstant(fieldName);
		methodWriter.loadConstantUnsafe(pos.line());
		methodWriter.loadConstantUnsafe(pos.column());
		methodWriter.callStatic(HudderV3Helper.class, "getClassProperty", "("
				+ "Ljava/lang/Object;"
				+ "Ljava/lang/String;"
				+ "Ljava/lang/String;"
				+ "II)"
				+ "Ljava/lang/Object;", false);
		methodWriter.jumpto(end);
		
		methodWriter.putLabel(value_getter);
		methodWriter.checkcast(ValueGetter.class);
		methodWriter.loadConstant(fieldName);
		methodWriter.callInterface(ValueGetter.class, "get", "(Ljava/lang/String;)Ljava/lang/Object;");
		
		methodWriter.putLabel(end);
	}
	
}
