/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum NightConfigCategory {
	PLAYER(new ItemStack(Items.ARMOR_STAND)),
	RENDER(new ItemStack(Items.YELLOW_STAINED_GLASS)),
	COMBAT(new ItemStack(Items.TOTEM_OF_UNDYING)),
	MOVEMENT(new ItemStack(Items.POTION)),
	EXPLOITS(new ItemStack(Items.REPEATING_COMMAND_BLOCK)),
	MISC(new ItemStack(Items.NAUTILUS_SHELL)),
	WORLD(new ItemStack(Items.GRASS_BLOCK));
	
	private final ItemStack item;
	
	NightConfigCategory(ItemStack item) {
		this.item = item;
	}
	
	public ItemStack getItem() {
		return item;
	}
}
