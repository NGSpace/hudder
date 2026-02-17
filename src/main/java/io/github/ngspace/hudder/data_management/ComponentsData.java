package io.github.ngspace.hudder.data_management;

import java.util.HashMap;

import io.github.ngspace.hudder.mixin.ItemCooldownsAccessor;
import io.github.ngspace.hudder.utils.ValueGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@SuppressWarnings("unused")
public class ComponentsData extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 229002063971831208L;
	
	public static Object getObject(String key, DataComponentMap data, ItemStack item) {
		var player = Minecraft.getInstance().player;
		return switch (key.toLowerCase()) {
			
			/* Text Objects */
			case "custom_name", "item_name": {
				Component t = (Component) data.get(BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.withDefaultNamespace(key)));
				
				yield t!=null ? t.getString() : null;
			}
			
			/* Primitives */
			case "repair_cost", "damage", "max_damage", "max_stack_size", "enchantment_glint_override":
				yield data.get(BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.withDefaultNamespace(key)));

			case "max_cooldown": {
				var component = data.get(DataComponents.USE_COOLDOWN);
				yield component == null ? 0 : component.seconds() * 20;
			}
			case "cooldown": {
				var group = player.getCooldowns().getCooldownGroup(item);
				var acc = ((ItemCooldownsAccessor)player.getCooldowns());
				var cooldown = acc.getCooldowns().get(group);
				if (cooldown==null)
					yield 0;
				var totaltime = (cooldown.endTime()-acc.getTickCount()) - (cooldown.startTime()-acc.getTickCount());
				yield totaltime - (acc.getTickCount()-cooldown.startTime());
			}

			case "trim":
				yield data.get(DataComponents.TRIM) ==null ? null : new TrimHolder(data);

			case "enchantable":
				var enchantable = data.get(DataComponents.ENCHANTABLE);
				yield enchantable !=null ? enchantable.value() : null;
			case "lore":
				var lore = data.get(DataComponents.LORE);
				yield lore !=null ? lore.styledLines() : null;
			case "rarity":
				var rarity = data.get(DataComponents.RARITY);
				yield rarity !=null ? rarity.toString() : null;
			
			case "unbreakable": yield data.get(DataComponents.UNBREAKABLE);
			
			case "custom_data": yield String.valueOf(data.get(DataComponents.CUSTOM_DATA));
			
			case "enchantments": {
				var d = data.get(DataComponents.ENCHANTMENTS);
				if (d==null) yield null;
				yield new EnchantmentInfo(d);
			}
			
			default: yield null;
		};
	}
	
	public static class EnchantmentInfo implements ValueGetter {
		private ItemEnchantments d;
		public EnchantmentInfo(ItemEnchantments r) {this.d=r;}
		@Override public Object get(String n) {
			for (var e : d.keySet())
				if (getName(e.value().description()).equals(n))
					return new LevelHolder(d,e);
			return null;
		}
		@Override public String toString() {
			String str = "{";
			for (var e : d.keySet()) {
				str += getName(e.value().description());
				str += ", ";
			}
			if (str.length()!=1) str = str.substring(0,str.length()-2);
			return str + "}";
		}
		private static String getName(Component description) {
			String s = ((TranslatableContents) description.getContents()).getKey();
			String[] ss = s.split("\\.");
			return ss[ss.length-1];
		}
	}
	public static class LevelHolder {
		public int level;
		private Enchantment e;
		public LevelHolder(ItemEnchantments d, Holder<Enchantment> e) {
			this.e = e.value();
			this.level = d.getLevel(e);
		}
		@Override public String toString() {return EnchantmentInfo.getName(e.description()) + " " + level;}
	}
	public static class TrimHolder {
		public String material;
		public String pattern;
		public TrimHolder(DataComponentMap data) {
			material = data.get(DataComponents.TRIM).material().value().assets().base().suffix();
			pattern = data.get(DataComponents.TRIM).pattern().value().assetId().toString();
		}
		@Override
		public String toString() {
			return "TrimHolder [material=" + material + ", pattern=" + pattern + "]";
		}
	}
}
