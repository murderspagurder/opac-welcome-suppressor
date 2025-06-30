//? if fabric {
package dev.spagurder.ows.loaders.fabric;

import dev.spagurder.ows.OpacWelcomeSuppressor;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer { ;
    @Override
    public void onInitialize() {
        OpacWelcomeSuppressor.init();
    }
}
//?}
