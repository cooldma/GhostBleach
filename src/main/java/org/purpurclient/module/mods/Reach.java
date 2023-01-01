/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurclient.module.mods;

import org.purpurclient.event.events.EventReach;
import org.purpurclient.eventbus.BleachSubscribe;
import org.purpurclient.module.Module;
import org.purpurclient.module.ModuleCategory;
import org.purpurclient.setting.module.SettingSlider;

public class Reach extends Module {

	public Reach() {
		super("Reach", KEY_UNBOUND, ModuleCategory.COMBAT, "Turns you into long armed popbob.",
				new SettingSlider("Reach", 0, 2, 0.3, 2).withDesc("How much further to be able to reach."));
	}

	@BleachSubscribe
	public void onReach(EventReach event) {
		event.setReach(event.getReach() + getSetting(0).asSlider().getValueFloat());
	}
}
