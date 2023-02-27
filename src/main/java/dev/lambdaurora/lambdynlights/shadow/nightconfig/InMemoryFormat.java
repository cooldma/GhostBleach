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
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.util.InventoryUtils;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.util.Hand;

public class InMemoryFormat extends NightConfig {

	private boolean eating;

	public InMemoryFormat() {
		super("AutoEat", KEY_UNBOUND, NightConfigCategory.PLAYER, "Automatically eats food for you.",
				new DynamicLightsInitializerToggle("Hunger", true).withDesc("Eats when you're bewlow a certain amount of hunger.").withChildren(
						new DynamicLightsInitializerSlider("Hunger", 0, 19, 14, 0).withDesc("The maximum hunger to eat at.")),
				new DynamicLightsInitializerToggle("Health", false).withDesc("Eats when you're bewlow a certain amount of health.").withChildren(
						new DynamicLightsInitializerSlider("Health", 0, 19, 14, 0).withDesc("The maximum health to eat at.")),
				new DynamicLightsInitializerToggle("Gapples", true).withDesc("Eats golden apples.").withChildren(
						new DynamicLightsInitializerToggle("Prefer", false).withDesc("Prefers golden apples avobe regular food.")),
				new DynamicLightsInitializerToggle("Chorus", false).withDesc("Eats chorus fruit."),
				new DynamicLightsInitializerToggle("Poisonous", false).withDesc("Eats poisonous food."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		mc.options.useKey.setPressed(false);

		super.onDisable(inWorld);
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (eating && mc.options.useKey.isPressed() && !mc.player.isUsingItem()) {
			eating = false;
			mc.options.useKey.setPressed(false);
		}

		if (getSetting(0).asToggle().getState() && mc.player.getHungerManager().getFoodLevel() <= getSetting(0).asToggle().getChild(0).asSlider().getValueInt()) {
			startEating();
		} else if (getSetting(1).asToggle().getState() && (int) mc.player.getHealth() + (int) mc.player.getAbsorptionAmount() <= getSetting(1).asToggle().getChild(0).asSlider().getValueInt()) {
			startEating();
		}
	}

	private void startEating() {
		boolean gapples = getSetting(2).asToggle().getState();
		boolean preferGapples = getSetting(2).asToggle().getChild(0).asToggle().getState();
		boolean chorus = getSetting(3).asToggle().getState();
		boolean poison = getSetting(4).asToggle().getState();

		int slot = -1;
		int hunger = -1;
		for (int s: InventoryUtils.getInventorySlots(true)) {
			FoodComponent food = mc.player.getInventory().getStack(s).getItem().getFoodComponent();

			if (food == null)
				continue;

			int h = preferGapples && (food == FoodComponents.GOLDEN_APPLE || food == FoodComponents.ENCHANTED_GOLDEN_APPLE)
					? Integer.MAX_VALUE : food.getHunger();

			if (h <= hunger
					|| (!gapples && (food == FoodComponents.GOLDEN_APPLE || food == FoodComponents.ENCHANTED_GOLDEN_APPLE))
					|| (!chorus && food == FoodComponents.CHORUS_FRUIT)
					|| (!poison && isPoisonous(food)))
				continue;

			slot = s;
			hunger = h;
		}

		if (hunger != -1) {
			if (slot == mc.player.getInventory().selectedSlot || slot == 40) {
				mc.options.useKey.setPressed(true);
				mc.interactionManager.interactItem(mc.player, slot == 40 ? Hand.OFF_HAND : Hand.MAIN_HAND);
				eating = true;
			} else {
				InventoryUtils.selectSlot(slot);
			}
		}
	}

	private boolean isPoisonous(FoodComponent food) {
		return food.getStatusEffects().stream().anyMatch(e -> e.getFirst().getEffectType().getCategory() == StatusEffectCategory.HARMFUL);
	}
}
