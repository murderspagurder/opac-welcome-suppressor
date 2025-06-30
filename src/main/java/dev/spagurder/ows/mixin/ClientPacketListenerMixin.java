package dev.spagurder.ows.mixin;

import dev.spagurder.ows.OpacWelcomeSuppressor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "setActionBarText", at = @At("HEAD"), cancellable = true)
    private void interceptActionBar(ClientboundSetActionBarTextPacket packet, CallbackInfo ci) {
        //? >1.20.4 {
        String msg = packet.text().getString();
        //?} else {
        /*String msg = packet.getText().getString();
        *///?}
        if (msg.startsWith("□ ") && msg.endsWith(" □")) {
            OpacWelcomeSuppressor.LOGGER.info("Suppressing OPAC Title: {}", msg);
            ci.cancel();
        }
    }

}