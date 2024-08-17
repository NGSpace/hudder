package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;
import io.github.ngspace.hudder.meta.MethodValue;
import net.minecraft.item.ItemStack;

public class InventoryInformationMethods implements IMethod {
	@Override
	public void invoke(ConfigInfo config, CompileState m, ATextCompiler c, String type, MethodValue... s) throws CompileException {
		ItemStack stack = Hudder.ins.player.getInventory().getStack(s[0].asInt());
		Object value = switch (type) {
			case "name"         ->stack.getName().getString();
			case "durability"   ->stack.getMaxDamage()-stack.getDamage();
			case "maxdurability"->stack.getMaxDamage();
			case "count"        ->stack.getCount();
			case "maxcount"     ->stack.getMaxCount();
			default -> throw new IllegalArgumentException();
		};
		c.put(s[1].asStringSafe(), value);
	}
}
