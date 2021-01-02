package ee.kasumata.tpsmon.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import ee.kasumata.tpsmon.TickrateCalculator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow public abstract TextRenderer getFontRenderer();

    private static int getColorForTPS(double tps) {
        if (tps == 0.0)
            return 0xFFFFFF; // white - tps = 0 means we have no data yet
        else if (tps >= 17.0)
            return 0x00FF00; // green
        else if (tps >= 12.0)
            return 0xFFFF00; // yellow
        else if (tps >= 8.0)
            return 0xFF7F00; // orange
        else
            return 0xFF0000; // red
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V")
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        double tps = TickrateCalculator.getAverageTPS();
        String tpsString = String.format(Locale.ROOT, "TPS: %.2f", tps);
        TextRenderer textRenderer = this.getFontRenderer();

        int textX = MinecraftClient.getInstance().getWindow().getScaledWidth() - textRenderer.getWidth(tpsString) - 5;
        int textY = MinecraftClient.getInstance().getWindow().getScaledHeight() - textRenderer.fontHeight - 5;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        textRenderer.draw(matrices, tpsString, textX, textY, getColorForTPS(tps) | 0xFF000000);

        RenderSystem.disableBlend();
    }
}
