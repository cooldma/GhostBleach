/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam.module.mods;

import org.nohurtcam.event.events.EventReach;
import org.nohurtcam.eventbus.BleachSubscribe;
import org.nohurtcam.module.Module;
import org.nohurtcam.module.ModuleCategory;
import org.nohurtcam.setting.module.SettingSlider;

public class Reach extends Module {

	public Reach() {
		super("Reach", KEY_UNBOUND, ModuleCategory.PLAYER, "Turns you into long armed popbob.",
				new SettingSlider("Reach", 0, 1, 0.3, 2).withDesc("How much further to be able to reach."));
	}

	@BleachSubscribe
	public void onReach(EventReach event) {
		event.setReach(event.getReach() + getSetting(0).asSlider().getValueFloat());
	}
}
