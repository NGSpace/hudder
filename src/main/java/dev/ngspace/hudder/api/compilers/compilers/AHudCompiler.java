package dev.ngspace.hudder.api.compilers.compilers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import net.minecraft.util.Util;

/**
 * Defines the common operations required to process and execute HUD files.
 *
 * @param <T> the type produced when a HUD file is processed
 * 
 * @see CompilerRegistry
 */
public abstract class AHudCompiler<T> {

	protected final Map<Path, T> instances;
	protected final Map<Path, Exception> errors = new HashMap<>();
	protected final HudderConfig config;
	protected T mainInstance = null;
	protected CompileException mainError = null;
	
	protected AHudCompiler(HudderConfig config, Map<Path, T> instancesMap) {
		this.config = config;
		this.instances = instancesMap;
	}
	
	public abstract T processFile(Path path) throws CompileException, IOException;

	public abstract HudInformation execute(T processedfile, String debugname) throws ExecutionException;
	
	public HudInformation processAndExecute(Path path, String debugname)
			throws CompileException, ExecutionException, IOException {
		if (errors.containsKey(path)) {
			var exception = errors.get(path);
			if (exception instanceof CompileException e)
				throw e;
			if (exception instanceof IOException e)
				throw e;
			throw new CompileException(exception);
		}
		T res = instances.get(path);
		if (res==null) {
			try {
				res = processFile(path);
				instances.put(path, res);
			} catch (CompileException | IOException e) {
				errors.put(path, e);
				throw e;
			}
		}
		return execute(res, debugname);
	}
	
	/*
	 * I fought and struggled against many, many concurrency issues...
	 * But then I remembered this isn't real multi-threading.
	 * Sure there are issues but I am not trading my sanity for thread-safety in hud compilation
	 * At the end of the day, if any compilers are having issues with this, they can @Override this method
	 * And map it to #processAndExecute or smt, idk, and idc anymore.
	 * 
	 * Update: That's what I eventually did lol
	 */
	public HudInformation processAndExecuteMain(Path path, String debugname)
			throws CompileException, ExecutionException, IOException {
		if (mainError!=null)
			throw mainError;
		try {
			if (mainInstance==null) {
				mainInstance = processFile(path);
				instances.put(path, mainInstance);
			}
		} catch (CompileException e) {
			mainError = e;
			throw e;
		}
		return execute(mainInstance, debugname);
	}

	public void shutdown() {}
	
	public abstract String[] getSupportedFileFormats();
	
	public boolean isValidFilePath(Path filepath) {
	    String filename = filepath.getFileName().toString().toLowerCase();
		for (String format : getSupportedFileFormats())
			if (filename.endsWith('.'+format.toLowerCase()))
				return true;
		return false;
	}
	
	public void edit(Path file) {
		// I know openPath exists but for some reason it just doesn't work
		Util.getPlatform().openFile(file.toFile());
	}
	
	/**
	 * Resets the compiler's state to be as if the game was restarted.
	 * 
	 * Called when settings are changed or reloadResources is called.
	 * 
	 * @throws IOException
	 */
	public void reset() throws IOException {
		mainInstance = null;
		mainError = null;
		instances.clear();
		errors.clear();
	}
}