/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventReach;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

public class WriteSyncFileConfig extends NightConfig {

	public WriteSyncFileConfig() {
		super("Reach", KEY_UNBOUND, NightConfigCategory.PLAYER, "Turns you into long armed popbob.",
				new DynamicLightsInitializerSlider("Reach", 0, 1, 0.3, 2).withDesc("How much further to be able to reach."));
	}

	@Subscribe
	public void onReach(EventReach event) {
		event.setReach(event.getReach() + getSetting(0).asSlider().getValueFloat());
	}
}
