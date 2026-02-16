package dev.ngspace.hudder.utils;

import java.io.InputStream;

import net.minecraft.resources.Identifier;

public record TextureSource(Identifier id, InputStream io) {}