/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import java.util.Optional;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ParsingMode extends NightConfig {

	private boolean buttonHeld = false;

	public ParsingMode() {
		super("MouseFriend", KEY_UNBOUND, NightConfigCategory.MISC, "Adds/Removes friends using a mouse button.",
				new DynamicLightsInitializerMode("Button", "Middle", "Right", "MOUSE4", "MOUSE5", "MOUSE6").withDesc("What mouse button to use."));
	}

	@Subscribe
	public void onTick(EventTick event) {
		int setting = getSetting(0).asMode().getMode();
		int button = setting == 0 ? GLFW.GLFW_MOUSE_BUTTON_MIDDLE : setting == 1 ? GLFW.GLFW_MOUSE_BUTTON_RIGHT : setting + 2;

		if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), button) == GLFW.GLFW_PRESS && !buttonHeld) {
			buttonHeld = true;

			Optional<Entity> lookingAt = DebugRenderer.getTargetedEntity(mc.player, 200);

			if (lookingAt.isPresent()) {
				Entity e = lookingAt.get();

				if (e instanceof PlayerEntity) {
					if (LambDynLights.friendMang.has(e)) {
						LambDynLights.friendMang.remove(e);
					} else {
						LambDynLights.friendMang.add(e);
					}
				}
			}
		} else if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), button) == GLFW.GLFW_RELEASE) {
			buttonHeld = false;
		}
	}
}
