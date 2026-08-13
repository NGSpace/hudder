package dev.ngspace.hudder.compilers.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.compilers.HudderV2Compiler;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.JavaScriptCompiler;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;

/**
 * Provides a central registry for HUD compilers.
 *
 * <p>
 * Each registered compiler has:
 * </p>
 * <ul>
 * <li>An internal name used for configuration and lookup.</li>
 * <li>A display name intended for user interfaces.</li>
 * <li>An unstable flag indicating whether the compiler is experimental.</li>
 * <li>An {@link AHudCompiler} instance.</li>
 * </ul>
 *
 * <p>
 * The collections returned by this class are immutable snapshots. Changes made
 * to the registry after obtaining one of these collections will not be
 * reflected in the previously returned collection.
 * </p>
 *
 * <p>
 * This registry is not designed for concurrent modification. Compiler
 * registration should normally be performed during application startup.
 * </p>
 */
public class Compilers {

	/**
	 * Prevents this utility class from being instantiated.
	 */
	private Compilers() {
	}
	
	private static final Map<String, CompilerInstance> registeredcompilers = new HashMap<String, CompilerInstance>();
	
	/**
	 * Shared Hudder V2 compiler instance.
	 *
	 * <p>
	 * A single instance is retained to prevent unnecessary creation of multiple
	 * {@link HudderV2Compiler} objects.
	 * </p>
	 */
	public static final HudderV2Compiler hudderV2Compiler = new HudderV2Compiler();
	public static final HudderV3Compiler hudderV3Compiler = new HudderV3Compiler();
	
	/**
	 * Registers the compilers included with Hudder.
	 *
	 * <p>
	 * If a compiler with one of the default names is already registered, that
	 * registration is replaced. Unlike
	 * {@link #registerCompiler(String, String, boolean, AHudCompiler)}, this method
	 * does not directly request a configuration refresh.
	 * </p>
	 */
	public static void registerDefaultCompilers() {
		put("hudder", "Hudder", false, hudderV2Compiler);
		put("js", "JavaScript", false, new JavaScriptCompiler());
		put("pack", "Hudpack", false, new HudPackCompiler());
		put("hudderv3", "Hudder V3", false, hudderV3Compiler);
	}
	
	/**
	 * Returns the compiler registered under the supplied internal name.
	 *
	 * <p>
	 * This lookup is case-sensitive.
	 * </p>
	 *
	 * @param name the registered internal compiler name
	 * @return the compiler registered under {@code name}
	 * @throws IllegalArgumentException if no compiler is registered under the
	 *                                  supplied name
	 */
	public static AHudCompiler<?> getCompilerFromName(String name) throws IllegalArgumentException {
		
		CompilerInstance instance = registeredcompilers.get(name);
		
		if (instance != null) {
			return instance.compiler();
		}
		
		throw getNoCompilerException(name, "name");
	}
	
	/**
	 * Returns the internal name associated with a compiler display name.
	 *
	 * <p>
	 * The display-name comparison is case-sensitive.
	 * </p>
	 *
	 * @param displayname the display name to search for
	 * @return the internal name associated with {@code displayname}
	 * @throws IllegalArgumentException if no compiler has the supplied display name
	 */
	public static String getCompilerNameFromDisplayname(String displayname) {
		return findEntryFromDisplayName(displayname)
				.orElseThrow(() -> getNoCompilerException(displayname, "display name")).name();
	}
	
	/**
	 * Returns the compiler associated with a display name.
	 *
	 * <p>
	 * The display-name comparison is case-sensitive.
	 * </p>
	 *
	 * @param displayname the display name to search for
	 * @return the compiler associated with {@code displayname}
	 * @throws IllegalArgumentException if no compiler has the supplied display name
	 */
	public static AHudCompiler<?> getCompilerFromDisplayname(String displayname) {
		
		return findEntryFromDisplayName(displayname)
				.orElseThrow(() -> getNoCompilerException(displayname, "display name")).compiler();
	}
	
	/**
	 * Returns the display name associated with an internal compiler name.
	 *
	 * <p>
	 * For compatibility with the existing implementation, the supplied name is
	 * converted to lowercase before lookup. Compiler registrations themselves are
	 * not automatically converted to lowercase, so registering mixed-case internal
	 * names is discouraged.
	 * </p>
	 *
	 * @param name the internal compiler name
	 * @return the compiler's display name
	 * @throws IllegalArgumentException if no compiler is registered under the
	 *                                  normalized name
	 */
	public static String getDisplayNameFromCompilerName(String name) {
		return findEntryFromName(name.toLowerCase()).orElseThrow(() -> getNoCompilerException(name, "name"))
				.displayname();
	}
	
	/**
	 * Returns the complete entry associated with a display name.
	 *
	 * <p>
	 * The display-name comparison is case-sensitive.
	 * </p>
	 *
	 * @param name the display name to search for
	 * @return the matching compiler entry
	 * @throws IllegalArgumentException if no matching compiler is registered
	 */
	public static CompilerEntry getEntryFromDisplayName(String name) {
		return findEntryFromDisplayName(name).orElseThrow(() -> getNoCompilerException(name, "displayname"));
	}
	
	/**
	 * Returns the complete entry associated with an internal compiler name.
	 *
	 * <p>
	 * This lookup is case-sensitive.
	 * </p>
	 *
	 * @param name the internal compiler name
	 * @return the matching compiler entry
	 * @throws IllegalArgumentException if no matching compiler is registered
	 */
	public static CompilerEntry getEntryFromName(String name) {
		return findEntryFromName(name).orElseThrow(() -> getNoCompilerException(name, "name"));
	}

	public static String getNameFromCompiler(AVarTextCompiler compiler) {
		return findNameFromCompiler(compiler).orElseThrow(() -> getNoCompilerException(compiler.getClass().getCanonicalName(), "type"));
	}
	
	/**
	 * Attempts to find a compiler entry by its internal name.
	 *
	 * <p>
	 * This is the non-throwing equivalent of {@link #getEntryFromName(String)}.
	 * </p>
	 *
	 * @param name the internal compiler name
	 * @return an optional containing the matching entry, or an empty optional when
	 *         no matching compiler is registered
	 */
	public static Optional<CompilerEntry> findEntryFromName(String name) {
		CompilerInstance instance = registeredcompilers.get(name);
		
		if (instance == null) {
			return Optional.empty();
		}
		
		return Optional.of(toEntry(name, instance));
	}
	
	/**
	 * Attempts to find a compiler entry by its display name.
	 *
	 * <p>
	 * The display-name comparison is case-sensitive.
	 * </p>
	 *
	 * @param displayName the compiler display name
	 * @return an optional containing the matching entry, or an empty optional when
	 *         no matching compiler is registered
	 */
	public static Optional<CompilerEntry> findEntryFromDisplayName(String displayName) {
		
		return registeredcompilers.entrySet().stream()
				.filter(entry -> entry.getValue().displayname().equals(displayName)).findFirst()
				.map(entry -> toEntry(entry.getKey(), entry.getValue()));
	}
	
	/**
	 * Attempts to find a compiler by its internal name.
	 *
	 * @param name the internal compiler name
	 * @return an optional containing the compiler, or an empty optional if no
	 *         matching compiler is registered
	 */
	public static Optional<AHudCompiler<?>> findCompilerFromName(String name) {
		return findEntryFromName(name).map(CompilerEntry::compiler);
	}
	
	/**
	 * Attempts to find a compiler by its display name.
	 *
	 * @param displayName the compiler display name
	 * @return an optional containing the compiler, or an empty optional if no
	 *         matching compiler is registered
	 */
	public static Optional<AHudCompiler<?>> findCompilerFromDisplayName(String displayName) {
		
		return findEntryFromDisplayName(displayName).map(CompilerEntry::compiler);
	}
	
	public static Optional<CompilerEntry> findEntryFromCompiler(AVarTextCompiler compiler) {
		return registeredcompilers.entrySet().stream()
				.filter(entry -> entry.getValue().compiler().getClass().isInstance(compiler)).findFirst()
				.map(entry -> toEntry(entry.getKey(), entry.getValue()));
	}

	public static Optional<String> findNameFromCompiler(AVarTextCompiler compiler) {
		return findEntryFromCompiler(compiler).map(CompilerEntry::name);
	}

	public static String[] getSupportedCompilersForFilepath(String filepath) {
		return registeredcompilers.entrySet().stream()
				.filter(entry -> entry.getValue().compiler().isValidFilePath(filepath))
				.map(Entry::getKey)
				.toArray(String[]::new);
	}
	
	/**
	 * Creates the exception used when a compiler lookup fails.
	 *
	 * @param value     the value used in the failed lookup
	 * @param inputType a description of the supplied value
	 * @return an exception describing the failed lookup
	 */
	private static IllegalArgumentException getNoCompilerException(String value, String inputType) {
		
		return new IllegalArgumentException("Compiler with the " + inputType + " \"" + value
				+ "\" either does not exist or has not yet been loaded.");
	}
	
	/**
	 * Registers a compiler by loading and instantiating its class through
	 * reflection.
	 *
	 * <p>
	 * The class must implement or extend {@link AHudCompiler} and expose an
	 * accessible no-argument constructor.
	 * </p>
	 *
	 * @param name      the internal compiler name
	 * @param classname the fully qualified compiler class name
	 * @throws IllegalArgumentException if the class cannot be loaded or
	 *                                  instantiated
	 * @deprecated Use
	 *             {@link #registerCompiler(String, String, boolean, AHudCompiler)}
	 *             instead.
	 */
	@Deprecated(since = "9.0.0", forRemoval = true)
	public static void registerCompiler(String name, String classname) {
		try {
			registerCompiler(name, name, true,
					(AHudCompiler<?>) Class.forName(classname).getConstructor().newInstance());
		} catch (ReflectiveOperationException exception) {
			exception.printStackTrace();
			throw new IllegalArgumentException("Failed to load compiler", exception);
		}
	}
	
	/**
	 * Registers a stable compiler using its internal name as its display name.
	 *
	 * @param name     the internal and display name of the compiler
	 * @param compiler the compiler instance to register
	 * @deprecated Use
	 *             {@link #registerCompiler(String, String, boolean, AHudCompiler)}
	 *             to specify a separate display name and stability status.
	 */
	@Deprecated(since = "10.2.0", forRemoval = false)
	public static void registerCompiler(String name, AHudCompiler<?> compiler) {
		
		registerCompiler(name, name, false, compiler);
	}
	
	/**
	 * Registers a compiler.
	 *
	 * <p>
	 * If another compiler is already registered under {@code name}, it is replaced.
	 * When Hudder's configuration has been initialized, registering the compiler
	 * also requests a configuration update.
	 * </p>
	 *
	 * @param name        the internal compiler name
	 * @param displayname the user-facing compiler name
	 * @param isUnstable  whether the compiler should be treated as experimental
	 * @param compiler    the compiler instance to register
	 */
	public static void registerCompiler(String name, String displayname, boolean isUnstable, AHudCompiler<?> compiler) {
		
		put(name, displayname, isUnstable, compiler);
		
		if (Hudder.config != null) {
			Hudder.config.readAndUpdateConfig();
		}
	}
	
	/**
	 * Adds or replaces an entry without refreshing Hudder's configuration.
	 *
	 * @param name        the internal compiler name
	 * @param displayname the user-facing compiler name
	 * @param isUnstable  whether the compiler is experimental
	 * @param compiler    the compiler instance
	 */
	private static void put(String name, String displayname, boolean isUnstable, AHudCompiler<?> compiler) {
		
		registeredcompilers.put(name, new CompilerInstance(displayname, isUnstable, compiler));
	}
	
	/**
	 * Determines whether a compiler is registered under an internal name.
	 *
	 * <p>
	 * This lookup is case-sensitive.
	 * </p>
	 *
	 * @param name the internal compiler name
	 * @return {@code true} when a matching compiler is registered
	 */
	public static boolean has(String name) {
		return registeredcompilers.get(name) != null;
	}
	
	/**
	 * Determines whether a compiler has the supplied display name.
	 *
	 * <p>
	 * The display-name comparison is case-sensitive.
	 * </p>
	 *
	 * @param name the display name to search for
	 * @return {@code true} when a matching compiler is registered
	 */
	public static boolean hasCompilerFromDisplayName(String name) {
		return registeredcompilers.values().stream().anyMatch(instance -> instance.displayname().equals(name));
	}
	
	/**
	 * Determines whether a registered compiler is marked as unstable.
	 *
	 * @param name the internal compiler name
	 * @return {@code true} when the compiler is marked as unstable
	 * @throws IllegalArgumentException if no compiler is registered under the
	 *                                  supplied name
	 */
	public static boolean isUnstable(String name) {
		return getEntryFromName(name).unstable();
	}
	
	/**
	 * Returns the number of registered compilers.
	 *
	 * @return the current registry size
	 */
	public static int size() {
		return registeredcompilers.size();
	}
	
	/**
	 * Determines whether the compiler registry is empty.
	 *
	 * @return {@code true} when no compilers are registered
	 */
	public static boolean isEmpty() {
		return registeredcompilers.isEmpty();
	}
	
	/**
	 * Returns an immutable snapshot of all registered internal names.
	 *
	 * @return the registered compiler names
	 * @deprecated Replaced by {@link #names()}.
	 */
	@Deprecated(since = "10.2.0", forRemoval = false)
	public static Set<String> keySet() {
		return Set.copyOf(registeredcompilers.keySet());
	}
	
	/**
	 * Returns an immutable snapshot of all registered internal names.
	 *
	 * @return the registered compiler names
	 */
	public static Set<String> names() {
		return Set.copyOf(registeredcompilers.keySet());
	}
	
	/**
	 * Returns an immutable snapshot of all registered display names.
	 *
	 * @return the registered compiler display names
	 */
	public static Set<String> displaynames() {
		return registeredcompilers.values().stream().map(CompilerInstance::displayname)
				.collect(Collectors.toUnmodifiableSet());
	}
	
	/**
	 * Returns an immutable snapshot of all registered compiler instances.
	 *
	 * <p>
	 * Because this method returns a set, compiler instances considered equal
	 * according to their {@link Object#equals(Object)} implementation may be
	 * collapsed into a single element.
	 * </p>
	 *
	 * @return the registered compiler instances
	 */
	public static Set<AHudCompiler<?>> compilers() {
		return registeredcompilers.values().stream().map(CompilerInstance::compiler)
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Stops all registered compiler executors.
	 *
	 * <p>This should be called when the Minecraft client begins shutting down so
	 * idle compiler workers do not remain parked after the game has closed.</p>
	 */
	public static void shutdownAll() {
		compilers().forEach(AHudCompiler::shutdown);
	}
	
	/**
	 * Returns an immutable snapshot of all compiler registry entries.
	 *
	 * @return all registered compiler entries
	 */
	public static Set<CompilerEntry> entries() {
		return registeredcompilers.entrySet().stream().map(entry -> toEntry(entry.getKey(), entry.getValue()))
				.collect(Collectors.toUnmodifiableSet());
	}
	
	/**
	 * Returns an immutable snapshot containing only stable compiler entries.
	 *
	 * @return all compiler entries not marked as unstable
	 */
	public static Set<CompilerEntry> stableEntries() {
		return entries().stream().filter(entry -> !entry.unstable()).collect(Collectors.toUnmodifiableSet());
	}
	
	/**
	 * Returns an immutable snapshot containing only unstable compiler entries.
	 *
	 * @return all compiler entries marked as unstable
	 */
	public static Set<CompilerEntry> unstableEntries() {
		return entries().stream().filter(CompilerEntry::unstable).collect(Collectors.toUnmodifiableSet());
	}
	
	/**
	 * Converts an internal registry value into a public compiler entry.
	 *
	 * @param name     the internal compiler name
	 * @param instance the internal compiler information
	 * @return the public representation of the registry entry
	 */
	private static CompilerEntry toEntry(String name, CompilerInstance instance) {
		
		return new CompilerEntry(name, instance.displayname(), instance.unstable(), instance.compiler());
	}
	
	/**
	 * Internal representation of a registered compiler.
	 *
	 * @param displayname the user-facing compiler name
	 * @param unstable    whether the compiler is experimental
	 * @param compiler    the compiler instance
	 */
	private static record CompilerInstance(String displayname, boolean unstable, AHudCompiler<?> compiler) {
	}
	
	/**
	 * Public immutable representation of a registered compiler entry.
	 *
	 * @param name        the internal compiler name
	 * @param displayname the user-facing compiler name
	 * @param unstable    whether the compiler is experimental
	 * @param compiler    the compiler instance
	 */
	public static record CompilerEntry(String name, String displayname, boolean unstable, AHudCompiler<?> compiler) {
	}
}