package dev.ngspace.hudder.hudderv3.instructions;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;

public class IfElseInstuction extends Instruction {

	private CompiledStatement[] compiled_statements;
	private String filename;
	private AV3Compiler comp;
	private HudderConfig info;
	
	public IfElseInstuction(Statement[] statements, String filename, AV3Compiler compiler, HudderConfig info)
			throws ExecutionException {
		compiled_statements = new CompiledStatement[statements.length];
		this.filename = filename;
		this.comp = compiler;
		this.info = info;
		for (int i = 0;i<compiled_statements.length;i++) {
			Statement statement = statements[i];
			
			VariableVisitor condition;
			if (statement.condition()==null||statement.condition().isBlank()) {
				if (i<compiled_statements.length-1)
					throw new ExecutionException("Detached else/else if statement!", -1, -1);
				condition = null;
			} else {
				condition = compiler.parseVariable(statement.condition());
			}
			compiled_statements[i] = new CompiledStatement(condition, statement.codeblock());
		}
	}
	
	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter,
			Label breaklabel) throws ExecutionException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		
		Label end = new Label();
		
		for (CompiledStatement statement : compiled_statements) {
			Label nextcondition = new Label();
			
			if (statement.condition()!=null) {
				statement.condition().visit(methodWriter);
				methodWriter.booleanValue();
				methodWriter.methodVisitor.visitJumpInsn(Opcodes.IFEQ, nextcondition);
			}
			
			comp.compile(methodWriter, classWriter, info, statement.code(), filename, breaklabel);
			methodWriter.jumpto(end);
			
			methodWriter.putLabel(nextcondition);
		}
		
		methodWriter.putLabel(end);
		
		methodWriter.setBuilderDisabled(builderdisabled);
	}
	
	public static record Statement(String condition, String codeblock) {}
	public static record CompiledStatement(VariableVisitor condition, String code) {}
}
