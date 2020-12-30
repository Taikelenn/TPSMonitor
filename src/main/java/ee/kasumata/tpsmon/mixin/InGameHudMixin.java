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

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V")
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        double tps = TickrateCalculator.getAverageTPS();
        String tpsString = String.format(Locale.ROOT, "TPS: %.2f", tps);
        TextRenderer textRenderer = this.getFontRenderer();

        int textX = MinecraftClient.getInstance().getWindow().getScaledWidth() - textRenderer.getWidth(tpsString) - 5;
        int textY = MinecraftClient.getInstance().getWindow().getScaledHeight() - textRenderer.fontHeight - 5;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int color;
        if (tps >= 17.0)
            color = 0x00FF00; // green
        else if (tps >= 12.0)
            color = 0xFFFF00; // yellow
        else if (tps >= 8.0)
            color = 0xFF7F00; // orange
        else
            color = 0xFF0000; // red

        textRenderer.draw(matrices, tpsString, textX, textY, color | 0xFF000000);

        RenderSystem.disableBlend();
    }
}
