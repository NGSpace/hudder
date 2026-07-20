package dev.ngspace.hudder.hudpacks;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public interface EntryReaderConsumer extends Closeable {
	
	public InputStream readEntry(String t) throws IOException;
	public String[] listEntries() throws IOException;
	
	public static class Zip implements EntryReaderConsumer {
		
		private ZipFile file;

		public Zip(File file) throws IOException {
			this.file = new ZipFile(file);
		}

		@Override
		public InputStream readEntry(String t) throws IOException {
			return file.getInputStream(file.getEntry(t));
		}

		@Override
		public String[] listEntries() throws IOException {
			List<String> list = new ArrayList<String>();
			var entries = file.entries();
			while (entries.hasMoreElements()) {
				String entry = entries.nextElement().getName();
				if (entry.endsWith("/")) continue;
				list.add(entry);
			}
			return list.toArray(new String[list.size()]);
		}

		@Override
		public void close() throws IOException {
			file.close();
		}
		
	}
	
	public static class Directory implements EntryReaderConsumer {
		
		private File dir;

		public Directory(File dir) {
			this.dir = dir;
		}

		@Override
		public InputStream readEntry(String t) throws IOException {
			return new FileInputStream(new File(dir.getAbsolutePath() + "/" + t));
		}

		@Override
		public void close() throws IOException {/* Nothin to close */}

		@Override
		public String[] listEntries() throws IOException {
			return listEntries("", dir).toArray(new String[0]);
		}
		
		public List<String> listEntries(String prefix, File folder) throws IOException {
			List<String> list = new ArrayList<String>();
			for (File file : folder.listFiles()) {
				if (file.isDirectory()) {
					list.addAll(listEntries(prefix + "/" + file.getName(), file));
					continue;
				}
				list.add((prefix + "/" + file.getName()).substring(1));
			}
			return list;
		}
		
	}
}
