package io.github.ngspace.hudder.config;

import static java.io.File.separator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.TextReader;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.compilers.DefaultCompiler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class ConfigInfo {
	/* EXPOSED :flushed: */
    @Expose public boolean enabled = true;
    @Expose public String compilertype = "Default";
	@Expose public String mainfile = "tutorial";
	@Expose public int color = 0xd6d6d6;
	@Expose public boolean shadow = true;
	@Expose public boolean showInF3 = false;
	@Expose public float scale = 1f;
	@Expose public int yoffset = 1;
	@Expose public int xoffset = 1;
	@Expose public int lineHeight = 10;
	@Expose public int metaBuffer = 2;
	@Expose public Map<String, Object> globalVariables = new HashMap<String, Object>();
	@Expose public boolean globalVariablesEnabled = true;
//	@Expose public int backgroundcolor = 0xd6d6d6;
//	@Expose public boolean background = false;
	
	public TextReader textReader = new TextReader();
	public ATextCompiler compiler = new DefaultCompiler();
	private File configFile = new File(ConfigInfo.FOLDER + "hud.json");
	
	private static final Map<String, ATextCompiler> loadedcomps = new HashMap<String,ATextCompiler>();
	
	public static final String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static final String FOLDER = FABRIC_CONFIG_FOLDER + separator + Hudder.MOD_ID + separator;
	public ConfigInfo(File f) {
		configFile = f;
		readConfig();
	}
	
	public String getText(String file) {
		return textReader.getText(FOLDER + file);
	}

	public CompileResult compile(String text) throws CompileException {
		if (compiler!=null) return compiler.compile(this, text);
		else throw new CompileException("There is no Compiler!");
	}
	
	public boolean readFile(String file) {
		return textReader.ReadFile(FOLDER + file);
	}

	public void readConfig() {
		try {
			Hudder.LOGGER.info("Reading Hudder config!");
			textReader.ReadFile(configFile.getAbsolutePath());
			String config = textReader.getText(configFile.getAbsolutePath());
			Hudder.LOGGER.info("Loading Hudder Config File:\n" + config);
			GsonBuilder builder = new GsonBuilder(); 
			builder.registerTypeAdapter(File.class, new FileAdapter()); 
			Gson gson = builder.create();
			var type = new TypeToken<Map<String, Object>>(){}.getType();
			Map<String, Object> newinfo = gson.fromJson(config, type);
			Field[] fields = ConfigInfo.class.getFields();
			for(Field f : fields)
				if (!Modifier.isStatic(f.getModifiers())
						&&!Modifier.isPrivate(f.getModifiers())
						&&!Modifier.isFinal(f.getModifiers())
						&&newinfo.get(f.getName())!=null)
					setField(f, newinfo.get(f.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			Hudder.LOGGER.error(e.getLocalizedMessage());
		} finally {
			try {
				compiler = (ATextCompiler) Class.forName(ATextCompiler.compilers.get(compilertype.toLowerCase()))
						.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				/* So many posibilites for something to go wrong so might as well just... :P */
				compiler = new DefaultCompiler();
				e.printStackTrace();
			}
		}
	}
	private void setField(Field f, Object object) throws Exception {
		if (object instanceof Number num) {
			if (f.getType().isAssignableFrom(Integer.class)) {
				f.set(this, (int)num);
			} else if (f.getType().isAssignableFrom(Double.class)) {
				f.set(this, (double)num);
			} else if (f.getType().isAssignableFrom(Float.class)) {
				f.set(this, (float)num);
			} else if (f.getType().isAssignableFrom(Long.class)) {
				f.set(this, (long)num);
			}
		} else f.set(this, object);
	}

	public void saveConfig() {
		Gson gson = new GsonBuilder().registerTypeAdapter(File.class, new FileAdapter())
				.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		String json = gson.toJson(this);
		try (FileWriter fw = new FileWriter(configFile)) {
			fw.append(json);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ATextCompiler getCompilerFromName(String name) throws Exception {
		if (ATextCompiler.compilers.get(name.toLowerCase())==null) return getCompilerFromName("default");
		if (!loadedcomps.containsKey(name)) loadedcomps.put(name,(ATextCompiler) Class.forName
				(ATextCompiler.compilers.get(name.toLowerCase())).getDeclaredConstructor().newInstance());
		return loadedcomps.get(name);
	}
	
	class FileAdapter extends TypeAdapter<File> {
		@Override public File read(JsonReader arg0) throws IOException {
			return new File(ConfigInfo.FOLDER + "hud.json");
		}
		@Override public void write(JsonWriter arg0, File arg1) throws IOException {}
	}

	public boolean shouldDrawResult(MinecraftClient ins) {
		return !ins.options.hudHidden&&(!ins.getDebugHud().shouldShowDebugHud()||showInF3)&&enabled;
	}
}
