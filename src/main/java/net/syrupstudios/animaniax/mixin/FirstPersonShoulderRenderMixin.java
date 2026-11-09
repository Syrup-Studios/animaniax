package net.syrupstudios.animaniax.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.CameraType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.syrupstudios.animaniax.client.ShoulderCarryRenderCache;
import net.syrupstudios.animaniax.entity.ShoulderCarryState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class FirstPersonShoulderRenderMixin {
    @Inject(method = "renderHandsWithItems", at = @At("TAIL"))
    private void animaniax$renderShoulder(float partialTick, PoseStack pose,
            MultiBufferSource.BufferSource buffers, LocalPlayer player, int light, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (player.isInvisible() || minecraft.options.getCameraType() != CameraType.FIRST_PERSON) return;
        String type = ShoulderCarryState.visualType(player);
        Entity pet = ShoulderCarryRenderCache.get(player, type);
        if (pet == null) return;
        pose.pushPose();
        pose.translate(-0.48, -0.28, -0.72);
        pose.scale(0.35F, 0.35F, 0.35F);
        minecraft.getEntityRenderDispatcher().render(pet, 0, 0, 0, 0, partialTick, pose, buffers, light);
        pose.popPose();
        buffers.endBatch();
    }
}
