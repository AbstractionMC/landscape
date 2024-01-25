package net.rotgruengelb.landscape.command.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SomeTextSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

	private final List<String> TEXT = new ArrayList<>();

	public SomeTextSuggestionProvider(Set<String> strings) {
		TEXT.addAll(strings);
	}

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {

		for (String text : TEXT) {
			builder.suggest(text);
		}

		return builder.buildFuture();
	}

	public SomeTextSuggestionProvider addText(String text) {
		TEXT.add(text);
		return this;
	}
}
