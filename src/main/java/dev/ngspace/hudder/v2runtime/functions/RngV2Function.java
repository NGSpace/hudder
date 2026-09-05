package dev.ngspace.hudder.v2runtime.functions;

import java.util.HashMap;
import java.util.Random;

import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class RngV2Function implements IV2Function, BindablePositionedFunction {
	
	private Random random = new Random();
	private HashMap<Integer, Random> randoms = new HashMap<Integer, Random>();

	@Override public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos) 
			throws ExecutionException {
		return (args.length==3? getRandom(args[2].asInt()) : random).nextDouble(args[0].asDouble(),args[1].asDouble());
	}
	
	public Random getRandom(int Seed) {
		var rng = randoms.get(Seed);
		if (rng==null) {
			rng = new Random(Seed);
			randoms.put(Seed, rng);
		}
		return rng;
	}

	@Override
	public Object invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos pos, HudderConfig config,
			ObjectWrapper... args) throws ExecutionException {
		return (args.length==3? getRandom(args[2].asInt()) : random).nextDouble(args[0].asDouble(),args[1].asDouble());
	}
}
