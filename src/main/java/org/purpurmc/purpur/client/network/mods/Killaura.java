/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurmc.purpur.client.network.mods;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.purpurmc.purpur.client.event.events.EventTick;
import org.purpurmc.purpur.client.eventbus.BleachSubscribe;
import org.purpurmc.purpur.client.network.Module;
import org.purpurmc.purpur.client.network.ModuleCategory;
import org.purpurmc.purpur.client.setting.module.SettingMode;
import org.purpurmc.purpur.client.setting.module.SettingRotate;
import org.purpurmc.purpur.client.setting.module.SettingSlider;
import org.purpurmc.purpur.client.setting.module.SettingToggle;
import org.purpurmc.purpur.client.util.world.EntityUtils;
import org.purpurmc.purpur.client.util.world.WorldUtils;

import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;

import com.google.common.collect.Streams;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Killaura extends Module {

	private int delay = 0;

	public Killaura() {
		super("Killaura", KEY_UNBOUND, ModuleCategory.COMBAT, "Automatically attacks entities.",
				new SettingMode("Sort", "Angle", "Distance").withDesc("How to sort targets."),
				new SettingToggle("Players", true).withDesc("Attacks Players."),
				new SettingToggle("Mobs", true).withDesc("Attacks Mobs."),
				new SettingToggle("Animals", false).withDesc("Attacks Animals."),
				new SettingToggle("ArmorStands", false).withDesc("Attacks Armor Stands."),
				new SettingToggle("Projectiles", false).withDesc("Attacks Shulker Bullets & Fireballs."),
				new SettingToggle("Triggerbot", false).withDesc("Only attacks the entity you're looking at."),
				new SettingToggle("MultiAura", false).withDesc("Atacks multiple entities at once.").withChildren(
						new SettingSlider("Targets", 1, 20, 3, 0).withDesc("How many targets to attack at once.")),
				new SettingRotate(true).withDesc("Rotates when attacking entities."),
				new SettingToggle("Raycast", true).withDesc("Only attacks if you can see the target."),
				new SettingToggle("1.9 Delay", false).withDesc("Uses the 1.9+ delay between hits."),
				new SettingSlider("Range", 0, 6, 4.25, 2).withDesc("Attack range."),
				new SettingSlider("CPS", 0, 20, 8, 0).withDesc("Attack CPS if 1.9 delay is disabled."));
	}



	@BleachSubscribe
	public void onTick(EventTick event) {
		if (!mc.player.isAlive()) {
			return;
		}

		delay++;
		int reqDelay = (int) Math.rint(20 / getSetting(12).asSlider().getValue());

		boolean cooldownDone = getSetting(10).asToggle().getState()
				? mc.player.getAttackCooldownProgress(mc.getTickDelta()) == 1.0f
				: (delay > reqDelay || reqDelay == 0);

		if (cooldownDone) {
			for (Entity e: getEntities()) {
				boolean shouldRotate = getSetting(8).asRotate().getState() && DebugRenderer.getTargetedEntity(mc.player, 7).orElse(null) != e;

				if (shouldRotate) {
					WorldUtils.facePosAuto(e.getX(), e.getY() + e.getHeight() / 2, e.getZ(), getSetting(8).asRotate());
				}

				boolean wasSprinting = mc.player.isSprinting();

				if (wasSprinting)
					mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));

				mc.interactionManager.attackEntity(mc.player, e);
				mc.player.swingHand(Hand.MAIN_HAND);

				if (wasSprinting)
					mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));

				delay = 0;
			}
		}
	}

	private List<Entity> getEntities() {
		Stream<Entity> targets;

		if (getSetting(6).asToggle().getState()) {
			Optional<Entity> entity = DebugRenderer.getTargetedEntity(mc.player, 7);

			if (entity.isEmpty()) {
				return Collections.emptyList();
			}

			targets = Stream.of(entity.get());
		} else {
			targets = Streams.stream(mc.world.getEntities());
		}

		Comparator<Entity> comparator;

		if (getSetting(0).asMode().getMode() == 0) {
			comparator = Comparator.comparing(e -> {
				Vec3d center = e.getBoundingBox().getCenter();

				double diffX = center.x - mc.player.getX();
				double diffY = center.y - mc.player.getEyeY();
				double diffZ = center.z - mc.player.getZ();

				double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

				float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
				float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

				return Math.abs(MathHelper.wrapDegrees(yaw - mc.player.getYaw())) + Math.abs(MathHelper.wrapDegrees(pitch - mc.player.getPitch()));
			});
		} else {
			comparator = Comparator.comparing(mc.player::distanceTo);
		}

		return targets
				.filter(e -> EntityUtils.isAttackable(e, true)
						&& mc.player.distanceTo(e) <= getSetting(11).asSlider().getValue()
						&& (mc.player.canSee(e) || !getSetting(9).asToggle().getState()))
				.filter(e -> (EntityUtils.isPlayer(e) && getSetting(1).asToggle().getState())
						|| (EntityUtils.isMob(e) && getSetting(2).asToggle().getState())
						|| (EntityUtils.isAnimal(e) && getSetting(3).asToggle().getState())
						|| (e instanceof ArmorStandEntity && getSetting(4).asToggle().getState())
						|| ((e instanceof ShulkerBulletEntity || e instanceof AbstractFireballEntity) && getSetting(5).asToggle().getState()))
				.sorted(comparator)
				.limit(getSetting(7).asToggle().getState() ? getSetting(7).asToggle().getChild(0).asSlider().getValueLong() : 1L)
				.collect(Collectors.toList());
	}
}
