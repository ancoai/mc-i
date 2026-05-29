package com.github.oreglow;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.entity.effect.StatusEffects;
import org.joml.Vector3f;

import java.util.Locale;

public final class OreGlowClient {
    private static final int SCAN_INTERVAL_TICKS = 5;
    private static final int MAX_PARTICLES_PER_SCAN = 34;
    private static final double PARTICLE_CHANCE = 0.24D;
    private static final double SPARKLE_CHANCE = 0.16D;
    private static final DustParticleEffect DEFAULT_GLOW = new DustParticleEffect(new Vector3f(0.55F, 0.85F, 1.0F), 0.72F);
    private static final DustParticleEffect DIAMOND_GLOW = new DustParticleEffect(new Vector3f(0.20F, 0.95F, 1.0F), 0.82F);
    private static final DustParticleEffect EMERALD_GLOW = new DustParticleEffect(new Vector3f(0.10F, 1.0F, 0.34F), 0.82F);
    private static final DustParticleEffect REDSTONE_GLOW = new DustParticleEffect(new Vector3f(1.0F, 0.13F, 0.08F), 0.84F);
    private static final DustParticleEffect LAPIS_GLOW = new DustParticleEffect(new Vector3f(0.18F, 0.35F, 1.0F), 0.78F);
    private static final DustParticleEffect GOLD_GLOW = new DustParticleEffect(new Vector3f(1.0F, 0.74F, 0.12F), 0.86F);
    private static final DustParticleEffect COPPER_GLOW = new DustParticleEffect(new Vector3f(1.0F, 0.42F, 0.18F), 0.80F);
    private static final DustParticleEffect IRON_GLOW = new DustParticleEffect(new Vector3f(0.92F, 0.74F, 0.55F), 0.76F);
    private static final DustParticleEffect COAL_GLOW = new DustParticleEffect(new Vector3f(0.38F, 0.38F, 0.42F), 0.68F);
    private static final DustParticleEffect QUARTZ_GLOW = new DustParticleEffect(new Vector3f(1.0F, 0.92F, 0.78F), 0.76F);
    private static final DustParticleEffect DEBRIS_GLOW = new DustParticleEffect(new Vector3f(0.58F, 0.24F, 0.86F), 0.88F);

    private OreGlowClient() {
    }

    public static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        if (player == null || world == null || !player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            return;
        }

        if (world.getTime() % SCAN_INTERVAL_TICKS != 0) {
            return;
        }

        Random random = world.random;
        BlockPos center = player.getBlockPos();
        int radius = OreGlowMod.config().nightVisionGlowRadius();
        int particles = 0;

        for (BlockPos pos : BlockPos.iterateOutwards(center, radius, radius, radius)) {
            if (particles >= MAX_PARTICLES_PER_SCAN) {
                return;
            }

            if (center.getSquaredDistance(pos) > radius * radius || random.nextDouble() > PARTICLE_CHANCE) {
                continue;
            }

            BlockState state = world.getBlockState(pos);
            if (!OreGlowMatcher.shouldGlow(state)) {
                continue;
            }

            spawnOreAura(world, pos, state, random);
            particles++;
        }
    }

    private static void spawnOreAura(ClientWorld world, BlockPos pos, BlockState state, Random random) {
        Direction face = randomExposedFace(world, pos, random);
        if (face == null) {
            return;
        }

        DustParticleEffect glow = glowColorFor(state);
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + random.nextDouble();
        double z = pos.getZ() + random.nextDouble();
        double inset = 0.035D;

        switch (face) {
            case DOWN -> y = pos.getY() - inset;
            case UP -> y = pos.getY() + 1.0D + inset;
            case NORTH -> z = pos.getZ() - inset;
            case SOUTH -> z = pos.getZ() + 1.0D + inset;
            case WEST -> x = pos.getX() - inset;
            case EAST -> x = pos.getX() + 1.0D + inset;
        }

        double velocityY = 0.006D + random.nextDouble() * 0.012D;
        world.addParticle(glow, x, y, z, 0.0D, velocityY, 0.0D);
        if (random.nextDouble() < SPARKLE_CHANCE) {
            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0D, 0.012D, 0.0D);
        }
    }

    private static Direction randomExposedFace(ClientWorld world, BlockPos pos, Random random) {
        Direction selected = null;
        int exposedFaces = 0;

        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighbor);
            if (!neighborState.isOpaqueFullCube(world, neighbor)) {
                exposedFaces++;
                if (random.nextInt(exposedFaces) == 0) {
                    selected = direction;
                }
            }
        }

        return selected;
    }

    private static DustParticleEffect glowColorFor(BlockState state) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        String path = id.getPath().toLowerCase(Locale.ROOT);

        if (path.contains("diamond")) {
            return DIAMOND_GLOW;
        }
        if (path.contains("emerald")) {
            return EMERALD_GLOW;
        }
        if (path.contains("redstone")) {
            return REDSTONE_GLOW;
        }
        if (path.contains("lapis")) {
            return LAPIS_GLOW;
        }
        if (path.contains("gold")) {
            return GOLD_GLOW;
        }
        if (path.contains("copper")) {
            return COPPER_GLOW;
        }
        if (path.contains("iron")) {
            return IRON_GLOW;
        }
        if (path.contains("coal")) {
            return COAL_GLOW;
        }
        if (path.contains("quartz")) {
            return QUARTZ_GLOW;
        }
        if (path.contains("debris")) {
            return DEBRIS_GLOW;
        }

        return DEFAULT_GLOW;
    }
}
