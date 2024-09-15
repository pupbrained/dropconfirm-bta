package xyz.pupbrained;

import net.minecraft.core.entity.player.EntityPlayer;

public final class DropConfirmUtil {
	public static boolean confirmed = false;
	public static boolean showConfirmationPrompt = false;
	public static long promptStartTime = 0;

	public static boolean isMainHandStackEmpty(EntityPlayer player) {
		return player.getHeldItem()	== null;
	}
}
