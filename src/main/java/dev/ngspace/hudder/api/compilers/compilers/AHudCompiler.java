package dev.ngspace.hudder.api.compilers.compilers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

	protected Future<T> mainCompilation;
	protected ExecutorService hudCompilerExecutor =
	        Executors.newSingleThreadExecutor(r -> {
	        	Thread thread = new Thread(r, "hud-compiler");
	        	// Compilation work must never keep the Minecraft JVM alive during exit.
	        	thread.setDaemon(true);
	        	return thread;
	        });
	protected final AtomicBoolean hudCompiling = new AtomicBoolean(false);
	protected final AtomicReference<T> mainInstance;
	protected final Map<String, T> instances;
	protected final Map<String, Exception> errors = new HashMap<>();
	protected final HudderConfig config;
	
	protected AHudCompiler(HudderConfig config, AtomicReference<T> mainInstance, Map<String, T> instancesMap) {
		this.config = config;
		this.mainInstance = mainInstance;
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
	

	public HudInformation processAndExecuteMain(String filepath, String filename)
			throws CompileException, ExecutionException, IOException {

    	if (mainInstance.get()!=null) {
    		return execute(mainInstance.get(), filename);
    	}
    	
	    // Immediately reject the call if another HUD is still being processed.
	    if (!hudCompiling.compareAndSet(false, true)) {
	        throw new CompileException("Hud still processing",-1,-1);
	    }
		
	    try {
	    	
		    mainCompilation = hudCompilerExecutor.submit(() -> {
		    	try {
		    		T res = processFile(filepath);
		    		instances.put(filepath, res);
		    		mainInstance.set(res);
		    		return mainInstance.get();
		    	} finally {
			        hudCompiling.set(false);
				}
		    });
		    try {
		    	var res = execute(mainCompilation.get(1000, TimeUnit.MILLISECONDS), filename);
		    	mainCompilation = null;
				return res;
		    } catch (TimeoutException _) {
		        throw new CompileException("Hud still processing",-1,-1);
		    } catch (InterruptedException e) {
		        Thread.currentThread().interrupt();
		        throw new IOException("Interrupted while waiting for Hud to finish processing", e);
		    } catch (java.util.concurrent.ExecutionException e) {
		    	if (e.getCause() instanceof CompileException ex)
		    		throw ex;
		    	if (e.getCause() instanceof ExecutionException ex)
		    		throw ex;
		    	if (e.getCause() instanceof IOException ex)
		    		throw ex;
				e.printStackTrace();
				var ex = e.getCause() != null ? e.getCause() : e;
		        throw new CompileException(ex.getMessage(),-1,-1,ex);
			}
	    } catch (RejectedExecutionException e) {
	        hudCompiling.set(false);
	        throw e;
		}
	}

	/**
	 * Stops pending compiler work and releases this compiler's worker thread.
	 *
	 * <p>This method is safe to call more than once.</p>
	 */
	public void shutdown() {
		hudCompilerExecutor.shutdownNow();
	}
	
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
		hudCompilerExecutor.shutdownNow();
		hudCompilerExecutor = Executors.newSingleThreadExecutor(r -> {
	        	Thread thread = new Thread(r, "hud-compiler");
	        	// Compilation work must never keep the Minecraft JVM alive during exit.
	        	thread.setDaemon(true);
	        	return thread;
	        });
		mainInstance.set(null);
		hudCompiling.set(false);
		instances.clear();
		errors.clear();
	}
}