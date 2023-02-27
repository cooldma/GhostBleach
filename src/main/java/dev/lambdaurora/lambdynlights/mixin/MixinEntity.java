/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.mixin;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.shadow.nightconfig.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import dev.lambdaurora.lambdynlights.event.events.EventPlayerPushed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(method = "getTa rgetingMargin", at = @At("HEAD"), cancellable = true)
	private void onGetTargetingMargin(CallbackInfoReturnable<Float> info) {
		double v = NightConfigManager.getModule(Utils.class).getSetting(0).asSlider().getValue();
		if(!NightConfigManager.getModule(Utils.class).isEnabled()) return;
		info.setReturnValue((float) v);
	}

	@ModifyArgs(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
	private void pushAwayFrom_addVelocity(Args args) {
		if ((Object) this == MinecraftClient.getInstance().player) {
			EventPlayerPushed event = new EventPlayerPushed(args.get(0), args.get(1), args.get(2));
			LambDynLights.eventBus.post(event);

			args.set(0, event.getPushX());
			args.set(1, event.getPushY());
			args.set(2, event.getPushZ());
		}
	}
}
