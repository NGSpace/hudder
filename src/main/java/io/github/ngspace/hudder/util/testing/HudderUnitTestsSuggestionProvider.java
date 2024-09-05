package io.github.ngspace.hudder.util.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class HudderUnitTestsSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
	
	public static List<String> suggestions;
	
	@Override public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context,
			SuggestionsBuilder builder) throws CommandSyntaxException {
		List<String> suggestionscopy = new ArrayList<String>(suggestions);
		for (int i = 0;i<suggestionscopy.size();i++) {
			String suggestion = suggestionscopy.get(i);
			if (suggestion.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) builder.suggest(suggestion);
		}
		return builder.buildFuture();
	}
}