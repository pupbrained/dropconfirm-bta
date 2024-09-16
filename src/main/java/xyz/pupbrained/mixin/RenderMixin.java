package xyz.pupbrained.mixin;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.pupbrained.DropConfirmUtil;

@Mixin(value = Minecraft.class, remap = false)
public class RenderMixin {
	@Inject(
		method = "run",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/WorldRenderer;updateCameraAndRender(F)V",
			shift = At.Shift.AFTER
		)
	)
	private void onRender(CallbackInfo ci) {
		if (!DropConfirmUtil.showConfirmationPrompt) return;

		Minecraft mc = Minecraft.getMinecraft(this);

		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - DropConfirmUtil.promptStartTime;

		float alpha = 1.0f;

		if (elapsedTime > 2000)
			alpha = 1.0f - ((elapsedTime - 2000) / 500.0f);

		// Make sure alpha doesn't go into negatives
		alpha = Math.max(0.0f, alpha);

		// Prevents flashing
		if (alpha <= 0.01f) {
			DropConfirmUtil.showConfirmationPrompt = false;
			return;
		}

		// Convert alpha to a hex value (between 0 and 255)
		int alphaHex = (int) (alpha * 255) << 24;

		// Add it to the RGB value
		int color = alphaHex | 0xFFFFFF;

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND); // Enable blending for alpha transparency
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Standard blend function

		mc.fontRenderer.drawCenteredString(
			"Press Q again to drop this item.",
			mc.resolution.scaledWidth / 2, // Complete center of the screen horizontally
			mc.resolution.scaledHeight - 60, // A little above the hotbar
			color
		);

		GL11.glDisable(GL11.GL_BLEND); // Disable blending after rendering
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
