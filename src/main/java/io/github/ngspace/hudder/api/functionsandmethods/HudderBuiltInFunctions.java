package io.github.ngspace.hudder.api.functionsandmethods;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.compilers.utils.functionandmethodapi.FunctionAndMethodAPI;
import io.github.ngspace.hudder.compilers.utils.functionandmethodapi.FunctionAndMethodAPI.TranslatedItemStack;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.ObjectDataAPI;
import io.github.ngspace.hudder.data_management.StringData;
import io.github.ngspace.hudder.main.HudCompilationManager;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.Minecraft;

public class HudderBuiltInFunctions {
	public static Minecraft mc = Minecraft.getInstance();
	
	public static void registerFunction(FunctionAndMethodAPI binder) {
		
		//Getters
		
		binder.registerFunction((m,c,s)->c.getVariable(s[0].asString()), "get", "getVal", "getVariable");
		binder.registerFunction((m,c,s)->NumberData.getNumber   (s[0].asString()), "getNumber" );
		binder.registerFunction((m,c,s)->StringData.getString   (s[0].asString()), "getString" );
		binder.registerFunction((m,c,s)->ObjectDataAPI.getObject(s[0].asString()), "getObject" );
		binder.registerFunction((m,c,s)->BooleanData.getBoolean (s[0].asString()), "getBoolean");
		
		binder.registerFunction((m,c,s)->new TranslatedItemStack(mc.player.getInventory().getItem(s[0].asInt())), "getItem");
		
//		binder.bindFunction((m,c,s)->c.getConfig().savedVariables.get(s[0].asString()),"readVal");
		
		//Compile
		
		binder.registerFunction((m,c,s)-> {
			try {
				var e = m.toElementArray();
				
				ATextCompiler ecompiler = Compilers.getCompilerFromName(s[1].asString());
				for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
				
				HudInformation result = ecompiler.compile(Hudder.config,HudFileUtils.readFile(s[0].asString()),s[0].asString());

				for (var v : result.elements) m.addElem(v);
				for (var v : e) m.addElem(v);
				
				for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
				return result;
			} catch (ReflectiveOperationException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new IllegalArgumentException("Unknown compiler");
			}
		}, "compile", "run", "execute");
		
		
		//Misc
		
		binder.registerFunction((m,c,s)->HudFileUtils.exists(s[0].asString()),"exists");
		binder.registerFunction((m,c,s)->mc.font.width(s[0].asString()), "strWidth", "strwidth");
		binder.registerFunction((m,c,s)->s[0].get().toString(), "toString");
	}
}
