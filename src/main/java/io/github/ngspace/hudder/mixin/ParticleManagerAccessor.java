package io.github.ngspace.hudder.mixin;

import java.util.Map;
import java.util.Queue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
	
	@Accessor("particles")
	public Map<ParticleTextureSheet, Queue<Particle>> getParticles();
	
}
