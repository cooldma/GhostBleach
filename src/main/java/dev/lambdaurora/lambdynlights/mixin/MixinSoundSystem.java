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
import dev.lambdaurora.lambdynlights.event.events.EventSoundPlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {

	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
	private void play(SoundInstance soundInstance, CallbackInfo ci) {
		EventSoundPlay.Normal event = new EventSoundPlay.Normal(soundInstance);
		LambDynLights.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
	private void play(SoundInstance soundInstance, int delay, CallbackInfo ci) {
		EventSoundPlay.Normal event = new EventSoundPlay.Normal(soundInstance);
		LambDynLights.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "playNextTick", at = @At("HEAD"), cancellable = true)
	private void playNextTick(TickableSoundInstance sound, CallbackInfo ci) {
		EventSoundPlay.Normal event = new EventSoundPlay.Normal(sound);
		LambDynLights.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "addPreloadedSound", at = @At("HEAD"), cancellable = true)
	private void addPreloadedSound(Sound sound, CallbackInfo ci) {
		EventSoundPlay.Preloaded event = new EventSoundPlay.Preloaded(sound);
		LambDynLights.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}
}
