package ee.kasumata.tpsmon.mixin;

import ee.kasumata.tpsmon.TickrateCalculator;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(at = @At("HEAD"), method = "handlePacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;)V")
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        // this packet should be received exactly every 1 second, any deviation from that indicates that the server is lagging behind
        if (packet.getClass() == WorldTimeUpdateS2CPacket.class) {
            TickrateCalculator.reportReceivedPacket();

            System.out.println(String.format(
                    Locale.ROOT, "%.2f | %.2f", TickrateCalculator.getCurrentTPS(), TickrateCalculator.getAverageTPS()));
        }
    }
}
