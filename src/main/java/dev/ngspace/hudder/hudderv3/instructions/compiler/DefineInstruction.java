package dev.ngspace.hudder.hudderv3.instructions.compiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;

public class DefineInstruction extends Instruction {
	
	public static int user_defines_count = 0;

	private String block;
	private String[] args;
	private HudderConfig info;
	private String name;
	private String finalname;
	private String filename;
	private AV3Compiler comp;

	public DefineInstruction(String block, String[] args, HudderConfig info, String name, String filename, AV3Compiler comp) {
		this.block = block;
		this.args = args;
		this.info = info;
		this.name = name;
		this.filename = filename;
		this.comp = comp;
		finalname = "user_" + name + "_" + ++user_defines_count;
	}

	@Override
	public void visit(V3ExecuteMethodWriter methodWriter, V3ClassWriter writer, Label breaklabel)
			throws CompileException {
		var method = writer.createExecuteMethod(finalname, new Class<?>[] {
			HudderConfig.class,
			String.class,
			String.class,
			Object[].class
		});
		
		for (int i = 0;i<args.length;i++) {
			method.defineVariable(args[i].toLowerCase().trim());
			method.defineVariable("arg" + (i+1));
			method.aload(4);
			method.loadConstantUnsafe(i);
			method.methodVisitor.visitInsn(Opcodes.AALOAD);
			method.dup();
			method.storeVariable(args[i].toLowerCase().trim());
			method.storeVariable("arg" + (i+1));
		}

		Label end = new Label();
		TokenizedCodeBlock tokenizedBlock = comp.compile(info, block, filename);
		tokenizedBlock.writeInstructions(method, writer, end);
		method.putLabel(end);
		method.end();
		
		if (tokenizedBlock.canReturnValue()!=tokenizedBlock.doesReturnValue()) {
			throw new CompileException("Function \""+name+"\" does not always return a value!",-1,-1);
		}
		
		if (!tokenizedBlock.canReturnValue())
			comp.user_methods.put(name, finalname);
		else
			comp.user_functions.put(name, finalname);
	}
}
