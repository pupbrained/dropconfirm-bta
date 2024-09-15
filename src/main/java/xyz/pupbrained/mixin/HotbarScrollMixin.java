package xyz.pupbrained.mixin;

import net.minecraft.core.player.inventory.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.pupbrained.DropConfirmUtil;

@Mixin(value = InventoryPlayer.class, remap = false)
public class HotbarScrollMixin {
	@Inject(
		method = "changeCurrentItem(I)V",
		at = @At("HEAD")
	)
	private void onHotbarScroll(int i, CallbackInfo ci) {
		DropConfirmUtil.confirmed = false;
	}
}
