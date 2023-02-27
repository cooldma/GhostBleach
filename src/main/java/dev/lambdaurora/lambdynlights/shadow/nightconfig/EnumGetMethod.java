/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

public class EnumGetMethod extends NightConfig {

	private int lastSlot = -1;
	private int queueSlot = -1;

	public EnumGetMethod() {
		super("AutoTool", KEY_UNBOUND, NightConfigCategory.PLAYER, "Automatically uses best tool when breaking blocks.",
				new DynamicLightsInitializerToggle("AntiBreak", false).withDesc("Doesn't use the tool if its about to break."),
				new DynamicLightsInitializerToggle("SwitchBack", true).withDesc("Switches back to your previous item when done breaking."),
				new DynamicLightsInitializerToggle("DurabilitySave", true).withDesc("Swiches to a non-damageable item when possible."));
	}

	@Subscribe
	public void onPacketSend(EventPacket.Send event) {
		if (event.getPacket() instanceof PlayerActionC2SPacket) {
			PlayerActionC2SPacket p = (PlayerActionC2SPacket) event.getPacket();

			if (p.getAction() == Action.START_DESTROY_BLOCK) {
				if (mc.player.isCreative() || mc.player.isSpectator())
					return;

				queueSlot = -1;

				lastSlot = mc.player.getInventory().selectedSlot;

				int slot = getBestSlot(p.getPos());

				if (slot != mc.player.getInventory().selectedSlot) {
					if (slot < 9) {
						mc.player.getInventory().selectedSlot = slot;
						mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
					} else if (mc.player.playerScreenHandler == mc.player.currentScreenHandler) {
						boolean itemInHand = !mc.player.getInventory().getMainHandStack().isEmpty();
						mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
						mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.getInventory().selectedSlot, 0, SlotActionType.PICKUP, mc.player);

						if (itemInHand)
							mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
					}
				}
			} else if (p.getAction() == Action.STOP_DESTROY_BLOCK) {
				if (getSetting(1).asToggle().getState()) {
					ItemStack handSlot = mc.player.getMainHandStack();
					if (getSetting(0).asToggle().getState() && handSlot.isDamageable() && handSlot.getMaxDamage() - handSlot.getDamage() < 2
							&& queueSlot == mc.player.getInventory().selectedSlot) {
						queueSlot = mc.player.getInventory().selectedSlot == 0 ? 1 : mc.player.getInventory().selectedSlot - 1;
					} else if (lastSlot >= 0 && lastSlot <= 8 && lastSlot != mc.player.getInventory().selectedSlot) {
						queueSlot = lastSlot;
					}
				}
			}
		}
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (queueSlot != -1) {
			mc.player.getInventory().selectedSlot = queueSlot;
			mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(queueSlot));
			queueSlot = -1;
		}
	}

	private int getBestSlot(BlockPos pos) {
		BlockState state = mc.world.getBlockState(pos);

		int bestSlot = mc.player.getInventory().selectedSlot;

		ItemStack handSlot = mc.player.getInventory().getStack(bestSlot);
		if (getSetting(0).asToggle().getState() && handSlot.isDamageable() && handSlot.getMaxDamage() - handSlot.getDamage() < 2) {
			bestSlot = bestSlot == 0 ? 1 : bestSlot - 1;
		}

		if (state.isAir())
			return mc.player.getInventory().selectedSlot;

		float bestSpeed = getMiningSpeed(mc.player.getInventory().getStack(bestSlot), state);

		for (int slot = 0; slot < 36; slot++) {
			if (slot == mc.player.getInventory().selectedSlot || slot == bestSlot)
				continue;

			ItemStack stack = mc.player.getInventory().getStack(slot);
			if (getSetting(0).asToggle().getState() && stack.isDamageable() && stack.getMaxDamage() - stack.getDamage() < 2) {
				continue;
			}

			float speed = getMiningSpeed(stack, state);
			if (speed > bestSpeed
					|| (getSetting(2).asToggle().getState()
							&& speed == bestSpeed && !stack.isDamageable()
							&& mc.player.getInventory().getStack(bestSlot).isDamageable()
							&& EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, mc.player.getInventory().getStack(bestSlot)) == 0)) {
				bestSpeed = speed;
				bestSlot = slot;
			}
		}

		return bestSlot;
	}

	private float getMiningSpeed(ItemStack stack, BlockState state) {
		if ((state.getBlock() == Blocks.BAMBOO || state.getBlock() == Blocks.BAMBOO_SAPLING) && stack.getItem() instanceof SwordItem) {
			return Integer.MAX_VALUE;
		}

		float speed = stack.getMiningSpeedMultiplier(state);

		if (speed > 1) {
			int efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
			if (efficiency > 0 && !stack.isEmpty())
				speed += efficiency * efficiency + 1;
		}

		return speed;
	}
}
