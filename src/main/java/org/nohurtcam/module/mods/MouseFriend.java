/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam.module.mods;

import java.util.Optional;

import org.nohurtcam.NoHurtCam;
import org.nohurtcam.event.events.EventTick;
import org.nohurtcam.eventbus.BleachSubscribe;
import org.nohurtcam.module.Module;
import org.nohurtcam.module.ModuleCategory;
import org.nohurtcam.setting.module.SettingMode;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class MouseFriend extends Module {

	private boolean buttonHeld = false;

	public MouseFriend() {
		super("MouseFriend", KEY_UNBOUND, ModuleCategory.MISC, "Adds/Removes friends using a mouse button.",
				new SettingMode("Button", "Middle", "Right", "MOUSE4", "MOUSE5", "MOUSE6").withDesc("What mouse button to use."));
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		int setting = getSetting(0).asMode().getMode();
		int button = setting == 0 ? GLFW.GLFW_MOUSE_BUTTON_MIDDLE : setting == 1 ? GLFW.GLFW_MOUSE_BUTTON_RIGHT : setting + 2;

		if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), button) == GLFW.GLFW_PRESS && !buttonHeld) {
			buttonHeld = true;

			Optional<Entity> lookingAt = DebugRenderer.getTargetedEntity(mc.player, 200);

			if (lookingAt.isPresent()) {
				Entity e = lookingAt.get();

				if (e instanceof PlayerEntity) {
					if (NoHurtCam.friendMang.has(e)) {
						NoHurtCam.friendMang.remove(e);
					} else {
						NoHurtCam.friendMang.add(e);
					}
				}
			}
		} else if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), button) == GLFW.GLFW_RELEASE) {
			buttonHeld = false;
		}
	}
}
