
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
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

public class AbstractConvertedConfig extends NightConfig {

	public int prevFov;
	public double prevSens;

	public AbstractConvertedConfig() {
		super("Zoom", KEY_UNBOUND, NightConfigCategory.RENDER, "ok zoomer.",
				new DynamicLightsInitializerSlider("Scale", 1, 10, 3, 2).withDesc("How much to zoom."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		prevFov = mc.options.getFov().getValue();
		prevSens = mc.options.getMouseSensitivity().getValue();

		mc.options.getFov().setValue((int) (prevFov / getSetting(0).asSlider().getValue()));
		mc.options.getMouseSensitivity().setValue(prevSens / getSetting(0).asSlider().getValue());
	}

	@Override
	public void onDisable(boolean inWorld) {
		mc.options.getFov().setValue(prevFov);
		mc.options.getMouseSensitivity().setValue(prevSens);

		super.onDisable(inWorld);
	}
}
