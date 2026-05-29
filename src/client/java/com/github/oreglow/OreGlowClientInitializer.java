package com.github.oreglow;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public final class OreGlowClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OreGlowClient.start(MinecraftClient.getInstance());
        OreGlowMod.LOGGER.info("Ore Glow night-vision ore aura enabled on the client.");
    }
}
