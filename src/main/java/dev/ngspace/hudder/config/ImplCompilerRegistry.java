package dev.ngspace.hudder.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.CompilerEntry;

public class ImplCompilerRegistry implements CompilerRegistry {

	private List<Consumer<CompilerEntry>> listeners = new ArrayList<>();
	private List<CompilerEntry> registredCompilers = new ArrayList<>();

	@Override
	public Optional<CompilerEntry> findEntryFromId(String id) {
		return registredCompilers.stream().filter(e->e.id().equals(id)).findAny();
	}

	@Override
	public Optional<CompilerEntry> findEntryFromDisplayName(String displayName) {
		return registredCompilers.stream().filter(e->e.display_name().equals(displayName)).findAny();
	}

	@Override
	public Optional<CompilerEntry> findEntryFromCompiler(AHudCompiler<?> compiler) {
		return registredCompilers.stream().filter(e->e.compiler()==compiler).findAny();
	}

	@Override
	public CompilerEntry[] getValidCompilersForFilePath(Path path) {
		return registredCompilers.stream().filter(e->e.compiler().isValidFilePath(path)).toArray(CompilerEntry[]::new);
	}

	@Override
	public CompilerEntry registerCompiler(String id, String display_name, boolean unstable, boolean deprecated,
			AHudCompiler<?> compiler) {
		if (registredCompilers.stream().map(CompilerEntry::id).anyMatch(s->s.equals(id)))
			throw new IllegalArgumentException("A compiler with the identifier \"" + id + "\" has already been registered");
		CompilerEntry entry = new CompilerEntry(id, display_name, unstable, deprecated, compiler);
		registredCompilers.add(entry);
		for (var listener : listeners)
			listener.accept(entry);
		return entry;
	}

	@Override
	public Set<CompilerEntry> entries() {
		return Set.copyOf(registredCompilers);
	}

	@Override
	public Set<AHudCompiler<?>> compilers() {
		return entries().stream().map(e->e.compiler()).collect(Collectors.toSet());
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
