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
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventOpenScreen;
import dev.lambdaurora.lambdynlights.gui.widget.LightSourceWidget;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import org.lwjgl.glfw.GLFW;

public class ConfigSpecCorrectionListener extends NightConfig {

	public ConfigSpecCorrectionListener() {
		super("ClickGui", GLFW.GLFW_KEY_BACKSLASH, NightConfigCategory.RENDER, "Draws the widget.",
				new DynamicLightsInitializerSlider("Length", 70, 85, 75, 0).withDesc("The length of each window."),
				new DynamicLightsInitializerToggle("Search bar", true).withDesc("Shows a search bar."),
				new DynamicLightsInitializerToggle("Help", true).withDesc("Shows the help text."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		mc.setScreen(LightSourceWidget.INSTANCE);
	}

	@Override
	public void onDisable(boolean inWorld) {
		if (mc.currentScreen instanceof LightSourceWidget)
			mc.setScreen(null);

		super.onDisable(inWorld);
	}

	@Subscribe
	public void onOpenScreen(EventOpenScreen event) {
		if (event.getScreen() == null) {
			setEnabled(false);
		}
	}
}
