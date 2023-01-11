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
import org.purpurmc.purpur.client.event.events.EventKeyPress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callback) {
		EventKeyPress.InChat event = new EventKeyPress.InChat(keyCode, scanCode, modifiers);
		PurpurClient.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(false);
		}
	}
}
