package dev.ngspace.hudder.variables.advanced;

import dev.ngspace.hudder.api.variableregistry.DataVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectData implements DataVariable<Object> {
    @Override
    public Object getValue0(String key) {
        return Minecraft.getInstance().player.getActiveEffects().stream()
                .map(Effect::new)
                .toList();
    }
    public static class Effect {
        public final String id;
        public final String name;
        public final int amplifier;
        public final int duration;
        public final boolean ambient;
        public final boolean visible;
        public final boolean beneficial;

        public Effect(MobEffectInstance e) {
            this.id = String.valueOf(BuiltInRegistries.MOB_EFFECT.getKey(e.getEffect().value()));
            this.name = e.getEffect().value().getDisplayName().getString();
            this.amplifier = e.getAmplifier();
            this.duration = e.getDuration();
            this.ambient = e.isAmbient();
            this.visible = e.isVisible();
            this.beneficial = e.getEffect().value().isBeneficial();
        }
        @Override
        public String toString() {
            return String.format("{id: \"%s\", name: %s, amplifier: %d, duration: %d, ambient: %b, visible: %b, beneficial: %b}",
                    id, name, amplifier, duration, ambient, visible, beneficial);
        }
    }
}
