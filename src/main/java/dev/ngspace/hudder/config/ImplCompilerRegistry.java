package dev.ngspace.hudder.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.CompilerEntry;

public class ImplCompilerRegistry implements CompilerRegistry {

	public List<Consumer<CompilerEntry>> listeners = new ArrayList<>();
	public Map<CompilerEntry, AHudCompiler<?>> registredCompilers = new HashMap<>();

	@Override
	public Optional<CompilerEntry> findEntryFromId(String id) {
		return entries().stream().filter(e->e.id().equals(id)).findAny();
	}

	@Override
	public Optional<CompilerEntry> findEntryFromDisplayName(String displayName) {
		return entries().stream().filter(e->e.display_name().equals(displayName)).findAny();
	}

	@Override
	public Optional<CompilerEntry> findEntryFromCompiler(AHudCompiler<?> compiler) {
		return entries().stream().filter(e->e.compiler()==compiler).findAny();
	}

	@Override
	public CompilerEntry[] getValidCompilersForFilePath(String filepath) {
		return entries().stream().filter(e->e.compiler().isValidFilePath(filepath)).toArray(CompilerEntry[]::new);
	}

	@Override
	public CompilerEntry registerCompiler(String id, String display_name, boolean unstable, boolean deprecated,
			AHudCompiler<?> compiler) {
		if (entries().stream().map(CompilerEntry::id).anyMatch(s->s.equals(id)))
			throw new IllegalArgumentException("A compiler with the identifier \"" + id + "\" has already been registered");
		CompilerEntry entry = new CompilerEntry(id, display_name, unstable, deprecated, compiler);
		registredCompilers.put(entry, compiler);
		for (var listener : listeners)
			listener.accept(entry);
		return entry;
	}

	@Override
	public Set<CompilerEntry> entries() {
		return registredCompilers.keySet();
	}

	@Override
	public Set<AHudCompiler<?>> compilers() {
		return Set.copyOf(registredCompilers.values());
	}

	@Override
	public boolean addRegistrationListener(Consumer<CompilerEntry> e) {
		return listeners.add(e);
	}

	@Override
	public boolean removeRegistrationListener(Consumer<CompilerEntry> listener) {
		return listeners.remove(listener);
	}
	
}
