/*
 * This file is part of the Lithium distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.lithium.module.mods;

import java.util.Optional;

import org.lithium.Lithium;
import org.lithium.event.events.EventTick;
import org.lithium.eventbus.BleachSubscribe;
import org.lithium.module.Module;
import org.lithium.module.ModuleCategory;
import org.lithium.setting.module.SettingMode;
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
					if (Lithium.friendMang.has(e)) {
						Lithium.friendMang.remove(e);
					} else {
						Lithium.friendMang.add(e);
					}
				}
			}
		} else if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), button) == GLFW.GLFW_RELEASE) {
			buttonHeld = false;
		}
	}
}
