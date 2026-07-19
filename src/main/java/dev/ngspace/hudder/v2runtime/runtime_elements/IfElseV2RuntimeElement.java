package dev.ngspace.hudder.v2runtime.runtime_elements;

import java.util.ArrayList;
import java.util.List;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class IfElseV2RuntimeElement extends AV2RuntimeElement {

	private CompiledStatement[] compiled_statements;
	
	public IfElseV2RuntimeElement(HudderConfig info, Statement[] statements, V2Runtime runtime,
			String filename, AV2Compiler compiler) throws CompileException, ExecutionException {
		compiled_statements = new CompiledStatement[statements.length];
		List<V2Runtime> runtimes = new ArrayList<V2Runtime>();
		for (int i = 0;i<compiled_statements.length;i++) {
			Statement statement = statements[i];
			V2Runtime code = compiler.buildRuntime(info, statement.codeblock(),
					statement.pos(), filename, runtime);
			runtimes.add(code);
			
			AV2Value condition;
			if (statement.condition()==null||statement.condition().isBlank()) {
				if (i<compiled_statements.length-1)
					throw new CompileException("Detached else/else if statement!", statement.pos());
				condition = null;
			} else {
				condition = compiler.getV2Value(code, statement.condition(),
						statement.pos().line(), statement.pos().column());
			}
			compiled_statements[i] = new CompiledStatement(condition, code, statement.pos());
		}
		nestedRuntimes = runtimes.toArray(new V2Runtime[runtimes.size()]);
	}
	
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws ExecutionException {
		for (CompiledStatement statement : compiled_statements) {
			if (statement.condition==null||statement.condition.asBoolean()) {
				CompileState res = statement.code().execute();
				meta.combineWithResult(res.toResult(), false);
				if (res.hasReturned) meta.setReturnValue(res.returnValue);
				return !res.hasBroken;
			}
		}
		return true;
	}
	
	public static record Statement(String condition, String codeblock, TextPos pos) {}
	public static record CompiledStatement(AV2Value condition, V2Runtime code, TextPos pos) {}
}
