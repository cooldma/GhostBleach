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
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.event.events.EventBlockShape;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class ReaderInput extends NightConfig {

	public ReaderInput() {
		super("Jesus", KEY_UNBOUND, NightConfigCategory.PLAYER, "Allows you to walk on water.",
				new DynamicLightsInitializerMode("Mode", "Vibrate", "Solid").withDesc("The jesus mode."));
	}

	@Subscribe
	public void onTick(EventTick event) {
		Entity e = mc.player.getRootVehicle();

		if (e.isSneaking() || e.fallDistance > 3f) 
			return;

		if (isSubmerged(e.getPos().add(0, 0.3, 0))) {
			e.setVelocity(e.getVelocity().x, 0.08, e.getVelocity().z);
		} else if (isSubmerged(e.getPos().add(0, 0.1, 0))) {
			e.setVelocity(e.getVelocity().x, 0.05, e.getVelocity().z);
		} else if (isSubmerged(e.getPos().add(0, 0.05, 0))) {
			e.setVelocity(e.getVelocity().x, 0.01, e.getVelocity().z);
		} else if (isSubmerged(e.getPos())) {
			e.setVelocity(e.getVelocity().x, -0.005, e.getVelocity().z);
			e.setOnGround(true);
		}
	}

	@Subscribe
	public void onBlockShape(EventBlockShape event) {
		if (getSetting(0).asMode().getMode() == 1
				&& !mc.world.getFluidState(event.getPos()).isEmpty()
				&& !mc.player.isSneaking()
				&& !mc.player.isTouchingWater()
				&& mc.player.getY() >= event.getPos().getY() + 0.9) {
			event.setShape(VoxelShapes.cuboid(0, 0, 0, 1, 0.9, 1));
		}
	}
	
	private boolean isSubmerged(Vec3d pos) {
		BlockPos bp = new BlockPos(pos);
		FluidState state = mc.world.getFluidState(bp);

		return !state.isEmpty() && pos.y - bp.getY() <= state.getHeight();
	}
}
