package dev.ngspace.hudder.api.compilers;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.CompilerEntry;

/**
 * Registry for Hudder Compilers.
 * 
 * <p>
 * It stores the compiler itself, it's user-facing display name, stability status, deprecation status,
 * and a unique identifier.
 * </p>
 * 
 * <p>
 * It also allows listening for compiler registration.
 * </p>
 * 
 * @see AHudCompiler
 * @see CompilerEntry
 */
public interface CompilerRegistry {
	
	/**
	 * Finds the entry with the given unique identifier.
	 * 
	 * @param id the unique identifier
	 * @return an optional containing the entry associated with this unique identifier or an empty optional
	 *  if none is found.
	 */
	public Optional<CompilerEntry> findEntryFromId(String id);
	
	/**
	 * Finds an entry with the given display name.
	 * 
	 * @apiNote
	 * If multiple compilers were registed under the same display name, the one returned will depend on the
	 * implementation.
	 * 
	 * @param displayName the display name
	 * @return an optional containing an entry with this display name or an empty optional if none is found.
	 */
	public Optional<CompilerEntry> findEntryFromDisplayName(String displayName);
	
	/**
	 * Finds an entry with the given compiler.
	 * 
	 * @apiNote
	 * If the given compiler had been registered multiple times under different identifiers, the one returned
	 * will depend on implementation.
	 * 
	 * @param compiler the compiler
	 * @return an optional containing the entry associated with this compiler instance or an empty optional
	 *  if none is found.
	 */
	public Optional<CompilerEntry> findEntryFromCompiler(AHudCompiler<?> compiler);
	
	/**
	 * Returns all valid compiler entries for the given path
	 * 
	 * @param path the path to check
	 * @return an array of all compiler entries that are valid for this filepath or an empty array if none
	 *  are valid.
	 * 
	 * @see AHudCompiler#isValidFilePath(String)
	 */
	public CompilerEntry[] getValidCompilersForFilePath(Path path);
	
	/**
	 * Registers a compiler in this registry
	 * 
	 * @param id the unique identifier
	 * @param displayname the user-facing display name
	 * @param unstable whether the compiler is considered unstable
	 * @param deprecated whether the compiler is deprecated
	 * @param compiler the compiler instance itself
	 * @return the generated CompilerEntry
	 * @throws IllegalArgumentException if a compiler with the given unique identifier had already been registered
	 *  in this registry
	 */
	public CompilerEntry registerCompiler(String id, String displayname, boolean unstable, boolean deprecated,
			AHudCompiler<?> compiler) throws IllegalArgumentException;
	
	/**
	 * Returns all compiler entries that have been registered in this registry
	 * @return a set of all registered compiler entries
	 */
	public Set<CompilerEntry> entries();
	
	/**
	 * Returns all compilers that have been registered in this registry
	 * @return a set of all registered compilers
	 */
	public Set<AHudCompiler<?>> compilers();
	
	/**
	 * Adds a listener to listen for compiler registrations in this registry.
	 *
	 * @param listener the listener to add
	 * @return whether the listener was added
	 */
	public boolean addRegistrationListener(Consumer<CompilerEntry> listener);
	
	/**
	 * Removes a listener from listening to compiler registrations in this registry.
	 * 
	 * @param listener the listener to remove
	 * @return whether the listener was removed
	 */
	public boolean removeRegistrationListener(Consumer<CompilerEntry> listener);

	/**
	 * Shuts down all compilers registered in this registry
	 * 
	 * @see AHudCompiler#shutdown()
	 */
	public default void shutdownAll() {
		compilers().forEach(AHudCompiler::shutdown);
	}
	
}
