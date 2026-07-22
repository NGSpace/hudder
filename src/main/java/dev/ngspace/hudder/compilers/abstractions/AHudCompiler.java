package dev.ngspace.hudder.compilers.abstractions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

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
}