/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurmc.purpur.client.network.mods;

import org.purpurmc.purpur.client.event.events.EventBlockBreakCooldown;
import org.purpurmc.purpur.client.event.events.EventTick;
import org.purpurmc.purpur.client.eventbus.BleachSubscribe;
import org.purpurmc.purpur.client.network.Module;
import org.purpurmc.purpur.client.network.ModuleCategory;
import org.purpurmc.purpur.client.setting.module.SettingMode;
import org.purpurmc.purpur.client.setting.module.SettingSlider;
import org.purpurmc.purpur.client.setting.module.SettingToggle;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class SpeedMine extends Module {

	public SpeedMine() {
		super("SpeedMine", KEY_UNBOUND, ModuleCategory.EXPLOITS, "Allows you to break blocks faster.",
				new SettingMode("Mode", "Haste", "OG").withDesc("SpeedMine Mode."),
				new SettingSlider("HasteLvl", 1, 3, 1, 0).withDesc("Haste Level."),
				new SettingSlider("Cooldown", 0, 4, 1, 0).withDesc("Cooldown between mining blocks (in ticks)."),
				new SettingSlider("Multiplier", 1, 3, 1.3, 1).withDesc("OG Mode multiplier."),
				new SettingToggle("AntiFatigue", true).withDesc("Removes the fatigue effect."),
				new SettingToggle("AntiOffGround", true).withDesc("Removing mining slowness from being offground."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		if (inWorld)
			mc.player.removeStatusEffect(StatusEffects.HASTE);

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (this.getSetting(0).asMode().getMode() == 0) {
			mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1, getSetting(1).asSlider().getValueInt() - 1));
		}
	}

	@BleachSubscribe
	public void onBlockBreakCooldown(EventBlockBreakCooldown event) {
		event.setCooldown(getSetting(2).asSlider().getValueInt());
	}
}
