package dev.ngspace.hudder.hudpacks;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public interface EntryReaderConsumer extends Closeable {
	
	public InputStream readEntry(String t) throws IOException;
	public String[] listEntries() throws IOException;
	
	public static class Zip implements EntryReaderConsumer {
		
		private ZipFile file;

		public Zip(Path path) throws IOException {
			this.file = new ZipFile(path.toFile());
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
		
		private Path dir;

		public Directory(Path path) {
			this.dir = path;
		}

		@Override
		public InputStream readEntry(String t) throws IOException {
			return Files.newInputStream(dir.toAbsolutePath().resolve(t));
		}

		@Override
		public void close() throws IOException {/* Nothin to close */}

		@Override
		public String[] listEntries() throws IOException {
			return listEntries("", dir).toArray(new String[0]);
		}
		
		public List<String> listEntries(String prefix, Path folder) throws IOException {
			List<String> list = new ArrayList<String>();
			for (Path path : Files.newDirectoryStream(folder)) {
				if (Files.isDirectory(path)) {
					list.addAll(listEntries(prefix + "/" + path.getFileName(), path));
					continue;
				}
				list.add((prefix + "/" + path.getFileName()).substring(1));
			}
			return list;
		}
		
	}
}
