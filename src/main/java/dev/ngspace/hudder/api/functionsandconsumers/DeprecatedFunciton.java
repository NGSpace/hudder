package dev.ngspace.hudder.api.functionsandconsumers;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class DeprecatedFunciton implements BindablePositionedFunction {
	
	private boolean firstRun = true;
	private String warning;
	private BindablePositionedFunction func;
	private String name;

	public DeprecatedFunciton(String warning, BindablePositionedFunction func, String[] names) {
		this.warning = warning;
		this.func = func;
		this.name = names[0];
	}

	@Override
	public Object invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos pos, ObjectWrapper... args) throws ExecutionException {
		if (firstRun) {
			firstRun = false;
			Hudder.showWarningToast(Component.literal(name+" function is Deprecated!").withStyle(ChatFormatting.BOLD),
					Component.literal("\u00A7a" + warning));
		}
		return func.invoke(man, comp, pos, args);
	}
	
}