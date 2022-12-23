/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam.mixin;

import org.nohurtcam.NoHurtCam;
//import org.bleachhack.command.Command;
import org.nohurtcam.event.events.EventKeyPress;
import org.nohurtcam.module.ModuleManager;
import org.nohurtcam.setting.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class MixinKeyboard {

	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	private void onKeyEvent(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo callbackInfo) {
		if (key >= 0) {
			EventKeyPress.Global event = new EventKeyPress.Global(key, scanCode, action, modifiers);
			NoHurtCam.eventBus.post(event);

			if (event.isCancelled()) {
				callbackInfo.cancel();
			}
		}
	}
	
	@Inject(method = "onKey", at = @At(value = "INVOKE", target = "net/minecraft/client/util/InputUtil.isKeyPressed(JI)Z", ordinal = 5), cancellable = true)
	private void onKeyEvent_1(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo callbackInfo) {
		if (Option.CHAT_QUICK_PREFIX.getValue()) {
//			MinecraftClient.getInstance().setScreen(new ChatScreen(Command.getPrefix()));
		}

		ModuleManager.handleKey(key);

		if (key >= 0) {
			EventKeyPress.InWorld event = new EventKeyPress.InWorld(key, scanCode);
			NoHurtCam.eventBus.post(event);

			if (event.isCancelled()) {
				callbackInfo.cancel();
			}
		}
	}
}
