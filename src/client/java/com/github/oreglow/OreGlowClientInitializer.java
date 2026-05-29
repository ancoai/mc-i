package com.github.oreglow;

import net.fabricmc.api.ClientModInitializer;

public final class OreGlowClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OreGlowMod.LOGGER.info("Ore Glow night-vision ore aura enabled on the client.");
    }
}
