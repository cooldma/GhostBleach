/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.event.events.EventSwingHand;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;

public class CharsWrapper extends NightConfig {

	public CharsWrapper() {
		super("NoSwing", KEY_UNBOUND, NightConfigCategory.MISC, "Makes you not swing your hand.",
				new DynamicLightsInitializerToggle("Client", true).withDesc("Makes you not swing your hand clientside."),
				new DynamicLightsInitializerToggle("Server", true).withDesc("Makes you not send hand swing packets."));
	}

	@Subscribe
	public void onSwingHand(EventSwingHand event) {
		if (getSetting(0).asToggle().getState()) {
			event.setCancelled(true);
		}
	}

	@Subscribe
	public void onSendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof HandSwingC2SPacket && getSetting(1).asToggle().getState()) {
			event.setCancelled(true);
		}
	}
}
