package dev.ngspace.hudder.compilers.abstractions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import net.minecraft.util.Util;

/**
 * Defines the common operations required to process and execute HUD files.
 *
 * @param <T> the type produced when a HUD file is processed
 */
public abstract class AHudCompiler<T> {

	/**
	 * Contains globally available compiler variables, indexed by name.
	 */
	public static Map<String, Object> variables = new HashMap<String, Object>();
	protected final ExecutorService hudCompilerExecutor =
	        Executors.newSingleThreadExecutor(r -> new Thread(r, "hud-compiler"));
	private final AtomicBoolean hudCompiling = new AtomicBoolean(false);
	
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
	 * @param info the Hudder configuration used during execution
	 * @param processedfile the processed representation of the HUD file
	 * @param filename the name of the HUD file being executed
	 * @return information describing the executed HUD
	 * @throws ExecutionException if an error occurs while executing the HUD
	 */
	public abstract HudInformation execute(HudderConfig info, T processedfile, String filename) throws ExecutionException;

	/**
	 * Retrieves a variable using the specified key.
	 *
	 * @param key the key of the variable to retrieve
	 * @return the value associated with the specified key
	 * @throws ExecutionException if the variable cannot be retrieved
	 */
	public abstract Object getVariable(String key) throws ExecutionException;
	
	
	/**
	 * Sets up the configurable settings used by HUDs compiled by this compiler.
	 *
	 * @param hudsettings the configuration category in which HUD settings are set up
	 * @return {@code true} if the HUD settings were set up successfully;
	 *         {@code false} otherwise
	 */
	public abstract boolean setupHudSettings(NGSMCConfigCategory hudsettings);
	
	/**
	 * Processes and then executes a HUD file.
	 *
	 * @param config the Hudder configuration used during execution
	 * @param filepath the path of the file to process
	 * @param filename the name of the HUD file being executed
	 * @return information describing the executed HUD
	 * @throws CompileException if the file cannot be compiled
	 * @throws ExecutionException if an error occurs while executing the HUD
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	public HudInformation processAndExecute(HudderConfig config, String filepath, String filename)
			throws CompileException, ExecutionException, IOException {
		return execute(config, processFile(filepath), filename);
	}
	

	public HudInformation processAndExecuteSafe(HudderConfig config, String filepath, String filename)
			throws CompileException, ExecutionException, IOException {
		
	    // Immediately reject the call if another HUD is still being processed.
	    if (!hudCompiling.compareAndSet(false, true)) {
	        throw new CompileException("Hud still processing",-1,-1);
	    }
		
	    var compilation = hudCompilerExecutor.submit(() -> {
	    	try {
	    		return processFile(filepath);
	    	} finally {
		        hudCompiling.set(false);
			}
	    });

	    try {
			return execute(config, compilation.get(1000, TimeUnit.MILLISECONDS), filename);
	    } catch (TimeoutException _) {
	        throw new CompileException("Hud still processing",-1,-1);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        throw new IOException("Interrupted while waiting for Hud to finish processing", e);
	    } catch (java.util.concurrent.ExecutionException e) {
			e.printStackTrace();
	        throw new CompileException(e.getMessage(),-1,-1,e);
		}
	}
	
	public String[] getSupportedFileFormats() {
		return new String[0];// By default, none;
	}
	
	public boolean isValidFilePath(String filepath) {
		for (String format : getSupportedFileFormats())
			if (filepath.endsWith('.'+format))
				return true;
		return false;
	}
	
	public void edit(File file) {
		Util.getPlatform().openFile(file);
	}
}