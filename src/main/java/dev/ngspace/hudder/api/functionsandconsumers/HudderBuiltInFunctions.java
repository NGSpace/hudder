package dev.ngspace.hudder.api.functionsandconsumers;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class HudderBuiltInFunctions {private HudderBuiltInFunctions() {}
	public static Minecraft mc = Minecraft.getInstance();
	
	public static void registerFunction(FunctionAndConsumerAPI binder) {
		
		//Getters
		
		binder.registerFunction((_,c,_,s)->c.getVariable(s[0].asString()), "get", "getVal", "getVariable");
		binder.registerFunction((_,_,_,s)->DataVariableRegistry.getNumber  (s[0].asString()), "getNumber" );
		binder.registerFunction((_,_,_,s)->DataVariableRegistry.getString  (s[0].asString()), "getString" );
		binder.registerFunction((_,_,_,s)->DataVariableRegistry.getObject  (s[0].asString()), "getObject" );
		binder.registerFunction((_,_,_,s)->DataVariableRegistry.getBoolean (s[0].asString()), "getBoolean");
		
		binder.registerFunction((_,_,_,s)->new TranslatedItemStack(mc.player.getInventory().getItem(s[0].asInt())), "getItem");
		
		binder.registerFunction((_,_,_,s)->Hudder.config.savedVariables().get(s[0].asString()),"readValue");
		
		//Stats
		
		binder.registerFunction((_,_,p,s)->{
			updateStats();
			Identifier blockId = s[0].asIdentifier();
			var block = BuiltInRegistries.BLOCK.get(blockId);
			if (block.isEmpty())
				throw new ExecutionException("Unknown block ID: \"" + blockId + '"', p);
			return mc.player.getStats().getValue(Stats.BLOCK_MINED, block.get().value());
		}, "getTimesMinedStat");
		
		binder.registerFunction((_,_,_,s)->getItemStat(Stats.ITEM_CRAFTED, s[0].asIdentifier()),
				"getTimesCraftedStat");
		
		binder.registerFunction((_,_,_,s)->getItemStat(Stats.ITEM_USED, s[0].asIdentifier()),
				"getTimesUsedStat");
		
		binder.registerFunction((_,_,_,s)->getItemStat(Stats.ITEM_BROKEN, s[0].asIdentifier()),
				"getTimesBrokenStat");
		
		binder.registerFunction((_,_,_,s)->getItemStat(Stats.ITEM_PICKED_UP, s[0].asIdentifier()),
				"getTimesPickedUpStat");
		
		binder.registerFunction((_,_,_,s)->getItemStat(Stats.ITEM_DROPPED, s[0].asIdentifier()),
				"getTimesDroppedStat");
		
		binder.registerFunction((_,_,_,s)->getEntityStat(Stats.ENTITY_KILLED, s[0].asIdentifier()),
				"getTimesKilledStat");
		
		binder.registerFunction((_,_,_,s)->getEntityStat(Stats.ENTITY_KILLED_BY, s[0].asIdentifier()),
				"getTimesKilledByStat");
		
		binder.registerFunction((_,_,p,s)->{
			updateStats();
			Identifier statId = s[0].asIdentifier();
			ObjectArrayList<Stat<Identifier>> stats = new ObjectArrayList<>(Stats.CUSTOM.iterator());
			
			for (Stat<Identifier> stat : stats)
				if (stat.getValue().equals(statId))
					return mc.player.getStats().getValue(stat);
			
			throw new ExecutionException("Unknown custom stat ID: \"" + statId + '"', p);
		}, "getCustomStat"); // https://minecraft.wiki/w/Statistics#List_of_custom_statistic_names
		
		//Keybinds

		binder.registerFunction((_,_,_,s)->KeyMapping.get(s[0].asString()).isDown(), "isKeybindDown");
		binder.registerFunction((_,_,_,s)->KeyMapping.get(s[0].asString()).isDefault(), "isKeybindDefault");
		binder.registerFunction((_,_,_,s)->KeyMapping.get(s[0].asString()).isUnbound(), "isKeybindUnbound");
		
		//Compile
		
		binder.registerFunction((m,_,_,s)-> {
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
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new ExecutionException(e1);
			}
		}, "compile", "run", "execute");
		
		
		//Misc
		
		binder.registerFunction((_,_,p,s)->{
			try {
				return HudFileUtils.exists(s[0].asString());
			} catch (IOException e) {
				e.printStackTrace();
				throw new ExecutionException(e, p);
			}
		},"exists");
		binder.registerFunction((_,_,_,s)->mc.font.width(s[0].asString()), "strWidth", "strwidth");
		binder.registerFunction((_,_,_,s)->s[0].get().toString(), "toString");
		binder.registerUnsafeFunction((_,_,_,_)->new HashMap<Object, Object>(), "map");
	}
	
	static Instant laststatsupdate = Instant.now();

	public static void updateStats() {
		Instant now = Instant.now();
		var connection = mc.getConnection();
		if (connection!=null&&Duration.between(laststatsupdate, now).toMillis()>1000) {
			laststatsupdate = Instant.now();
			connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.
					Action.REQUEST_STATS));
		}
	}

	public static Object getItemStat(StatType<Item> stattype, Identifier itemId) throws ExecutionException {
		updateStats();
		var item = BuiltInRegistries.ITEM.get(itemId);
		if (item.isEmpty())
			throw new ExecutionException("Unknown item ID: \"" + itemId + '"', -1, -1);
		return mc.player.getStats().getValue(stattype, item.get().value());
	}

	public static Object getEntityStat(StatType<EntityType<?>> stattype, Identifier entityId) throws ExecutionException {
		updateStats();
		var entity = BuiltInRegistries.ENTITY_TYPE.get(entityId);
		if (entity.isEmpty())
			throw new ExecutionException("Unknown entity ID: \"" + entityId + '"', -1, -1);
		return mc.player.getStats().getValue(stattype, entity.get().value());
	}
}
