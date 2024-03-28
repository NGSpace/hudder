package io.github.ngspace.hudder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

import io.github.ngspace.hudder.config.ConfigInfo;

public class TextReader {
	ConfigInfo info;
	HashMap<String, String> savedfiles = new HashMap<String, String>();
	
	public String getText(String file) {
		if (!savedfiles.containsKey(file))
			try {
				File f = new File(file);
//				if (!f.exists()) {
//					f.getParentFile().mkdirs();
//					if (!f.createNewFile()) return "§cError while creating file: Unknown";
//				}
				savedfiles.put(file,new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "§aError while reading file: " + e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return "§cNo such file file: " + new File(file).getName() + "   " + e.getMessage();
			}
		return savedfiles.get(file);
	}
	
	public boolean ReadFile(String file) {
		try {
			File f = new File(file);
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				if (!f.createNewFile()) return false;
			}
			savedfiles.put(file,new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
