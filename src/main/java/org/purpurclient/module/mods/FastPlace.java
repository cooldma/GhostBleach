/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurclient.module.mods;

import java.util.Set;

import org.purpurclient.event.events.EventTick;
import org.purpurclient.eventbus.BleachSubscribe;
import org.purpurclient.module.Module;
import org.purpurclient.module.ModuleCategory;
import org.purpurclient.setting.module.SettingMode;
import org.purpurclient.setting.module.SettingToggle;

import com.google.common.collect.Sets;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class FastPlace extends Module {

	private static final Set<Item> THROWABLE = Sets.newHashSet(
			Items.SNOWBALL, Items.EGG, Items.EXPERIENCE_BOTTLE,
			Items.ENDER_EYE, Items.ENDER_PEARL, Items.SPLASH_POTION, Items.LINGERING_POTION);

	public FastPlace() {
		super("FastPlace", KEY_UNBOUND, ModuleCategory.PLAYER, "Place blocks faster (modified from FastUse)",
				new SettingMode("Mode", "Single").withDesc("Whether to throw once per tick or multiple times."),
				new SettingToggle("Throwables Only", true).withDesc("Only uses throwables.").withChildren(
						new SettingToggle("XP Only", false).withDesc("Only uses XP bottles.")));
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (getSetting(2).asToggle().getState()) {
			if (!(THROWABLE.contains(mc.player.getMainHandStack().getItem())
					&& (!getSetting(2).asToggle().getChild(0).asToggle().getState() 
							|| mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE))) {
				return;
			}
		}

		mc.itemUseCooldown = 0;
		if (getSetting(0).asMode().getMode() == 1 && mc.options.useKey.isPressed()) {
			for (int i = 0; i < getSetting(1).asSlider().getValueInt(); i++) {
				mc.doItemUse();
			}
		}
	}
}
