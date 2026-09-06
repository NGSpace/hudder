package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter.UserMethod;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;

public class DefineInstruction extends Instruction {
	
	public static int user_defines_count = 0;

	private String[] args;
	private String name;
	private String bytecodename;
	private TokenizedCodeBlock tokenizedBlock;

	public DefineInstruction(String block, String[] args, String name, String filename,
			AV3Compiler comp, TextPos pos) throws CompileException {
		super(pos);
		this.args = args;
		this.name = name;
		tokenizedBlock = comp.compile(block, filename, new TextPos(pos.line()+1, 0));
		
		bytecodename = "user_" + (tokenizedBlock.canReturnValue() ? "function" : "method") + "_"
				+ (++user_defines_count);
		
		if (tokenizedBlock.canReturnValue()!=tokenizedBlock.doesReturnValue())
			throw new CompileException("Function \""+name+"\" does not always return a value!", pos);
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter writer, Label breaklabel)
			throws CompileException {
		var method = writer.createExecuteMethod(bytecodename, new Class<?>[] {
			HudderConfig.class,
			String.class,
			Object[].class
		});
		
		for (int i = 0;i<args.length;i++) {
			method.defineVariable(args[i].toLowerCase().trim());
			method.defineVariable("arg" + (i+1));
			method.aload(3);
			method.loadConstantUnsafe(i);
			method.aaload();
			method.dup();
			method.storeVariable(args[i].toLowerCase().trim());
			method.storeVariable("arg" + (i+1));
		}

		Label end = new Label();
		tokenizedBlock.writeInstructions(method, writer, end);
		method.putLabel(end);
		method.end();
		
		if (!tokenizedBlock.canReturnValue())
			writer.user_methods.put(name, new UserMethod(bytecodename, args.length, args.length));
		else
			writer.user_functions.put(name, new UserMethod(bytecodename, args.length, args.length));
	}
}
