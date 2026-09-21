package dev.ngspace.hudder.testing;

import dev.ngspace.hudder.utils.NoAccess;

public class JavaTestPartialNoAccess {
	
	public String visibleField = "visible";
	
	@NoAccess
	public String hiddenField = "hidden";
	
	public JavaTestNoAccess noAccessField = new JavaTestNoAccess();
	
	public String visibleMethod() {
		return "visible";
	}
	
	@NoAccess
	public String hiddenMethod() {
		return "hidden";
	}

	public JavaTestNoAccess getNoAccessObject() {
		return noAccessField;
	}
}
