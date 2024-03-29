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
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.event.events.EventClientMove;
import dev.lambdaurora.lambdynlights.event.events.EventOpenScreen;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.world.PlayerCopyEntity;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class WritingMode extends NightConfig {

	private PlayerCopyEntity dummy;
	private double[] playerPos;
	private float[] playerRot;
	private Entity riding;

	private boolean prevFlying;
	private float prevFlySpeed;

	public WritingMode() {
		super("Freecam", KEY_UNBOUND, NightConfigCategory.PLAYER, "Its freecam, you know what it does.",
				new DynamicLightsInitializerSlider("Speed", 0, 3, 0.5, 2).withDesc("Moving speed in freecam."),
				new DynamicLightsInitializerToggle("HorseInv", true).withDesc("Opens your Horse inventory when riding a horse."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		if (!inWorld)
			return;

		super.onEnable(inWorld);

		mc.chunkCullingEnabled = false;

		playerPos = new double[] { mc.player.getX(), mc.player.getY(), mc.player.getZ() };
		playerRot = new float[] { mc.player.getYaw(), mc.player.getPitch() };

		dummy = new PlayerCopyEntity(mc.player);

		dummy.spawn();

		if (mc.player.getVehicle() != null) {
			riding = mc.player.getVehicle();
			mc.player.getVehicle().removeAllPassengers();
		}

		if (mc.player.isSprinting()) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
		}

		prevFlying = mc.player.getAbilities().flying;
		prevFlySpeed = mc.player.getAbilities().getFlySpeed();
	}

	@Override
	public void onDisable(boolean inWorld) {
		if (inWorld) {
			mc.chunkCullingEnabled = true;
	
			dummy.despawn();
			mc.player.noClip = false;
			mc.player.getAbilities().flying = prevFlying;
			mc.player.getAbilities().setFlySpeed(prevFlySpeed);
	
			mc.player.refreshPositionAndAngles(playerPos[0], playerPos[1], playerPos[2], playerRot[0], playerRot[1]);
			mc.player.setVelocity(Vec3d.ZERO);
	
			if (riding != null && mc.world.getEntityById(riding.getId()) != null) {
				mc.player.startRiding(riding);
			}
		}

		super.onDisable(inWorld);
	}

	@Subscribe
	public void sendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof ClientCommandC2SPacket || event.getPacket() instanceof PlayerMoveC2SPacket) {
			event.setCancelled(true);
		}
	}

	@Subscribe
	public void onOpenScreen(EventOpenScreen event) {
		if (getSetting(1).asToggle().getState() && riding instanceof AbstractHorseEntity) {
			if (event.getScreen() instanceof InventoryScreen) {
				mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
				event.setCancelled(true);
			}
		}
	}

	@Subscribe
	public void onClientMove(EventClientMove event) {
		mc.player.noClip = true;
	}

	@Subscribe
	public void onTick(EventTick event) {
		mc.player.setOnGround(false);
		mc.player.getAbilities().setFlySpeed((float) (getSetting(0).asSlider().getValue() / 5));
		mc.player.getAbilities().flying = true;
		mc.player.setPose(EntityPose.STANDING);
	}
}
