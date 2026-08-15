package dev.ngspace.hudder.tests_mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {
    @Accessor("allMessages")
    public List<GuiMessage> getMessages();
}