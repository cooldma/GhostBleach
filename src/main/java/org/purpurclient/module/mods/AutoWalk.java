/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurclient.module.mods;

import org.purpurclient.event.events.EventTick;
import org.purpurclient.eventbus.BleachSubscribe;
import org.purpurclient.module.Module;
import org.purpurclient.module.ModuleCategory;

public class AutoWalk extends Module {

	public AutoWalk() {
		super("AutoWalk", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Automatically holds your walk key down.");
	}

	@Override
	public void onDisable(boolean inWorld) {
		mc.options.forwardKey.setPressed(false);

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		mc.options.forwardKey.setPressed(true);
	}
}
