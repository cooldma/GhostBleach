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
import dev.lambdaurora.lambdynlights.shadow.nightconfig.ConfigWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.BossBarHud;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(CallbackInfo info) {
		if (NightConfigManager.getModule(ConfigWriter.class).isOverlayToggled(6)) {
			info.cancel();
		}
	}

}
