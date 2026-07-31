package dev.ngspace.hudder.hudderv3.instructions.compiler;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.StringVariableVisitor;

public class ConditionInstruction extends Instruction {
	
	private List<ConditionBranch> branches = new ArrayList<>();
	
	public ConditionInstruction(HudderConfig info, String filename, List<String> conds, AV3Compiler comp)
			throws CompileException {
		for (int i = 0; i + 1 < conds.size(); i += 2) {
			VariableVisitor condition = comp.parseVariable(conds.get(i));
			
			branches.add(prepareValue(condition, info, filename, conds.get(i + 1), comp));
		}
		if (conds.size() % 2 == 1) {
			branches.add(prepareValue(null, info, filename, conds.get(conds.size() - 1), comp));
		}
	}
	
	private static ConditionBranch prepareValue(VariableVisitor condition, HudderConfig info, String filename,
			String source, AV3Compiler comp) throws CompileException {
		
		VariableVisitor variable = comp.parseVariable(source);
		
		TokenizedCodeBlock compiledBlock = variable instanceof StringVariableVisitor string
				? comp.compile(info, string.value, filename)
				: null;
		
		return new ConditionBranch(condition, variable, compiledBlock);
	}
	
	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		
		Label conditionEnd = new Label();
		
		for (ConditionBranch branch : branches) {
			Label elseLabel = new Label();
			
			if (branch.condition() != null) {
				branch.condition().visit(methodWriter);
				methodWriter.booleanValue();
				
				methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, elseLabel);
			}
			
			if (branch.compiledBlock() != null) {
				branch.compiledBlock().writeInstructions(methodWriter, classWriter, breaklabel);
				
				methodWriter.loadConstant("");
			} else {
				branch.variable().visit(methodWriter);
			}
			
			methodWriter.jumpto(conditionEnd);
			methodWriter.putLabel(elseLabel);
		}
		
		methodWriter.loadConstant("");
		
		methodWriter.putLabel(conditionEnd);
		methodWriter.appendToBuilderAndPop();
	}
	
	private record ConditionBranch(VariableVisitor condition, VariableVisitor variable,
			TokenizedCodeBlock compiledBlock) {
	}
}