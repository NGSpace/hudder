package dev.ngspace.hudder.api.functionsandconsumers;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class DeprecatedConsumer implements BindableConsumer {
	
	private boolean firstRun = true;
	private String warning;
	private BindableConsumer cons;
	private String name;

	public DeprecatedConsumer(String warning, BindableConsumer cons, String[] names) {
		this.warning = warning;
		this.cons = cons;
		this.name = names[0];
	}

	@Override
	public void invoke(IUIElementManager man, AHudCompiler<?> comp, ObjectWrapper... args) throws ExecutionException {
		if (firstRun) {
			firstRun = false;
			Hudder.showWarningToast(Component.literal(name+" function is Deprecated!").withStyle(ChatFormatting.BOLD),
					Component.literal("\u00A7a" + warning));
		}
		cons.invoke(man, comp, args);
	}
	
}
