package io.github.ngspace.hudder.meta.methods;

import static io.github.ngspace.hudder.Hudder.ins;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler.Value;
import io.github.ngspace.hudder.meta.elements.ItemElement;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemStackMethods implements IMethod {
	@Override
	public void invoke(ConfigInfo ci, Meta meta, ATextCompiler comp, String type, Value... args) throws CompileException {
		int offset = "slot".equals(type)||"item".equals(type) ? 1:0;
		if (args.length<2+offset) {
			throw new CompileException("\""+type+"\" only accepts \""+type
				+("slot".equals(type)?",[slot]":"")
				+("item".equals(type)?",[item]":"")
				+",[x],[y],<scale>,<show count>\"");
		}
		double x = args[0+offset].asDouble();
		double y = args[1+offset].asDouble();
		float scale = (float)(args.length>2+offset?args[2+offset].asDouble():ci.scale);
		
		boolean showcount = args.length<=3+offset || args[3+offset].asBoolean();
		
		PlayerInventory inv = ins.player.getInventory();
		ItemStack stack = switch (type) {
			case "hand","selectedslot": yield inv.getStack(inv.selectedSlot);
			case "helmet", "hat": yield inv.getArmorStack(3);
			case "chestplate": yield inv.getArmorStack(2);
			case "pants", "leggings": yield inv.getArmorStack(1);
			case "boots": yield inv.getArmorStack(0);
			case "offhand": yield inv.offHand.get(0);
			case "slot": yield inv.getStack(args[0].asInt());
			case "item": yield new ItemStack(Registries.ITEM.get(Identifier.tryParse(args[1].asString())));
			default: throw new IllegalArgumentException("Unexpected value: " + type);
		};
		meta.elements.add(new ItemElement(x, y, stack, scale, showcount));
	}
}