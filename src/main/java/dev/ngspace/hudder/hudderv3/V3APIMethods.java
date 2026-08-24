package dev.ngspace.hudder.hudderv3;

import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.exceptions.ExecutionException;

public class V3APIMethods {
	private V3APIMethods() {}

	public static void bindAllAPIMethods(PositionedBinder binder) {
		//Logging and errors
		binder.bindConsumer((_,_,p,_,s)->{throw new ExecutionException(s[0].asString(),p);}, "throw");
	}
	
}
