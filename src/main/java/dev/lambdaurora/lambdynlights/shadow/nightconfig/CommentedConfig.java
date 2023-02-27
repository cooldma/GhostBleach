/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import org.apache.commons.lang3.RandomUtils;
import dev.lambdaurora.lambdynlights.event.events.EventClientMove;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.event.events.EventSendMovementPackets;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CommentedConfig extends NightConfig {

	public CommentedConfig() {
		super("ElytraFly", KEY_UNBOUND, NightConfigCategory.MOVEMENT, "Improves the elytra.",
				new DynamicLightsInitializerMode("Mode", "AutoBoost", "Boost", "Control", "BruhFly", "Pak\u00e8tFly").withDesc("Elytrafly mode."),
				new DynamicLightsInitializerSlider("Boost", 0, 0.15, 0.05, 2).withDesc("Boost speed."),
				new DynamicLightsInitializerSlider("MaxBoost", 0, 5, 2.5, 1).withDesc("Max boost speed."),
				new DynamicLightsInitializerSlider("Speed", 0, 5, 0.8, 2).withDesc("Speed for all the other modes."),
				new DynamicLightsInitializerSlider("Packets", 1, 10, 2, 0).withDesc("How many packets to send in packet mode."));
	}

	@Subscribe
	public void onClientMove(EventClientMove event) {
		/* Cancel the retarded auto elytra movement */
		if (getSetting(0).asMode().getMode() == 2 && mc.player.isFallFlying()) {
			if (!mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
				event.setVec(new Vec3d(event.getVec().x, 0, event.getVec().z));
			}

			if (!mc.options.backKey.isPressed() && !mc.options.leftKey.isPressed()
					&& !mc.options.rightKey.isPressed() && !mc.options.forwardKey.isPressed()) {
				event.setVec(new Vec3d(0, event.getVec().y, 0));
			}
		}
	}

	@Subscribe
	public void onTick(EventTick event) {
		Vec3d vec3d = new Vec3d(0, 0, getSetting(3).asSlider().getValue())
				.rotateY(-(float) Math.toRadians(mc.player.getYaw()));

		double currentVel = Math.abs(mc.player.getVelocity().x) + Math.abs(mc.player.getVelocity().y) + Math.abs(mc.player.getVelocity().z);
		float radianYaw = (float) Math.toRadians(mc.player.getYaw());
		float boost = getSetting(1).asSlider().getValueFloat();

		switch (getSetting(0).asMode().getMode()) {
			case 0:
				if (mc.player.isFallFlying() && currentVel <= getSetting(2).asSlider().getValue()) {
					if (mc.options.backKey.isPressed()) {
						mc.player.addVelocity(MathHelper.sin(radianYaw) * boost, 0, MathHelper.cos(radianYaw) * -boost);
					} else if (mc.player.getPitch() > 0) {
						mc.player.addVelocity(MathHelper.sin(radianYaw) * -boost, 0, MathHelper.cos(radianYaw) * boost);
					}
				}

				break;
			case 1:
				if (mc.player.isFallFlying() && currentVel <= getSetting(2).asSlider().getValue()) {
					if (mc.options.forwardKey.isPressed()) {
						mc.player.addVelocity(MathHelper.sin(radianYaw) * -boost, 0, MathHelper.cos(radianYaw) * boost);
					} else if (mc.options.backKey.isPressed()) {
						mc.player.addVelocity(MathHelper.sin(radianYaw) * boost, 0, MathHelper.cos(radianYaw) * -boost);
					}
				}

				break;
			case 2:
				if (mc.player.isFallFlying()) {
					if (mc.options.backKey.isPressed()) vec3d = vec3d.negate();
					if (mc.options.leftKey.isPressed()) vec3d = vec3d.rotateY((float) Math.toRadians(90));
					else if (mc.options.rightKey.isPressed()) vec3d = vec3d.rotateY(-(float) Math.toRadians(90));
					if (mc.options.jumpKey.isPressed()) vec3d = vec3d.add(0, getSetting(3).asSlider().getValue(), 0);
					if (mc.options.sneakKey.isPressed()) vec3d = vec3d.add(0, -getSetting(3).asSlider().getValue(), 0);

					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
							mc.player.getX() + vec3d.x, mc.player.getY() - 0.01, mc.player.getZ() + vec3d.z, false));

					mc.player.setVelocity(vec3d.x, vec3d.y, vec3d.z);
				}

				break;
			case 3:
				if (shouldPacketFly()) {
					mc.player.setVelocity(vec3d);
					mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
							mc.player.getX() + vec3d.x, mc.player.getY() + vec3d.y, mc.player.getZ() + vec3d.z, true));
				}

				break;
			case 4:
				if (shouldPacketFly()) {
					mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
					double randMult = RandomUtils.nextDouble(0.9, 1.1);

					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
							mc.player.getX() + vec3d.x * randMult,
							mc.player.getY(),
							mc.player.getZ() + vec3d.z * randMult,
							false));

					for (int i = 0; i < 6; i++) {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
								mc.player.getX() + vec3d.x * (randMult + i),
								mc.player.getY() - 0.0001,
								mc.player.getZ() + vec3d.z * (randMult + i),
								true));
					}
				}
		}
	}

	// Packet moment

	@Subscribe
	public void onMovement(EventSendMovementPackets event) {
		if (getSetting(0).asMode().getMode() == 4 && shouldPacketFly()) {
			mc.player.setVelocity(Vec3d.ZERO);
			event.setCancelled(true);
		}
	}

	@Subscribe
	public void onMovement(EventClientMove event) {
		if (getSetting(0).asMode().getMode() == 4 && shouldPacketFly()) {
			event.setCancelled(true);
		}
	}

	@Subscribe
	public void onReadPacket(EventPacket.Read event) {
		if (getSetting(0).asMode().getMode() == 4 && shouldPacketFly() && event.getPacket() instanceof PlayerPositionLookS2CPacket) {
			PlayerPositionLookS2CPacket p = (PlayerPositionLookS2CPacket) event.getPacket();

			p.yaw = mc.player.getYaw();
			p.pitch = mc.player.getPitch();
		}
	}

	@Subscribe
	public void onSendPacket(EventPacket.Send event) {
		if (getSetting(0).asMode().getMode() == 4 && shouldPacketFly()) {
			if (event.getPacket() instanceof PlayerMoveC2SPacket.LookAndOnGround) {
				event.setCancelled(true);
				return;
			}

			if (event.getPacket() instanceof PlayerMoveC2SPacket.Full) {
				event.setCancelled(true);
				PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) event.getPacket();
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.getX(0), p.getY(0), p.getZ(0), p.isOnGround()));
			}
		}
	}

	private boolean shouldPacketFly() {
		return !mc.player.isOnGround()
				&& !mc.options.sneakKey.isPressed()
				&& mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA;
	}
}
