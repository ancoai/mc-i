package com.github.oreglow.mixin;

import com.github.oreglow.OreGlowClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void oreglow$highlightNightVisionOres(CallbackInfo ci) {
        OreGlowClient.tick((MinecraftClient) (Object) this);
    }
}
