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
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

public class ConfigSpecValueSpec extends NightConfig {

	public ConfigSpecValueSpec() {
		super("AutoWalk", KEY_UNBOUND, NightConfigCategory.MOVEMENT, "Automatically holds your walk key down.");
	}

	@Override
	public void onDisable(boolean inWorld) {
		mc.options.forwardKey.setPressed(false);

		super.onDisable(inWorld);
	}

	@Subscribe
	public void onTick(EventTick event) {
		mc.options.forwardKey.setPressed(true);
	}
}
