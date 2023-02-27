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
import dev.lambdaurora.lambdynlights.event.events.EventBlockShape;
import dev.lambdaurora.lambdynlights.event.events.EventClientMove;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.block.CactusBlock;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.shape.VoxelShapes;

public class FormatDetector extends NightConfig {

	public FormatDetector() {
		super("Solidify", KEY_UNBOUND, NightConfigCategory.WORLD, "Adds collision boxes to certain blocks/areas.",
				new DynamicLightsInitializerToggle("Cactus", true).withDesc("Makes cactuses solid so they don't prickle you."),
				new DynamicLightsInitializerToggle("Fire", true).withDesc("Makes fire solid."),
				new DynamicLightsInitializerToggle("Lava", true).withDesc("Makes lava solid."),
				new DynamicLightsInitializerToggle("Cobweb", false).withDesc("Makes cobwebs solid."),
				new DynamicLightsInitializerToggle("BerryBushes", false).withDesc("Makes berry bushes solid."),
				new DynamicLightsInitializerToggle("Honeyblocks", false).withDesc("Makes honey blocks solid so you don't slide on the edges."),
				new DynamicLightsInitializerToggle("PowderSnow", false).withDesc("Makes powdered snow solid even if you don't have lether boots."),
				new DynamicLightsInitializerToggle("Unloaded", true).withDesc("Adds walls to unloaded chunks."));
	}

	@Subscribe
	public void onBlockShape(EventBlockShape event) {
		if ((getSetting(0).asToggle().getState() && event.getState().getBlock() instanceof CactusBlock)
				|| (getSetting(1).asToggle().getState() && event.getState().getBlock() instanceof FireBlock)
				|| (getSetting(2).asToggle().getState() && event.getState().getFluidState().getFluid() instanceof LavaFluid)
				|| (getSetting(3).asToggle().getState() && event.getState().getBlock() instanceof CobwebBlock)
				|| (getSetting(4).asToggle().getState() && event.getState().getBlock() instanceof SweetBerryBushBlock)
				|| (getSetting(5).asToggle().getState() && event.getState().getBlock() instanceof HoneyBlock)
				|| (getSetting(6).asToggle().getState() && event.getState().getBlock() instanceof PowderSnowBlock)) {
			event.setShape(VoxelShapes.fullCube());
		}
	}

	@Subscribe
	public void onClientMove(EventClientMove event) {
		int x = (int) (mc.player.getX() + event.getVec().x) >> 4;
		int z = (int) (mc.player.getZ() + event.getVec().z) >> 4;
		if (getSetting(7).asToggle().getState() && !mc.world.getChunkManager().isChunkLoaded(x, z)) {
			event.setCancelled(true);
		}
	}

	@Subscribe
	public void onSendPacket(EventPacket.Send event) {
		if (getSetting(7).asToggle().getState()) {
			if (event.getPacket() instanceof VehicleMoveC2SPacket) {
				VehicleMoveC2SPacket packet = (VehicleMoveC2SPacket) event.getPacket();
				if (!mc.world.getChunkManager().isChunkLoaded((int) packet.getX() >> 4, (int) packet.getZ() >> 4)) {
					mc.player.getVehicle().updatePosition(mc.player.getVehicle().prevX, mc.player.getVehicle().prevY, mc.player.getVehicle().prevZ);
					event.setCancelled(true);
				}
			} else if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket) event.getPacket();
				if (!mc.world.getChunkManager().isChunkLoaded((int) packet.getX(mc.player.getX()) >> 4, (int) packet.getZ(mc.player.getZ()) >> 4)) {
					event.setCancelled(true);
				}
			}
		}
	}
}
