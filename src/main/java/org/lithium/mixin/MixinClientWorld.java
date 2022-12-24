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
import org.lithium.event.events.EventSkyRender;
import org.lithium.event.events.EventTick;
import org.lithium.util.BleachQueue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

	@Shadow @Final private DimensionEffects dimensionEffects;

	@Inject(method = "tickEntities", at = @At("HEAD"), cancellable = true)
	private void tickEntities(CallbackInfo info) {
		BleachQueue.nextQueue();

		EventTick event = new EventTick();
		Lithium.eventBus.post(event);
		if (event.isCancelled())
			info.cancel();
	}

	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	public void getSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> ci) {
		EventSkyRender.Color.SkyColor event = new EventSkyRender.Color.SkyColor(tickDelta);
		Lithium.eventBus.post(event);

		if (event.isCancelled()) {
			ci.setReturnValue(Vec3d.ZERO);
		} else if (event.getColor() != null) {
			ci.setReturnValue(event.getColor());
		}
	}

	@Inject(method = "getCloudsColor", at = @At("HEAD"), cancellable = true)
	private void getCloudsColor(float f, CallbackInfoReturnable<Vec3d> ci) {
		EventSkyRender.Color.CloudColor event = new EventSkyRender.Color.CloudColor(f);
		Lithium.eventBus.post(event);

		if (event.isCancelled()) {
			ci.setReturnValue(Vec3d.ZERO);
		} else if (event.getColor() != null) {
			ci.setReturnValue(event.getColor());
		}
	}

	@Overwrite
	public DimensionEffects getDimensionEffects() {
		if (MinecraftClient.getInstance().world == null) {
			return dimensionEffects;
		}

		EventSkyRender.Properties event = new EventSkyRender.Properties(dimensionEffects);
		Lithium.eventBus.post(event);

		return event.getSky();
	}
}
