package io.github.ngspace.hudder.v2runtime.functions;

import java.util.HashMap;
import java.util.Random;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class RngV2Function implements IV2Function {
	
	private Random random = new Random();
	private HashMap<Integer, Random> randoms = new HashMap<Integer, Random>();

	@Override public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos)
			throws CompileException {
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
}
