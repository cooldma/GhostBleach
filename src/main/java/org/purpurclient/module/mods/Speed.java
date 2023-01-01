/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurclient.module.mods;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import org.purpurclient.event.events.EventClientMove;
import org.purpurclient.event.events.EventTick;
import org.purpurclient.eventbus.BleachSubscribe;
import org.purpurclient.module.Module;
import org.purpurclient.module.ModuleCategory;
import org.purpurclient.setting.module.SettingMode;
import org.purpurclient.setting.module.SettingSlider;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.purpurclient.setting.module.SettingToggle;


public class Speed extends Module {

	private boolean jumping;
	PlayerEntity target = (PlayerEntity) mc.player;

	public Speed() {
		super("Speed", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Allows you to go faster, what did you expect?",
				new SettingMode("Mode", "StrafeHop", "Strafe", "OnGround", "MiniHop", "Bhop").withDesc("Speed mode."),
				new SettingSlider("Strafe", 0.15, 0.55, 0.27, 2).withDesc("Strafe speed."),
				new SettingSlider("OnGround", 0.1, 10, 2, 1).withDesc("OnGround speed."),
				new SettingSlider("MiniHop", 0.1, 25, 2, 1).withDesc("MiniHop speed."),
				new SettingSlider("Bhop", 0.1, 10, 2, 1).withDesc("Bhop speed."),
				new SettingToggle("NoInertia", false).withDesc("Prevents you from moving forcefully."));
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		//System.out.println(mc.player.forwardSpeed + " | " + mc.player.sidewaysSpeed);
		if (mc.options.sneakKey.isPressed())
			return;

			/* Strafe */
		if (getSetting(0).asMode().getMode() <= 1) {
			if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0) /*&& mc.player.isOnGround()*/) {
				if (!mc.player.isSprinting()) {
					mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
				}
				if (hasElytra (mc.player.getInventory())) {

					mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
					mc.player.updateVelocity(getSetting(1).asSlider().getValueFloat(),
							new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
				}

				double vel = Math.abs(mc.player.getVelocity().getX()) + Math.abs(mc.player.getVelocity().getZ());

				if (getSetting(0).asMode().getMode() == 0 && vel >= 0.12 && mc.player.isOnGround()) {
					mc.player.updateVelocity(vel >= 0.3 ? 0.0f : 0.15f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
					mc.player.jump();
				}
			}

			/* OnGround */
		} else if (getSetting(0).asMode().getMode() == 2) {
			if (mc.options.jumpKey.isPressed() || mc.player.fallDistance > 0.25)
				return;

			double speeds = 0.85 + getSetting(2).asSlider().getValue() / 30;

			if (jumping && mc.player.getY() >= mc.player.prevY + 0.399994D) {
				mc.player.setVelocity(mc.player.getVelocity().x, -0.9, mc.player.getVelocity().z);
				mc.player.setPos(mc.player.getX(), mc.player.prevY, mc.player.getZ());
				jumping = false;
			}

			if (mc.player.forwardSpeed != 0.0F && !mc.player.horizontalCollision) {
				if (mc.player.verticalCollision) {
					mc.player.setVelocity(mc.player.getVelocity().x * speeds, mc.player.getVelocity().y, mc.player.getVelocity().z * speeds);
					jumping = true;
					mc.player.jump();
					// 1.0379
				}

				if (jumping && mc.player.getY() >= mc.player.prevY + 0.399994D) {
					mc.player.setVelocity(mc.player.getVelocity().x, -100, mc.player.getVelocity().z);
					jumping = false;
				}

			}

			/* MiniHop */
		} else if (getSetting(0).asMode().getMode() == 3) {
			if (mc.player.horizontalCollision || mc.options.jumpKey.isPressed() || mc.player.forwardSpeed == 0) {
				return;
			}
			if (hasElytra (mc.player.getInventory())) {

				double speeds = 0.9 + getSetting(3).asSlider().getValue() / 30;

				if (mc.player.isOnGround()) {
					mc.player.jump();
				} else if (mc.player.getVelocity().y > 0) {
					mc.player.setVelocity(mc.player.getVelocity().x * speeds, -1, mc.player.getVelocity().z * speeds);
					mc.player.input.movementSideways += 1.5F;
				}
			}

			/* Bhop */
		} else if (getSetting(0).asMode().getMode() == 4) {
			if (mc.player.forwardSpeed > 0 && mc.player.isOnGround()) {
				double speeds = 0.65 + getSetting(4).asSlider().getValue() / 30;

				mc.player.jump();
				mc.player.setVelocity(mc.player.getVelocity().x * speeds, 0.255556, mc.player.getVelocity().z * speeds);
				mc.player.sidewaysSpeed += 3.0F;
				mc.player.jump();
				mc.player.setSprinting(true);
			}
		}
	}

	public static boolean hasElytra(PlayerInventory inventory) {
		int chestSlot = 2;
		return (inventory.armor.get(chestSlot).getCount() == 1 &&
				inventory.armor.get(chestSlot).getItem().asItem().equals(Items.ELYTRA));
	}

	@BleachSubscribe
	public void onMove(EventClientMove event) {
		if (mc.player.forwardSpeed == 0 && mc.player.sidewaysSpeed == 0 && getSetting(5).asToggle().getState()) {
			event.setVec(new Vec3d(0, event.getVec().y, 0));
		}
	}

}
