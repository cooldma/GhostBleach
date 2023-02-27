/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.mixin;

import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.shadow.nightconfig.CheckedCommentedFileConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.RenderTickCounter;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {

	@Shadow private float lastFrameDuration;
	@Shadow private float tickDelta;
	@Shadow private long prevTimeMillis;
	@Shadow private float tickTime;

	@Inject(method = "beginRenderTick", at = @At("HEAD"), cancellable = true)
	private void beginRenderTick(long timeMillis, CallbackInfoReturnable<Integer> ci) {
		if (NightConfigManager.getModule(CheckedCommentedFileConfig.class).isEnabled()) {
			this.lastFrameDuration = (float) (((timeMillis - this.prevTimeMillis) / this.tickTime)
					* NightConfigManager.getModule(CheckedCommentedFileConfig.class).getSetting(0).asSlider().getValue());
			this.prevTimeMillis = timeMillis;
			this.tickDelta += this.lastFrameDuration;
			int i = (int) this.tickDelta;
			this.tickDelta -= i;

			ci.setReturnValue(i);
		}
	}

}
