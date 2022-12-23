/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam.module.mods;

import org.nohurtcam.event.events.EventPacket;
import org.nohurtcam.event.events.EventSwingHand;
import org.nohurtcam.eventbus.BleachSubscribe;
import org.nohurtcam.module.Module;
import org.nohurtcam.module.ModuleCategory;
import org.nohurtcam.setting.module.SettingToggle;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;

public class NoSwing extends Module {

	public NoSwing() {
		super("NoSwing", KEY_UNBOUND, ModuleCategory.MISC, "Makes you not swing your hand.",
				new SettingToggle("Client", true).withDesc("Makes you not swing your hand clientside."),
				new SettingToggle("Server", true).withDesc("Makes you not send hand swing packets."));
	}

	@BleachSubscribe
	public void onSwingHand(EventSwingHand event) {
		if (getSetting(0).asToggle().getState()) {
			event.setCancelled(true);
		}
	}

	@BleachSubscribe
	public void onSendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof HandSwingC2SPacket && getSetting(1).asToggle().getState()) {
			event.setCancelled(true);
		}
	}
}
