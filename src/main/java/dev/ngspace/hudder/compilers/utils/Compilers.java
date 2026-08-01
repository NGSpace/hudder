package dev.ngspace.hudder.compilers.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.compilers.HudderV2Compiler;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.JavaScriptCompiler;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;

public class Compilers {private Compilers() {}
	
	private static Map<String, CompilerInstance> registeredcompilers = new HashMap<String, CompilerInstance>();
	//Made this to make sure there aren't a fuckton of HudderV2Compiler objects being initalized by accident
	public static final HudderV2Compiler hudderV2Compiler = new HudderV2Compiler();
	
	
	public static void registerDefaultCompilers() {
		put("hudder", "Hudder", false, hudderV2Compiler);
		put("js", "JavaScript", false, new JavaScriptCompiler());
		put("pack", "Hudpack", false, new HudPackCompiler());
		put("hudderv3", "Hudder V3 (Exp)", true, new HudderV3Compiler());
	}
	
	public static AHudCompiler<?> getCompilerFromName(String name) throws IllegalArgumentException {
		String comp = name.toLowerCase();
		if (registeredcompilers.containsKey(comp)) return registeredcompilers.get(comp).compiler();
		
		throw getNoCompilerException(name, "name");
	}
	
	public static String getCompilerNameFromDisplayname(String displayname) {
		return entries().stream()
				.filter(e->e.displayname().equals(displayname))
				.findFirst()
				.orElseThrow(()->getNoCompilerException(displayname, "display name"))
				.name();
	}
	
	public static AHudCompiler<?> getCompilerFromDisplayname(String displayname) {
		return entries().stream()
				.filter(e->e.displayname().equals(displayname))
				.findFirst()
				.orElseThrow(()->getNoCompilerException(displayname, "display name"))
				.compiler();
	}

	public static String getDisplayNameFromCompilerName(String name) {
		return entries().stream()
				.filter(e->e.name().equals(name))
				.findFirst()
				.orElseThrow(()->getNoCompilerException(name, "name"))
				.displayname();
	}

	public static CompilerEntry getEntryFromDisplayName(String name) {
		return entries().stream()
				.filter(e->e.displayname().equals(name))
				.findFirst()
				.orElseThrow(()->getNoCompilerException(name, "displayname"));
	}
	
	private static IllegalArgumentException getNoCompilerException(String value, String input_type) {
		return new IllegalArgumentException(
				"Compiler with the "+input_type + " \"" + value + "\" either does not exist or has not yet been loaded.");
	}
	
	/**
	 * @deprecated use {@code #registerCompiler(String, ATextCompiler)}
	 */
	@Deprecated(since = "9.0.0", forRemoval = true)
	public static void registerCompiler(String name, String classname) {
		try {
			registerCompiler(name,(AHudCompiler<?>) Class.forName(classname).getConstructor().newInstance());
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Failed to load compiler", e);
		}
	}

	/**
	 * @deprecated use {@code #registerCompiler(String, String, ATextCompiler)} to specify a displayname and stability status
	 */
	@Deprecated(since = "10.2.0", forRemoval = false)
	public static void registerCompiler(String name, AHudCompiler<?> compiler) {
		registerCompiler(name, name, false, compiler);
	}
	
	public static void registerCompiler(String name, String displayname, boolean isUnstable, AHudCompiler<?> compiler) {
		put(name.toLowerCase(), displayname, isUnstable, compiler);
		if (Hudder.config!=null)
			Hudder.config.readAndUpdateConfig();
	}
	
	private static void put(String name, String displayname, boolean isUnstable, AHudCompiler<?> compiler) {
		registeredcompilers.put(name, new CompilerInstance(displayname, isUnstable, compiler));
	}
	
	public static boolean has(String name) {
		return registeredcompilers.get(name.toLowerCase())!=null;
	}

	public static boolean hasCompilerFromDisplayName(String name) {
		return entries().stream()
				.anyMatch(e->e.displayname().equals(name));
	}
	
	/**
	 * @deprecated replaced by {@link #names()}
	 */
	@Deprecated(since = "10.2.0", forRemoval = false)
	public static Set<String> keySet() {
		return Set.copyOf(registeredcompilers.keySet());
	}

	public static Set<String> names() {
		return Set.copyOf(registeredcompilers.keySet());
	}

	public static Set<String> displaynames() {
		return registeredcompilers.values()
				.stream()
				.map(ins->ins.displayname())
				.collect(Collectors.toUnmodifiableSet());
	}

	public static Set<AHudCompiler<?>> compilers() {
		return registeredcompilers.values()
				.stream()
				.map(ins->ins.compiler())
				.collect(Collectors.toUnmodifiableSet());
	}

	public static Set<CompilerEntry> entries() {
		return registeredcompilers.entrySet()
				.stream()
				.map(e->new CompilerEntry(e.getKey(), e.getValue().displayname, e.getValue().unstable(),
						e.getValue().compiler()))
				.collect(Collectors.toUnmodifiableSet());
	}
	
	private static record CompilerInstance(String displayname, boolean unstable, AHudCompiler<?> compiler) {}
	
	public static record CompilerEntry(String name, String displayname, boolean unstable, AHudCompiler<?> compiler) {}
	
}
