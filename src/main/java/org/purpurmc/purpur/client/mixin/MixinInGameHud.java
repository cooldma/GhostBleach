/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurmc.purpur.client.mixin;

import org.purpurmc.purpur.client.PurpurClient;
import org.purpurmc.purpur.client.event.events.EventRenderCrosshair;
import org.purpurmc.purpur.client.event.events.EventRenderInGameHud;
import org.purpurmc.purpur.client.event.events.EventRenderOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
public class MixinInGameHud {

	@Unique private boolean bypassRenderOverlay = false;
	@Unique private boolean bypassRenderCrosshair = false;

	@Shadow private void renderOverlay(Identifier texture, float opacity) {}
	@Shadow private void renderCrosshair(MatrixStack matrices) {}

	@Inject(method = "render", at = @At("RETURN"), cancellable = true)
	private void render(MatrixStack matrixStack, float tickDelta, CallbackInfo info) {
		EventRenderInGameHud event = new EventRenderInGameHud(matrixStack);
		PurpurClient.eventBus.post(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}

	@Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
	private void renderOverlay(Identifier texture, float opacity, CallbackInfo ci) {
		if (!bypassRenderOverlay) {
			EventRenderOverlay event = new EventRenderOverlay(texture, opacity);
			PurpurClient.eventBus.post(event);

			if (!event.isCancelled()) {
				bypassRenderOverlay = true;
				renderOverlay(event.getTexture(), event.getOpacity());
				bypassRenderOverlay = false;
			}

			ci.cancel();
		}
	}


	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void renderCrosshair(MatrixStack matrices, CallbackInfo callback) {
		if (!bypassRenderCrosshair) {
			EventRenderCrosshair event = new EventRenderCrosshair(matrices);
			PurpurClient.eventBus.post(event);

			if (!event.isCancelled()) {
				bypassRenderCrosshair = true;
				renderCrosshair(event.getMatrices());
				bypassRenderCrosshair = false;
			}

			callback.cancel();
		}
	}
}
