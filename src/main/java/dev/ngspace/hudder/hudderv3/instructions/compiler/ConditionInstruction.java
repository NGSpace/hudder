package dev.ngspace.hudder.hudderv3.instructions.compiler;

import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.StringVariableVisitor;

public class ConditionInstruction extends Instruction {
	
	final List<String> conds;
	final AV3Compiler comp;
	final String filename;
	final HudderConfig info;
	
	public ConditionInstruction(HudderConfig info, String filename, List<String> conds, AV3Compiler comp) {
		this.conds = conds;
		this.comp = comp;
		this.filename = filename;
		this.info = info;
	}
	

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter, Label breaklabel)
			throws CompileException {
		Label conditionend = new Label();
		
		for (int i = 0;i<conds.size()-1;i++) {
			Label elseLabel = new Label();
			if (i!=conds.size()) {
				comp.parseVariable(conds.get(i)).visit(methodWriter);
				methodWriter.booleanValue();
				methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, elseLabel);

				VariableVisitor variable = comp.parseVariable(conds.get(i+1));
				if (variable instanceof StringVariableVisitor st) {
					comp.compile(info, st.value, filename).writeInstructions(methodWriter, classWriter, breaklabel);
					methodWriter.loadConstant("");
				} else
					variable.visit(methodWriter);
			}
			
			methodWriter.jumpto(conditionend);
			methodWriter.putLabel(elseLabel);
			i++;
		}
		
		if (conds.size()%2==1)
			comp.parseVariable(conds.get(conds.size()-1)).visit(methodWriter);
		else
			methodWriter.loadConstant("");
		
		methodWriter.putLabel(conditionend);
		
		methodWriter.appendToBuilderAndPop();
	}
	
}
