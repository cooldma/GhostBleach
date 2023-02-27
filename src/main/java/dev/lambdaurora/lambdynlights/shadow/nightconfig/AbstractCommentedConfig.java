/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import java.util.Set;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import com.google.common.collect.Sets;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class AbstractCommentedConfig extends NightConfig {

	private static final Set<Item> THROWABLE = Sets.newHashSet(
			Items.SNOWBALL, Items.EGG, Items.EXPERIENCE_BOTTLE,
			Items.ENDER_EYE, Items.ENDER_PEARL, Items.SPLASH_POTION, Items.LINGERING_POTION);

	public AbstractCommentedConfig() {
		super("FastUse", KEY_UNBOUND, NightConfigCategory.PLAYER, "Allows you to use items faster.",
				new DynamicLightsInitializerMode("Mode", "Single", "Multi").withDesc("Whether to throw once per tick or multiple times."),
				new DynamicLightsInitializerSlider("Multi", 1, 100, 20, 0).withDesc("How many items to use per tick if on multi mode."),
				new DynamicLightsInitializerToggle("Throwables Only", true).withDesc("Only uses throwables.").withChildren(
						new DynamicLightsInitializerToggle("XP Only", false).withDesc("Only uses XP bottles.")));
	}

	@Subscribe
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
