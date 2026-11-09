package net.syrupstudios.animaniax.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import com.mojang.math.Axis;
import net.syrupstudios.animaniax.client.ShoulderCarryRenderCache;
import net.syrupstudios.animaniax.entity.ShoulderCarryState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class ShoulderRenderMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void animaniax$renderShoulder(AbstractClientPlayer player, float yaw, float partialTick,
            PoseStack pose, MultiBufferSource buffers, int light, CallbackInfo ci) {
        String type = ShoulderCarryState.visualType(player);
        Entity pet = ShoulderCarryRenderCache.get(player, type);
        if (pet == null || player.isInvisible()) return;
        float bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        float playerYaw = Mth.rotLerp(partialTick, player.yRotO, player.getYRot());
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-bodyYaw));
        pose.translate(player.getBbWidth() * 0.35, player.getBbHeight() * 0.82, 0.0);
        pose.scale(0.55F, 0.55F, 0.55F);
        Minecraft.getInstance().getEntityRenderDispatcher().render(pet, 0, 0, 0, playerYaw - bodyYaw, partialTick, pose, buffers, light);
        pose.popPose();
    }
}
