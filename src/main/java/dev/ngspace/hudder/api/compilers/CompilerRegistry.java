package dev.ngspace.hudder.api.compilers;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.CompilerEntry;

public interface CompilerRegistry {
	
	public Optional<CompilerEntry> findEntryFromName(String name);
	
	public Optional<CompilerEntry> findEntryFromDisplayName(String displayName);
	
	public Optional<CompilerEntry> findEntryFromCompiler(AHudCompiler<?> compiler);
	
	public String[] getSupportedCompilersForFilepath(String filepath);

	public CompilerEntry registerCompiler(String name, String displayname, boolean isUnstable, boolean deprecated,
			AHudCompiler<?> compiler);
	
	public Set<CompilerEntry> entries();
	
	public Set<AHudCompiler<?>> compilers();
	
	public boolean addRegistrationListener(Consumer<CompilerEntry> e);
	
	public Consumer<CompilerEntry> removeRegistrationListener(int index);

	public default void shutdownAll() {
		compilers().forEach(AHudCompiler::shutdown);
	}
	
}
