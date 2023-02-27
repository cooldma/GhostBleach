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
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ConfigSpec extends NightConfig {

	private int delay;
	private boolean holdingTotem;

	public ConfigSpec() {
		super("AutoTotem", KEY_UNBOUND, NightConfigCategory.COMBAT, "Automatically equips totems.",
				new DynamicLightsInitializerToggle("Override", false).withDesc("Equips a totem even if theres another item in the offhand."),
				new DynamicLightsInitializerSlider("Delay", 0, 10, 0, 0).withDesc("Minimum delay between equipping totems (in ticks)."),
				new DynamicLightsInitializerSlider("PopDelay", 0, 10, 0, 0).withDesc("How long to wait after popping to equip a new totem (in ticks)."));
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (holdingTotem && mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
			delay = Math.max(getSetting(2).asSlider().getValueInt(), delay);
		}

		holdingTotem = mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING;

		if (delay > 0) {
			delay--;
			return;
		}

		if (holdingTotem || (!mc.player.getOffHandStack().isEmpty() && !getSetting(0).asToggle().getState())) {
			return;
		}

		// Cancel at all non-survival-inventory containers
		if (mc.player.playerScreenHandler == mc.player.currentScreenHandler) {
			for (int i = 9; i < 45; i++) {
				if (mc.player.getInventory().getStack(i >= 36 ? i - 36 : i).getItem() == Items.TOTEM_OF_UNDYING) {
					boolean itemInOffhand = !mc.player.getOffHandStack().isEmpty();
					mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
					mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);

					if (itemInOffhand)
						mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);

					delay = getSetting(1).asSlider().getValueInt();
					return;
				}
			}
		} else {
			// If the player is in another inventory, atleast check the hotbar for anything to swap
			for (int i = 0; i < 9; i++) {
				if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
					if (i != mc.player.getInventory().selectedSlot) {
						mc.player.getInventory().selectedSlot = i;
						mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
					}

					mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
					delay = getSetting(1).asSlider().getValueInt();
					return;
				}
			}
		}
	}

}
