package dev.ngspace.hudder.api.functionsandconsumers;

import java.util.HashMap;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.TranslatedItemStack;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.Minecraft;

public class HudderBuiltInFunctions {private HudderBuiltInFunctions() {}
	public static Minecraft mc = Minecraft.getInstance();
	
	public static void registerFunction(FunctionAndConsumerAPI binder) {
		
		//Getters
		
		binder.registerFunction((_,c,s)->c.getVariable(s[0].asString()), "get", "getVal", "getVariable");
		binder.registerFunction((_,_,s)->DataVariableRegistry.getNumber  (s[0].asString()), "getNumber" );
		binder.registerFunction((_,_,s)->DataVariableRegistry.getString  (s[0].asString()), "getString" );
		binder.registerFunction((_,_,s)->DataVariableRegistry.getObject  (s[0].asString()), "getObject" );
		binder.registerFunction((_,_,s)->DataVariableRegistry.getBoolean (s[0].asString()), "getBoolean");
		
		binder.registerFunction((_,_,s)->new TranslatedItemStack(mc.player.getInventory().getItem(s[0].asInt())), "getItem");
		
		binder.registerFunction((_,_,s)->Hudder.config.savedVariables().get(s[0].asString()),"readValue");
		
		//Compile
		
		binder.registerFunction((m,_,s)-> {
			try {
				var e = m.toUIElementArray();
				
				AHudCompiler<?> ecompiler = Compilers.getCompilerFromName(s[1].asString());
				for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
				
				HudInformation result = ecompiler.processAndExecute(Hudder.config,s[0].asString(),s[0].asString());

				for (var v : result.elements()) m.addUIElement(v);
				for (var v : e) m.addUIElement(v);
				
				for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
				return result;
			} catch (CompileException e1) {
				throw new ExecutionException(e1);
			}
		}, "compile", "run", "execute");
		
		
		//Misc
		
		binder.registerFunction((_,_,s)->HudFileUtils.exists(s[0].asString()),"exists");
		binder.registerFunction((_,_,s)->mc.font.width(s[0].asString()), "strWidth", "strwidth");
		binder.registerFunction((_,_,s)->s[0].get().toString(), "toString");
		binder.registerUnsafeFunction((_,_,_)->new HashMap<Object, Object>(), "map");
	}
}
