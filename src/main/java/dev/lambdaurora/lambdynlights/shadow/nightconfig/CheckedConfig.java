/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class CheckedConfig extends NightConfig {

	private boolean jump = false;

	public CheckedConfig() {
		super("ElytraReplace", KEY_UNBOUND, NightConfigCategory.PLAYER, "Automatically replaces your elytra when its broken and continues flying.");
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (mc.player.playerScreenHandler != mc.player.currentScreenHandler)
			return;

		int chestSlot = 38;
		ItemStack chest = mc.player.getInventory().getStack(chestSlot);
		if (chest.getItem() instanceof ElytraItem && chest.getDamage() == (Items.ELYTRA.getMaxDamage() - 1)) {
			// search inventory for elytra

			Integer elytraSlot = null;
			for (int slot = 0; slot < 36; slot++) {
				ItemStack stack = mc.player.getInventory().getStack(slot);
				if (stack.getItem() instanceof ElytraItem && stack.getDamage() != (Items.ELYTRA.getMaxDamage() - 1)) {
					elytraSlot = slot;
					break;
				}
			}

			if (elytraSlot == null) {
				return;
			}

			mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
			mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, elytraSlot < 9 ? (elytraSlot + 36) : (elytraSlot), 0, SlotActionType.PICKUP,
					mc.player);
			mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);

			mc.options.jumpKey.setPressed(true); // Make them fly again
			jump = true;
		} else if (jump) {
			mc.options.jumpKey.setPressed(false); // Make them fly again
			jump = false;
		}
	}
}
