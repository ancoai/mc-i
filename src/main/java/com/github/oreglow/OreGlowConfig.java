package com.github.oreglow;

import java.util.List;

public final class OreGlowConfig {
    public int lightLevel = 9;
    public boolean respectHigherExistingLight = true;
    public boolean matchConventionalOreTags = true;
    public boolean matchBlockIdHeuristics = true;
    public List<String> additionalOreIds = List.of();
    public List<String> excludedBlockIds = List.of();
    public int nightVisionAuraRadius = 10;

    public int clampedLightLevel() {
        return Math.max(0, Math.min(15, lightLevel));
    }

    public int nightVisionGlowRadius() {
        return Math.max(4, Math.min(16, nightVisionAuraRadius));
    }
}
