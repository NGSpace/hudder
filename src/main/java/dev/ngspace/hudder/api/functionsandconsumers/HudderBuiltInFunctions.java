package dev.ngspace.hudder.api.functionsandconsumers;

import java.util.HashMap;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.ATextCompiler;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.TranslatedItemStack;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.Minecraft;

public class HudderBuiltInFunctions {private HudderBuiltInFunctions() {}
	public static Minecraft mc = Minecraft.getInstance();
	
	public static void registerFunction(FunctionAndConsumerAPI binder) {
		
		//Getters
		
		binder.registerFunction((m,c,s)->c.getVariable(s[0].asString()), "get", "getVal", "getVariable");
		binder.registerFunction((m,c,s)->DataVariableRegistry.getNumber  (s[0].asString()), "getNumber" );
		binder.registerFunction((m,c,s)->DataVariableRegistry.getString  (s[0].asString()), "getString" );
		binder.registerFunction((m,c,s)->DataVariableRegistry.getObject  (s[0].asString()), "getObject" );
		binder.registerFunction((m,c,s)->DataVariableRegistry.getBoolean (s[0].asString()), "getBoolean");
		
		binder.registerFunction((m,c,s)->new TranslatedItemStack(mc.player.getInventory().getItem(s[0].asInt())), "getItem");
		
		binder.registerFunction((m,c,s)->c.getConfig().savedVariables().get(s[0].asString()),"readValue");
		
		//Compile
		
		binder.registerFunction((m,c,s)-> {
			var e = m.toUIElementArray();
			
			ATextCompiler ecompiler = Compilers.getCompilerFromName(s[1].asString());
			for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
			
			HudInformation result = ecompiler.compile(Hudder.config,HudFileUtils.readFile(s[0].asString()),s[0].asString());

			for (var v : result.elements()) m.addUIElement(v);
			for (var v : e) m.addUIElement(v);
			
			for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
			return result;
		}, "compile", "run", "execute");
		
		
		//Misc
		
		binder.registerFunction((m,c,s)->HudFileUtils.exists(s[0].asString()),"exists");
		binder.registerFunction((m,c,s)->mc.font.width(s[0].asString()), "strWidth", "strwidth");
		binder.registerFunction((m,c,s)->s[0].get().toString(), "toString");
		binder.registerUnsafeFunction((m,c,s)->new HashMap<Object, Object>(), "map");
	}
}
