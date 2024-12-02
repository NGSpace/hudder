package io.github.ngspace.hudder.data_management;

import java.util.HashMap;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import io.github.ngspace.hudder.utils.ValueGetter;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class ComponentsData extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 229002063971831208L;
	
	public static Object getObject(String key, ComponentMap comp) {
		
		return switch (key.toLowerCase()) {
			
			/* Text Objects */
			case "custom_name", "item_name": {
				Text t = (Text) comp.get(Registries.DATA_COMPONENT_TYPE.get(Identifier.of(key)));
				
				yield t!=null ? t.getString() : null;
			}
			
			/* Primitives */
			case "repair_cost", "damage", "max_damage", "max_stack_size", "enchantment_glint_override":
				yield comp.get(Registries.DATA_COMPONENT_TYPE.get(Identifier.of(key)));
			
//			case "mining_speed": yield comp.get(DataComponentTypes.TOOL).defaultMiningSpeed();

			case "trim": yield comp.get(DataComponentTypes.TRIM) !=null ? new Object() {
				String material = comp.get(DataComponentTypes.TRIM).material().value().assetName();
				String pattern = comp.get(DataComponentTypes.TRIM).pattern().value().assetId().toString();
				boolean showintooltip = comp.get(DataComponentTypes.TRIM).showInTooltip();
			} : null;

			case "enchantable": yield comp.get(DataComponentTypes.ENCHANTABLE) !=null ? 
					comp.get(DataComponentTypes.ENCHANTABLE).value() : null;
			case "lore": yield comp.get(DataComponentTypes.LORE) !=null ? 
					comp.get(DataComponentTypes.LORE).styledLines() : null;
			case "rarity": yield comp.get(DataComponentTypes.RARITY) !=null ? 
					comp.get(DataComponentTypes.RARITY).asString() : null;
			
			case "unbreakable": yield comp.get(DataComponentTypes.UNBREAKABLE);
			
			case "custom_data": yield String.valueOf(comp.get(DataComponentTypes.CUSTOM_DATA));
			
			case "enchantments": {
				var d = comp.get(DataComponentTypes.ENCHANTMENTS);
				if (d==null) yield null;
				yield new EnchantmentInfo(d);
			}
			
			default: yield null;
		};
	}
	
	public static class EnchantmentInfo implements ValueGetter {
		private ItemEnchantmentsComponent d;
		public EnchantmentInfo(ItemEnchantmentsComponent r) {this.d=r;}
		@Override public Object get(String n) {
			for (var e : d.getEnchantments())
				if (getName(e.value().description()).equals(n))
					return new LevelHolder(d,e);
			return null;
		}
		@Override public String toString() {
			String str = "{";
			for (var e : d.getEnchantments()) {
				str += getName(e.value().description());
				str += ", ";
			}
			if (str.length()!=1) str = str.substring(0,str.length()-2);
			return str + "}";
		}
		private static String getName(Text description) {
			String s = ((TranslatableTextContent) description.getContent()).getKey();
			String[] ss = s.split("\\.");
			return ss[ss.length-1];
		}
	}
	public static class LevelHolder {
		public int level;
		private Enchantment e;
		public LevelHolder(ItemEnchantmentsComponent d, RegistryEntry<Enchantment> e) {
			this.e = e.value();
			this.level = d.getLevel(e);
		}
		@Override public String toString() {return EnchantmentInfo.getName(e.description()) + " " + level;}
	}
}
