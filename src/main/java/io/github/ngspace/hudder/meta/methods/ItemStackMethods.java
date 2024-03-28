package io.github.ngspace.hudder.meta.methods;

import static io.github.ngspace.hudder.Hudder.player;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.ItemElement;
import io.github.ngspace.hudder.meta.Meta;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemStackMethods extends AMethod {
	@Override
	public void execute(ConfigInfo ci, Meta meta, ATextCompiler comp, String... args) throws CompileException {
		String type = "hand";
		try {
			type = args[0].toLowerCase();
			int offset = "slot".equals(type)||"item".equals(type) ? 1:0;
			double x = tryParse(comp.getVariable(args[1+offset]));
			double y = tryParse(comp.getVariable(args[2+offset]));
			float scale = (float)(args.length>3+offset?tryParse(comp.getVariable(args[3+offset])):ci.scale);
			boolean showcount = args.length>4+offset ?
					Boolean.valueOf(String.valueOf(comp.getVariable(args[4+offset]))) : true;
			
			var inv = player.getInventory();
			ItemStack stack = switch (type) {
				case "hand","selectedslot": yield inv.getStack(inv.selectedSlot);
				case "helmet", "hat": yield inv.getArmorStack(3);
				case "chestplate": yield inv.getArmorStack(2);
				case "pants", "leggings": yield inv.getArmorStack(1);
				case "boots": yield inv.getArmorStack(0);
				case "offhand": yield inv.offHand.get(0);
				case "slot": yield inv.getStack(tryParseInt(comp.getVariable(args[1])));
				case "item": yield new ItemStack(Registries.ITEM.get(Identifier.tryParse(args[1])));
				default: throw new IllegalArgumentException("Unexpected value: " + type);
			};
			meta.elements.add(new ItemElement(x, y, stack, scale, showcount));
		} catch (Exception e) {
			throw new CompileException("\""+type+"\" only accepts \""+type
					+("slot".equals(type)?",[slot]":"")
					+("item".equals(type)?",[item]":"")
					+",[x],[y],<scale>,<show count>\"");
		}
	}
}
