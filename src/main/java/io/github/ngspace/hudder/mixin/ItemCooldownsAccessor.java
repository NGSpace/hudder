package io.github.ngspace.hudder.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemCooldowns;

@Mixin(ItemCooldowns.class)
public interface ItemCooldownsAccessor {
	@Accessor Map<Identifier, ItemCooldowns.CooldownInstance> getCooldowns();
	@Accessor int getTickCount();
	
}
