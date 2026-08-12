package dev.ngspace.hudder.hudderv3;

import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.methods.LoadMethod;

public class HudderAPIMethods {
	private HudderAPIMethods() {}

	public static void bindAllAPIMethods(PositionedBinder binder) {

		
		//Compiler and Variables
		binder.bindConsumer(new LoadMethod(), "load", "execute", "compile", "run", "add");
		
		//Logging and errors
		binder.bindConsumer((_,_,p,s)->{throw new ExecutionException(s[0].asString(),p);}, "throw");
	}
	
}
