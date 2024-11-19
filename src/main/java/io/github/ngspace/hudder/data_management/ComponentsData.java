package io.github.ngspace.hudder.data_management;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import io.github.ngspace.hudder.util.ValueGetter;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ComponentsData extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 229002063971831208L;
	
    static HashMap<String, Enchantment> enchantments = new HashMap<String, Enchantment>(); static {
	    for (Field field : GLFW.class.getFields()) {
	    	try {
	    		if (field.canAccess(null)) enchantments.put(field.getName().toLowerCase(),(Enchantment) field.get(null));
			} catch (Exception e) {e.printStackTrace();}
	    }
    }
	

	@SuppressWarnings("unused")
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
				yield (ValueGetter) n -> {
					for (var e : d.getEnchantments()) {
						if (e.value().description().getString().equals(n)) {
							return new Object() {
								int level = d.getLevel(e);
							};
						}
					}
					return null;
				};
			}
			
			default: yield null;
		};
	}
}
