package io.github.ngspace.hudder.data_management;

import java.util.Objects;

import io.github.ngspace.hudder.data_management.api.DataVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectData implements DataVariable<Object> {
    @Override
    public Object getValue0(String key) {
        var ins = Minecraft.getInstance();
        if (Objects.equals(key, "active_effects")) {
            return ins.player.getActiveEffects().stream()
                    .map(Effect::new)
                    .toList();
        }
        return null;
    }
    public static class Effect {
        public final String id;
        public final int amplifier;
        public final int duration;
        public final boolean ambient;
        public final boolean visible;
        public final boolean beneficial;

        public Effect(MobEffectInstance e) {
            this.id = BuiltInRegistries.MOB_EFFECT.getKey(e.getEffect().value()).toString();
            this.amplifier = e.getAmplifier();
            this.duration = e.getDuration();
            this.ambient = e.isAmbient();
            this.visible = e.isVisible();
            this.beneficial = e.getEffect().value().isBeneficial();
        }
        @Override
        public String toString() {
            return String.format("{id: \"%s\", amplifier: %d, duration: %d, ambient: %b, visible: %b, beneficial: %b}",
                    id, amplifier, duration, ambient, visible, beneficial);
        }
    }
}
