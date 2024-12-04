package io.github.ngspace.hudder.data_management;

import java.util.HashMap;

import io.github.ngspace.hudder.utils.ValueGetter;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess.RegistryEntry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@SuppressWarnings("unused")
public class ComponentsData extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 229002063971831208L;
	
	public static Object getObject(String key, DataComponentMap comp) {
		
		return switch (key.toLowerCase()) {
			
			/* Text Objects */
			case "custom_name", "item_name": {
				Component t = (Component) comp.get(BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(ResourceLocation.withDefaultNamespace(key)));
				
				yield t!=null ? t.getString() : null;
			}
			
			/* Primitives */
			case "repair_cost", "damage", "max_damage", "max_stack_size", "enchantment_glint_override":
				yield comp.get(BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(ResourceLocation.withDefaultNamespace(key)));
			
//			case "mining_speed": yield comp.get(DataComponentTypes.TOOL).defaultMiningSpeed();

			case "trim": yield comp.get(DataComponents.TRIM) !=null ? new Object() {
				String material = comp.get(DataComponents.TRIM).material().value().assetName();
				String pattern = comp.get(DataComponents.TRIM).pattern().value().assetId().toString();
				boolean showintooltip = comp.get(DataComponents.TRIM).showInTooltip();
			} : null;

			case "enchantable": yield comp.get(DataComponents.ENCHANTABLE) !=null ? 
					comp.get(DataComponents.ENCHANTABLE).value() : null;
			case "lore": yield comp.get(DataComponents.LORE) !=null ? 
					comp.get(DataComponents.LORE).styledLines() : null;
			case "rarity": yield comp.get(DataComponents.RARITY) !=null ? 
					comp.get(DataComponents.RARITY).toString() : null;
			
			case "unbreakable": yield comp.get(DataComponents.UNBREAKABLE);
			
			case "custom_data": yield String.valueOf(comp.get(DataComponents.CUSTOM_DATA));
			
			case "enchantments": {
				var d = comp.get(DataComponents.ENCHANTMENTS);
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
}
