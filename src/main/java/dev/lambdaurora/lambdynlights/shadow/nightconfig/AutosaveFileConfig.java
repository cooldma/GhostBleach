/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerColor;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.event.events.EventWorldRender;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.render.Renderer;
import dev.lambdaurora.lambdynlights.util.render.color.LineColor;
import dev.lambdaurora.lambdynlights.util.world.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

public class AutosaveFileConfig extends NightConfig {

	public AutosaveFileConfig() {
		super("Tracers", KEY_UNBOUND, NightConfigCategory.RENDER, "Shows lines to entities you select.",
				new DynamicLightsInitializerToggle("Players", true).withDesc("Shows Tracers for Players.").withChildren(
						new DynamicLightsInitializerColor("Player Color", 255, 75, 75).withDesc("Tracer color for players."),
						new DynamicLightsInitializerColor("Friend Color", 0, 255, 255).withDesc("Tracer color for friends.")),

				new DynamicLightsInitializerToggle("Mobs", false).withDesc("Shows Tracers for Mobs.").withChildren(
						new DynamicLightsInitializerColor("Color", 128, 25, 128).withDesc("Tracer color for mobs.")),

				new DynamicLightsInitializerToggle("Animals", false).withDesc("Shows Tracers for Animals.").withChildren(
						new DynamicLightsInitializerColor("Color", 75, 255, 75).withDesc("Tracer color for animals.")),

				new DynamicLightsInitializerToggle("Items", true).withDesc("Shows Tracers for Items.").withChildren(
						new DynamicLightsInitializerColor("Color", 255, 200, 50).withDesc("Tracer color for items.")),

				new DynamicLightsInitializerToggle("Crystals", true).withDesc("Shows Tracers for End Crystals.").withChildren(
						new DynamicLightsInitializerColor("Color", 255, 50, 255).withDesc("Tracer color for crystals.")),

				new DynamicLightsInitializerToggle("Vehicles", false).withDesc("Shows Tracers for Vehicles (minecarts/boats).").withChildren(
						new DynamicLightsInitializerColor("Color", 150, 150, 150).withDesc("Tracer color for vehicles.")),

				new DynamicLightsInitializerToggle("Armorstands", false).withDesc("Shows Tracers for armor stands.").withChildren(
						new DynamicLightsInitializerColor("Color", 160, 150, 50).withDesc("Outline color for armor stands.")),

				new DynamicLightsInitializerSlider("Width", 0.1, 5, 1.5, 1).withDesc("Thickness of the tracers."),
				new DynamicLightsInitializerSlider("Opacity", 0, 1, 0.75, 2).withDesc("Opacity of the tracers."));
	}

	@Subscribe
	public void onRender(EventWorldRender.Post event) {
		float width = getSetting(7).asSlider().getValueFloat();
		int opacity = (int) (getSetting(8).asSlider().getValueFloat() * 255);

		for (Entity e : mc.world.getEntities()) {
			int[] col = getColor(e);

			if (col != null) {
				Vec3d vec = e.getPos().subtract(Renderer.getInterpolationOffset(e));
				Vec3d vec2 = new Vec3d(0, 0, 75)
						.rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
						.rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
						.add(mc.cameraEntity.getEyePos());

				LineColor lineColor =  LineColor.single(col[0], col[1], col[2], opacity);
				Renderer.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, lineColor, width);
				Renderer.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + e.getHeight() * 0.9, vec.z, lineColor, width);
			}
		}
	}

	private int[] getColor(Entity e) {
		if (e == mc.player)
			return null;

		if (e instanceof PlayerEntity && getSetting(0).asToggle().getState()) {
			return getSetting(0).asToggle().getChild(LambDynLights.friendMang.has(e) ? 1 : 0).asColor().getRGBArray();
		} else if (e instanceof Monster && getSetting(1).asToggle().getState()) {
			return getSetting(1).asToggle().getChild(0).asColor().getRGBArray();
		} else if (EntityUtils.isAnimal(e) && getSetting(2).asToggle().getState()) {
			return getSetting(2).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof ItemEntity && getSetting(3).asToggle().getState()) {
			return getSetting(3).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof EndCrystalEntity && getSetting(4).asToggle().getState()) {
			return getSetting(4).asToggle().getChild(0).asColor().getRGBArray();
		} else if ((e instanceof BoatEntity || e instanceof AbstractMinecartEntity) && getSetting(5).asToggle().getState()) {
			return getSetting(5).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof ArmorStandEntity && getSetting(6).asToggle().getState()) {
			return getSetting(6).asToggle().getChild(0).asColor().getRGBArray();
		}

		return null;
	}
}
