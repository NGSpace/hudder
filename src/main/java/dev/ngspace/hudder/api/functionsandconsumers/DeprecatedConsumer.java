package dev.ngspace.hudder.api.functionsandconsumers;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class DeprecatedConsumer implements BindablePositionedConsumer {
	
	private boolean firstRun = true;
	private String warning;
	private BindablePositionedConsumer cons;
	private String name;

	/**
	 * @deprecated use {@link #DeprecatedConsumer(String, BindablePositionedConsumer, String[])}
	 */
	@SuppressWarnings("removal")
	@Deprecated(since = "10.3.0", forRemoval = false)
	public DeprecatedConsumer(String warning, BindableConsumer cons, String[] names) {
		this(warning, (BindablePositionedConsumer) cons, names);
	}

	public DeprecatedConsumer(String warning, BindablePositionedConsumer cons, String[] names) {
		this.warning = warning;
		this.cons = cons;
		this.name = names[0];
	}

	@Override
	public void invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos pos, HudderConfig config,
			ObjectWrapper... args) throws ExecutionException {
		if (firstRun) {
			firstRun = false;
			Hudder.showWarningToast(Component.literal(name+" function is Deprecated!").withStyle(ChatFormatting.BOLD),
					Component.literal("\u00A7a" + warning));
		}
		cons.invoke(man, comp, pos, config, args);
	}
	
}
