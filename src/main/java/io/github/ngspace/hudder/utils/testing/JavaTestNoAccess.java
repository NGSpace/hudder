package io.github.ngspace.hudder.utils.testing;

import io.github.ngspace.hudder.utils.NoAccess;

@NoAccess
public class JavaTestNoAccess {
	
	public ImportantDataHold ImportantDataHolder = new ImportantDataHold();
	
	@NoAccess
	public class ImportantDataHold {
		public String veryImportantData = "I luv u";
	}
}
