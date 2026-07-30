package dev.ngspace.hudder.hudderv3.instructions.variables.operations;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3MethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.utils.HudderUtils;

public class ClassAccessVariableVisitor extends VariableVisitor {
	
	private static final String[] forbiddenValuesAndFunctions = {"getClass","hashCode","wait","notify","notifyAll","clone","finalize"};
	private VariableVisitor classobj;
	private boolean isFunctionCall;
	private VariableVisitor[] functionCallArgs;
	private String funcName = "";
	private String fieldName = "";

	protected ClassAccessVariableVisitor(AV3Compiler comp, String classyobjname, String prop) throws ExecutionException {
		super(comp);
		this.classobj = comp.parseVariable(classyobjname);
		if (!prop.startsWith("(")&&prop.endsWith(")")) {
			int argStart = prop.indexOf("(");
			if (argStart!=-1) {
				this.funcName = prop.substring(0, argStart);
				if (funcName.matches("^[a-zA-Z0-9_-]*$")) {
					
					String parametersString = prop.substring(argStart+1, prop.length()-1);
					
					String[] tokenizedArgs = HudderUtils.processParemeters(parametersString);
					functionCallArgs = new VariableVisitor[tokenizedArgs.length];
					
					for (int i=0;i<functionCallArgs.length;i++) 
						functionCallArgs[i] = comp.parseVariable(tokenizedArgs[i]);
					
					this.isFunctionCall = true;
				}
			}
		}
		if (!isFunctionCall) fieldName = prop;
		for (String forbidden : forbiddenValuesAndFunctions) {
			if (forbidden.equals(funcName)) throw new ExecutionException("No function named \""+funcName+'"',-1,-1);
			if (forbidden.equals(fieldName)) throw new ExecutionException("No property named \""+fieldName+'"',-1,-1);
		}
	}

	@Override
	public void visit(V3MethodWriter methodWriter) throws ExecutionException {
		// TODO Auto-generated method stub
		
	}
	
}
