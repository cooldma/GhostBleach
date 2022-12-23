/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam.module.mods;

import java.util.Map.Entry;
import java.util.Optional;

//import org.bleachhack.command.Command;
import org.nohurtcam.event.events.EventRenderCrosshair;
import org.nohurtcam.event.events.EventTick;
import org.nohurtcam.eventbus.BleachSubscribe;
//import org.bleachhack.gui.EntityMenuScreen;
import org.nohurtcam.module.Module;
import org.nohurtcam.module.ModuleCategory;
import org.nohurtcam.setting.module.SettingToggle;
import org.nohurtcam.util.collections.MutablePairList;
import org.nohurtcam.util.io.BleachFileHelper;
import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonElement;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author <a href="https://github.com/lasnikprogram">Lasnik</a>
 */
public class EntityMenu extends Module {
	
	// fuck maps
	public MutablePairList<String, String> interactions = new MutablePairList<>();

	private boolean buttonHeld;

	public EntityMenu() {
		super("EntityMenu", KEY_UNBOUND, ModuleCategory.MISC, "An interaction screen when looking at an entity and pressing the middle mouse button. Customizable via the " + "entitymenu command.",
				new SettingToggle("PlayersOnly", false).withDesc("Only opens the menu when clicking on players."));

		JsonElement je = BleachFileHelper.readMiscSetting("entityMenu");

		if (je != null && je.isJsonObject()) {
			for (Entry<String, JsonElement> entry: je.getAsJsonObject().entrySet()) {
				if (entry.getValue().isJsonPrimitive()) {
					interactions.add(entry.getKey(), entry.getValue().getAsString());
				}
			}
		}
	}
	
	@BleachSubscribe
	public void onTick(EventTick event) {
		if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS && !buttonHeld) {
			buttonHeld = true;
			
			Optional<Entity> lookingAt = DebugRenderer.getTargetedEntity(mc.player, 20);
			
			if (lookingAt.isPresent()) {
				Entity e = lookingAt.get();

				if (e instanceof LivingEntity && (e instanceof PlayerEntity || !getSetting(0).asToggle().getState())) {
//					mc.setScreen(new EntityMenuScreen((LivingEntity) e));
				}
			}
		} else if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_RELEASE) {
			buttonHeld = false;
		}
	}
	
	@BleachSubscribe
	public void onRenderCrosshair(EventRenderCrosshair event) {
//		if (mc.currentScreen instanceof EntityMenuScreen) {
//			event.setCancelled(true);
//		}
	}
}
