package xyz.pupbrained.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.pupbrained.DropConfirm;
import xyz.pupbrained.DropConfirmUtil;

@Mixin(value = EntityPlayer.class, remap = false)
public class ItemDropMixin {
	Minecraft mc = Minecraft.getMinecraft(this);

	@Inject(
		method="dropCurrentItem(Z)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onItemDrop(boolean dropFullStack, CallbackInfo ci) {
		if (DropConfirmUtil.isMainHandStackEmpty(mc.thePlayer))
			return;

		if (!DropConfirmUtil.confirmed) {
			DropConfirmUtil.confirmed = true;
			DropConfirmUtil.showConfirmationPrompt = true;
			DropConfirmUtil.promptStartTime = System.currentTimeMillis(); // Set start time here

			new Thread(() -> {
				try {
					Thread.sleep(1000);

					synchronized (DropConfirmUtil.class) {
						DropConfirmUtil.confirmed = false;
						DropConfirmUtil.showConfirmationPrompt = false;
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					DropConfirm.LOGGER.error("Interrupted while waiting to reset confirmation.", e);
				}
			}).start();

			ci.cancel();
		} else {
			DropConfirmUtil.confirmed = false;
			DropConfirmUtil.showConfirmationPrompt = false;

			mc.theWorld.playSoundAtEntity(mc.thePlayer, mc.thePlayer, "random.pop", 1.0F, 1.0F);
		}
	}
}
