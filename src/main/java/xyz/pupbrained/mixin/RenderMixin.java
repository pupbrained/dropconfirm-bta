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

		// Calculate alpha value based on elapsed time (fade out over 1000ms)
		float alpha = 1.0f - (elapsedTime / 1000.0f); // Alpha decreases over time
		if (alpha < 0.0f) {
			alpha = 0.0f; // Ensure alpha doesn't go below 0
		}

		// Convert alpha to a hex value (between 0 and 255) and incorporate it into the color
		int alphaHex = (int) (alpha * 255) << 24;
		int color = 0xFFFFFF | alphaHex; // Combine white color with alpha

		// Weird GL stuff
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND); // Enable blending for alpha transparency
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Standard blend function

		mc.fontRenderer.drawCenteredString(
			"Press Q again to drop this item.",
			mc.resolution.scaledWidth / 2, // Complete center of the screen horizontally
			mc.resolution.scaledHeight - 60, // A little above the hotbar
			color // Color with alpha
		);

		// Undo weird GL stuff
		GL11.glDisable(GL11.GL_BLEND); // Disable blending after rendering
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

		// Stop showing the prompt after it has fully faded out
		if (alpha == 0.0f) {
			DropConfirmUtil.showConfirmationPrompt = false;
		}
	}
}
