package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public class IfElseInstuction extends Instruction {

	private CompiledStatement[] compiled_statements;
	private AV3Compiler comp;
	
	public IfElseInstuction(Statement[] statements, String filename, AV3Compiler compiler, TextPos pos)
			throws CompileException {
		super(pos);
		compiled_statements = new CompiledStatement[statements.length];
		this.comp = compiler;
		for (int i = 0;i<compiled_statements.length;i++) {
			Statement statement = statements[i];
			
			ExpressionVisitor condition;
			if (statement.condition()==null||statement.condition().isBlank()) {
				if (i<compiled_statements.length-1)
					throw new CompileException("Detached else/else if statement!", pos);
				condition = null;
			} else {
				condition = compiler.parseVariable(statement.condition(), pos);
			}
			compiled_statements[i] = new CompiledStatement(condition,
					comp.compile(statement.codeblock(), filename, pos));
		}
	}
	
	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter classWriter,
			Label breaklabel) throws CompileException {
		boolean builderdisabled = methodWriter.isBuilderDisabled();
		methodWriter.setBuilderDisabled(true);
		
		Label end = new Label();
		
		for (CompiledStatement statement : compiled_statements) {
			Label nextcondition = new Label();
			
			if (statement.condition()!=null) {
				statement.condition().visit(methodWriter);
				methodWriter.checkcastSafe(Boolean.class, pos);
				methodWriter.ensureNotNull("Condition can not be null!", pos);
				methodWriter.booleanValue();
				methodWriter.ifeq(nextcondition);
			}
			
			statement.code().writeInstructions(methodWriter, classWriter, breaklabel);
			methodWriter.jumpto(end);
			
			methodWriter.putLabel(nextcondition);
		}
		
		methodWriter.putLabel(end);
		
		methodWriter.setBuilderDisabled(builderdisabled);
	}
	
	@Override
	public boolean canReturnValue() {
		for (int i = 0;i<compiled_statements.length;i++) {
			CompiledStatement statement = compiled_statements[i];
			if (statement.code().canReturnValue())
				return true;
		}
		return false;
	}
	
	@Override
	public boolean doesReturnValue() {
		for (int i = 0;i<compiled_statements.length;i++) {
			CompiledStatement statement = compiled_statements[i];
			if (!statement.code().doesReturnValue())
				return false;
		}
		// If there is no else then in case the condition falls through it will not return a value
		return compiled_statements[compiled_statements.length-1].condition()==null;
	}
	
	public static record Statement(String condition, String codeblock) {}
	public static record CompiledStatement(ExpressionVisitor condition, TokenizedCodeBlock code) {}
}
