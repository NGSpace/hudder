package dev.ngspace.hudder.testing;

import dev.ngspace.hudder.utils.ValueGetter;

public class WrapperTest {
	
	public String method1() {
		return "1";
	}
	
	public String method2() {
		return "2";
	}
	
	public static class WrapperTestWrapper implements ValueGetter {
		
	    public final WrapperTest original;
	    public WrapperTestWrapper(WrapperTest original) {
	        this.original = original;
	    }
	    public String method1() {
	        return original.method1();
	    }
	
		@Override
		public Object get(String Id) {
			return null;
		}
		
	}
}
