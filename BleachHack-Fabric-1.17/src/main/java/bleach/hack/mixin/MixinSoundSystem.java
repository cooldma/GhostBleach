package bleach.hack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import bleach.hack.BleachHack;
import bleach.hack.event.events.EventSoundPlay;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {

	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
	public void play(SoundInstance soundInstance_1, CallbackInfo ci) {
		EventSoundPlay.Normal event = new EventSoundPlay.Normal(soundInstance_1);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
	public void play(SoundInstance soundInstance_1, int i, CallbackInfo ci) {
		EventSoundPlay.Normal event = new EventSoundPlay.Normal(soundInstance_1);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "playNextTick", at = @At("HEAD"), cancellable = true)
	public void playNextTick(TickableSoundInstance soundInstance_1, CallbackInfo ci) {
		EventSoundPlay.Normal event = new EventSoundPlay.Normal(soundInstance_1);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "addPreloadedSound", at = @At("HEAD"), cancellable = true)
	public void addPreloadedSound(Sound sound_1, CallbackInfo ci) {
		EventSoundPlay.Preloaded event = new EventSoundPlay.Preloaded(sound_1);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}
}
