package dev.ngspace.hudder.hudpacks;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public interface EntryReaderConsumer extends Closeable {
	
    InputStream readEntry(String t) throws IOException;
	
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
		
	}
}
