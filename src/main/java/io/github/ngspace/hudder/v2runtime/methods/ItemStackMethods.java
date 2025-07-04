package io.github.ngspace.hudder.v2runtime.methods;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.uielements.ItemElement;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ItemStackMethods implements IMethod {
	
	protected static Minecraft mc = Minecraft.getInstance();
	@Override
	public void invoke(HudderConfig ci, CompileState meta, ATextCompiler comp, String type, int line, int charpos, ObjectWrapper... args) throws CompileException {
		int offset = "slot".equals(type)||"item".equals(type) ? 1:0;
		if (args.length<2+offset) {
			throw new CompileException("\""+type+"\" only accepts ;"+type
				+("slot".equals(type)?",[slot]":"")
				+("item".equals(type)?",[item]":"")
				+",[x],[y],<scale>,<show count>;", line, charpos);
		}
		double x = args[0+offset].asDouble();
		double y = args[1+offset].asDouble();
		float scale = (float)(args.length>2+offset?args[2+offset].asDouble():ci.scale);

		boolean showcount = args.length<=3+offset || args[3+offset].asBoolean();
		
		Inventory inv = mc.player.getInventory();
		
		ItemStack stack = switch (type) {
			case "hand","selectedslot": yield inv.getItem(inv.getSelectedSlot());
			case "helmet", "hat": yield inv.getItem(39);
			case "chestplate": yield inv.getItem(38);
			case "pants", "leggings": yield inv.getItem(37);
			case "boots": yield inv.getItem(36);
			case "offhand": yield mc.player.getOffhandItem();
			case "slot": yield inv.getItem(args[0].asInt());
			case "item": yield new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.tryParse(args[0].asString())));
			default: throw new IllegalArgumentException("Unexpected value: " + type);
		};
		meta.elements.add(new ItemElement(x, y, stack, scale, showcount));
	}
}