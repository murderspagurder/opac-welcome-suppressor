package com.github.murderspagurder.ows.fabric;

import net.fabricmc.api.ModInitializer;

import com.github.murderspagurder.ows.OpacWelcomeSuppressor;

public final class OpacWelcomeSuppressorFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OpacWelcomeSuppressor.init();
    }
}
