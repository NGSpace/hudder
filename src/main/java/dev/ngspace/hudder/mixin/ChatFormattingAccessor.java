package dev.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.ChatFormatting;

@Mixin(ChatFormatting.class)
public interface ChatFormattingAccessor {
	@Accessor("code") public char code();
}
