/*
 * This file is part of the Lithium distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.lithium.mixin;

import org.lithium.Lithium;
import org.lithium.event.events.EventOpenScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V", shift = Shift.BEFORE))
	private void init(RunArgs args, CallbackInfo callback) {
		Lithium.getInstance().postInit();
	}
	

	@Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
	private void setScreen(Screen screen, CallbackInfo info) {
		EventOpenScreen event = new EventOpenScreen(screen);
		Lithium.eventBus.post(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}
}
