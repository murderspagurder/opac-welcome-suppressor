package com.github.murderspagurder.ows.neoforge;

import net.neoforged.fml.common.Mod;

import com.github.murderspagurder.ows.OpacWelcomeSuppressor;

@Mod(OpacWelcomeSuppressor.MOD_ID)
public final class OpacWelcomeSuppressorNeoForge {
    public OpacWelcomeSuppressorNeoForge() {
        OpacWelcomeSuppressor.init();
    }
}
