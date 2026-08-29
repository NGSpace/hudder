package dev.ngspace.hudder.api.compilers.compilers;

import java.io.File;
import java.io.IOException;
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

	protected final Map<String, T> instances;
	protected final Map<String, Exception> errors = new HashMap<>();
	protected final HudderConfig config;
	protected T mainInstance = null;
	
	protected AHudCompiler(HudderConfig config, Map<String, T> instancesMap) {
		this.config = config;
		this.instances = instancesMap;
	}
	
	/**
	 * Processes a HUD file into the representation used by this compiler.
	 *
	 * @param filepath the path of the file to process
	 * @return the processed representation of the HUD file
	 * @throws CompileException if the file cannot be compiled
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	public abstract T processFile(String filepath) throws CompileException, IOException;

	/**
	 * Executes a previously processed HUD file.
	 *
	 * @param processedfile the processed representation of the HUD file
	 * @param filename the name of the HUD file being executed
	 * @return information describing the executed HUD
	 * @throws ExecutionException if an error occurs while executing the HUD
	 */
	public abstract HudInformation execute(T processedfile, String filename) throws ExecutionException;
	
	/**
	 * Processes and then executes a HUD file.
	 *
	 * @param filepath the path of the file to process
	 * @param filename the name of the HUD file being executed
	 * @return information describing the executed HUD
	 * @throws CompileException if the file cannot be compiled
	 * @throws ExecutionException if an error occurs while executing the HUD
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	public HudInformation processAndExecute(String filepath, String filename)
			throws CompileException, ExecutionException, IOException {
		if (errors.containsKey(filepath)) {
			var exception = errors.get(filepath);
			if (exception instanceof CompileException e)
				throw e;
			if (exception instanceof IOException e)
				throw e;
			throw new CompileException(exception);
		}
		T res = instances.get(filepath);
		if (res==null) {
			try {
				res = processFile(filepath);
				instances.put(filepath, res);
			} catch (CompileException | IOException e) {
				errors.put(filepath, e);
				throw e;
			}
		}
		return execute(res, filename);
	}
	
	/*
	 * I fought and struggled against many, many concurrency issues...
	 * But then I remembered this isn't real multi-threading.
	 * Sure there are issues but I am not trading my sanity for thread-safety in hud compilation
	 * At the end of the day, if any compilers are having issues with this, they can @Override this method
	 * And map it to #processAndExecute or smt, idk, and idc anymore.
	 */
	public HudInformation processAndExecuteMain(String filepath, String filename)
			throws CompileException, ExecutionException, IOException {
		if (mainInstance!=null) {
			mainInstance = processFile(filepath);
			instances.put(filepath, mainInstance);
		}
		return execute(mainInstance, filename);
	}

	/**
	 * Stops pending compiler work and releases this compiler's worker thread.
	 *
	 * <p>This method is safe to call more than once.</p>
	 */
	public void shutdown() {}
	
	public abstract String[] getSupportedFileFormats();
	
	public boolean isValidFilePath(String filepath) {
		for (String format : getSupportedFileFormats())
			if (filepath.endsWith('.'+format))
				return true;
		return false;
	}
	
	public void edit(File file) {
		Util.getPlatform().openFile(file);
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
		instances.clear();
		errors.clear();
	}
}