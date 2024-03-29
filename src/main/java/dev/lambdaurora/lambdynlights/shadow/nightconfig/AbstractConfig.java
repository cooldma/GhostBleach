/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import java.util.ArrayList;
import java.util.List;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AbstractConfig extends NightConfig {

	public List<PlayerMoveC2SPacket> queue = new ArrayList<>();
	public long startTime = 0;

	public AbstractConfig() {
		super("FakeLag", KEY_UNBOUND, NightConfigCategory.MOVEMENT, "Stores up movement packets and makes the server think you're lagging.",
				new DynamicLightsInitializerMode("Mode", "Always", "Pulse").withDesc("Lag mode."),
				new DynamicLightsInitializerToggle("Limit", false).withDesc("Disable FakeLag after x seconds.").withChildren(
						new DynamicLightsInitializerSlider("Limit", 0, 15, 5, 1).withDesc("How many seconds before disabling.")),
				new DynamicLightsInitializerSlider("Pulse", 0, 5, 1, 1).withDesc("Pulse interval."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		startTime = System.currentTimeMillis();
		queue.clear();
	}

	@Override
	public void onDisable(boolean inWorld) {
		if (inWorld)
			sendPackets();

		super.onDisable(inWorld);
	}

	@Subscribe
	public void sendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof PlayerMoveC2SPacket) {
			queue.add((PlayerMoveC2SPacket) event.getPacket());
			event.setCancelled(true);
		}
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (getSetting(0).asMode().getMode() == 0) {
			if (getSetting(1).asToggle().getState() &&
					System.currentTimeMillis() - startTime > getSetting(1).asToggle().getChild(0).asSlider().getValue() * 1000)
				setEnabled(false);
		} else if (getSetting(0).asMode().getMode() == 1) {
			if (System.currentTimeMillis() - startTime > getSetting(2).asSlider().getValue() * 1000) {
				setEnabled(false);
				setEnabled(true);
			}
		}
	}

	public void sendPackets() {
		for (PlayerMoveC2SPacket p : new ArrayList<>(queue)) {
			if (!(p instanceof PlayerMoveC2SPacket.LookAndOnGround)) {
				mc.player.networkHandler.sendPacket(p);
			}
		}

		queue.clear();
	}
}
