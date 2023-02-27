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
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.event.events.EventBlockBreakCooldown;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FileConfigBuilder extends NightConfig {

	public FileConfigBuilder() {
		super("SpeedMine", KEY_UNBOUND, NightConfigCategory.EXPLOITS, "Allows you to break blocks faster.",
				new DynamicLightsInitializerMode("Mode", "Haste", "OG").withDesc("SpeedMine Mode."),
				new DynamicLightsInitializerSlider("HasteLvl", 1, 3, 1, 0).withDesc("Haste Level."),
				new DynamicLightsInitializerSlider("Cooldown", 0, 4, 1, 0).withDesc("Cooldown between mining blocks (in ticks)."),
				new DynamicLightsInitializerSlider("Multiplier", 1, 3, 1.3, 1).withDesc("OG Mode multiplier."),
				new DynamicLightsInitializerToggle("AntiFatigue", true).withDesc("Removes the fatigue effect."),
				new DynamicLightsInitializerToggle("AntiOffGround", true).withDesc("Removing mining slowness from being offground."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		if (inWorld)
			mc.player.removeStatusEffect(StatusEffects.HASTE);

		super.onDisable(inWorld);
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (this.getSetting(0).asMode().getMode() == 0) {
			mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1, getSetting(1).asSlider().getValueInt() - 1));
		}
	}

	@Subscribe
	public void onBlockBreakCooldown(EventBlockBreakCooldown event) {
		event.setCooldown(getSetting(2).asSlider().getValueInt());
	}
}
