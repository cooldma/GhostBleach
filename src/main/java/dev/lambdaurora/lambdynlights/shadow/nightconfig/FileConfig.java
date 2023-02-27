/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

public class FileConfig extends NightConfig {

	public FileConfig() {
		super("Sprint", KEY_UNBOUND, NightConfigCategory.MOVEMENT, "Makes the player automatically sprint.",
				new DynamicLightsInitializerToggle("HungerCheck", false).withDesc("Checks that you actually have enough hunger to sprint."));
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (getSetting(0).asToggle().getState() && mc.player.getHungerManager().getFoodLevel() <= 6)
			return;

		mc.player.setSprinting(
				mc.player.input.movementForward > 0 && 
				(mc.player.input.movementSideways != 0 ||mc.player.input.movementForward > 0) &&
				!mc.player.isSneaking());
	}
}
