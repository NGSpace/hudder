package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.MethodValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class InventoryInformationMethods implements IMethod {
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	@Override public boolean isDeprecated(String name) {return true;}
	
	@Override
	public String getDeprecationWarning(String name) {
		return name + " is Deprecated and will be removed, use the item" +name + "([Slot number]) function instead!";
	}
	
	@Override
	public void invoke(ConfigInfo config, CompileState m, ATextCompiler c, String type, int line, int charpos, MethodValue... s) throws CompileException {
		ItemStack stack = mc.player.getInventory().getStack(s[0].asInt());
		Object value = switch (type) {
			case "name"         ->stack.getName().getString();
			case "durability"   ->stack.getMaxDamage()-stack.getDamage();
			case "maxdurability"->stack.getMaxDamage();
			case "count"        ->stack.getCount();
			case "maxcount"     ->stack.getMaxCount();
			default -> throw new IllegalArgumentException();
		};
		c.put(s[1].getAbsoluteValue(), value);
	}
}
