package dev.ngspace.hudder.api.compilers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Compilers implements CompilerRegistry {
	
	private final Map<String, CompilerInstance> registeredcompilers = new HashMap<>();
    private final List<Consumer<CompilerEntry>> registrationListeners = new ArrayList<>();

	public Optional<CompilerEntry> findEntryFromName(String name) {
		CompilerInstance instance = registeredcompilers.get(name);
		
		if (instance == null) {
			return Optional.empty();
		}
		
		return Optional.of(toEntry(name, instance));
	}
	
	public Optional<CompilerEntry> findEntryFromDisplayName(String displayName) {
		return registeredcompilers.entrySet().stream()
				.filter(entry -> entry.getValue().displayname().equals(displayName)).findFirst()
				.map(entry -> toEntry(entry.getKey(), entry.getValue()));
	}
	
	public Optional<CompilerEntry> findEntryFromCompiler(AHudCompiler<?> compiler) {
		return registeredcompilers.entrySet().stream()
				.filter(entry -> entry.getValue().compiler().getClass().isInstance(compiler)).findFirst()
				.map(entry -> toEntry(entry.getKey(), entry.getValue()));
	}

	public String[] getSupportedCompilersForFilepath(String filepath) {
		return registeredcompilers.entrySet().stream()
				.filter(entry -> entry.getValue().compiler().isValidFilePath(filepath))
				.map(Entry::getKey)
				.toArray(String[]::new);
	}
	
	public CompilerEntry registerCompiler(String name, String displayname, boolean isUnstable, boolean deprecated,
			AHudCompiler<?> compiler) {
		CompilerEntry entry = toEntry(name, put(name, displayname, isUnstable, deprecated, compiler));
		for (Consumer<CompilerEntry> listener : registrationListeners)
			listener.accept(entry);
		return entry;
	}
	
	private CompilerInstance put(String name, String displayname, boolean isUnstable, boolean deprecated, AHudCompiler<?> compiler) {
		CompilerInstance instance = new CompilerInstance(displayname, isUnstable, deprecated, compiler);
		registeredcompilers.put(name, instance);
		return instance;
	}
	
	public boolean has(String name) {
		return registeredcompilers.get(name) != null;
	}
	
	public int size() {
		return registeredcompilers.size();
	}
	
	public boolean isEmpty() {
		return registeredcompilers.isEmpty();
	}
	
	public Set<String> names() {
		return Set.copyOf(registeredcompilers.keySet());
	}
	
	public Set<String> displaynames() {
		return registeredcompilers.values().stream().map(CompilerInstance::displayname)
				.collect(Collectors.toUnmodifiableSet());
	}
	
	public Set<AHudCompiler<?>> compilers() {
		return registeredcompilers.values().stream().map(CompilerInstance::compiler)
				.collect(Collectors.toUnmodifiableSet());
	}
	
	public Set<CompilerEntry> entries() {
		return registeredcompilers.entrySet().stream().map(entry -> toEntry(entry.getKey(), entry.getValue()))
				.collect(Collectors.toUnmodifiableSet());
	}
	
	public Set<CompilerEntry> stableEntries() {
		return entries().stream().filter(entry -> !entry.unstable()).collect(Collectors.toUnmodifiableSet());
	}
	
	public Set<CompilerEntry> unstableEntries() {
		return entries().stream().filter(CompilerEntry::unstable).collect(Collectors.toUnmodifiableSet());
	}
	
	private CompilerEntry toEntry(String name, CompilerInstance instance) {
		return new CompilerEntry(name, instance.displayname(), instance.unstable(), instance.deprecated(), instance.compiler());
	}
    
	public boolean addRegistrationListener(Consumer<CompilerEntry> e) {
		return registrationListeners.add(e);
	}

	public Consumer<CompilerEntry> removeRegistrationListener(int index) {
		return registrationListeners.remove(index);
	}
	
	private static record CompilerInstance(String displayname, boolean unstable, boolean deprecated, AHudCompiler<?> compiler) {}
	
}