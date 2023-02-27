/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import java.io.IOException;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.item.*;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.event.events.EventEntityRender;
import dev.lambdaurora.lambdynlights.event.events.EventWorldRender;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.util.render.Renderer;
import dev.lambdaurora.lambdynlights.util.render.color.QuadColor;
import dev.lambdaurora.lambdynlights.util.shader.CoreShaders;
import dev.lambdaurora.lambdynlights.util.shader.ColorVertexConsumerProvider;
import dev.lambdaurora.lambdynlights.util.shader.ShaderEffectWrapper;
import dev.lambdaurora.lambdynlights.util.shader.ShaderLoader;
import dev.lambdaurora.lambdynlights.util.world.EntityUtils;

import com.google.gson.JsonSyntaxException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;

public class CheckedCommentedConfig extends NightConfig {

	private ShaderEffectWrapper shader;
	private ColorVertexConsumerProvider colorVertexer;

	public CheckedCommentedConfig() {
		super("ESP", KEY_UNBOUND, NightConfigCategory.RENDER, "Highlights Entities in the world.",
				new DynamicLightsInitializerMode("Render", "Shader", "Box").withDesc("The Render mode."),
				new DynamicLightsInitializerSlider("ShaderFill", 1, 255, 50, 0).withDesc("How opaque the fill on shader mode should be."),
				new DynamicLightsInitializerSlider("Box", 0, 5, 2, 1).withDesc("How thick the box outline should be."),
				new DynamicLightsInitializerSlider("BoxFill", 0, 255, 50, 0).withDesc("How opaque the fill on box mode should be."),

				new DynamicLightsInitializerToggle("Players", true).withDesc("Highlights Players.").withChildren(
						new DynamicLightsInitializerColor("Player Color", 255, 75, 75).withDesc("Outline color for players."),
						new DynamicLightsInitializerColor("Friend Color", 0, 255, 255).withDesc("Outline color for friends.")),

				new DynamicLightsInitializerToggle("Mobs", false).withDesc("Highlights Mobs.").withChildren(
						new DynamicLightsInitializerColor("Color", 128, 25, 128).withDesc("Outline color for mobs.")),

				new DynamicLightsInitializerToggle("Animals", false).withDesc("Highlights Animals").withChildren(
						new DynamicLightsInitializerColor("Color", 75, 255, 75).withDesc("Outline color for animals.")),

				new DynamicLightsInitializerToggle("Items", true).withDesc("Highlights Items.").withChildren(
						new DynamicLightsInitializerColor("Color", 255, 200, 50).withDesc("Outline color for items.")),

				new DynamicLightsInitializerToggle("Crystals", true).withDesc("Highlights End Crystals.").withChildren(
						new DynamicLightsInitializerColor("Color", 255, 50, 255).withDesc("Outline color for crystals.")),

				new DynamicLightsInitializerToggle("Vehicles", false).withDesc("Highlights Vehicles.").withChildren(
						new DynamicLightsInitializerColor("Color", 150, 150, 150).withDesc("Outline color for vehicles (minecarts/boats).")),

				new DynamicLightsInitializerToggle("Armorstands", false).withDesc("Highlights armor stands.").withChildren(
						new DynamicLightsInitializerColor("Color", 160, 150, 50).withDesc("Outline color for armor stands.")));
		
		try {
			shader = new ShaderEffectWrapper(
					ShaderLoader.loadEffect(mc.getFramebuffer(), new Identifier("lambdynlights", "shaders/post/entity_outline.json")));
			
			colorVertexer = new ColorVertexConsumerProvider(shader.getFramebuffer("main"), CoreShaders::getColorOverlayShader);
		} catch (JsonSyntaxException | IOException e) {
			throw new RuntimeException("Failed to initialize ESP Shader! loaded too early?", e);
		}
	}

	@Subscribe
	public void onWorldRender(EventWorldRender.Pre event) {
		shader.prepare();
		shader.clearFramebuffer("main");
	}

	@Subscribe
	public void onEntityRender(EventEntityRender.Single.Pre event) {
		if (getSetting(0).asMode().getMode() != 0)
			return;

		int[] color = getColor(event.getEntity());

		if (color != null) {
			event.setVertex(colorVertexer.createDualProvider(event.getVertex(), color[0], color[1], color[2], getSetting(1).asSlider().getValueInt()));
		}
	}

	@Subscribe
	public void onWorldRender(EventWorldRender.Post event) {
		if (getSetting(0).asMode().getMode() == 0) {
			colorVertexer.draw();
			shader.render();
			shader.drawFramebufferToMain("main");
		} else {
			float width = getSetting(2).asSlider().getValueFloat();
			int fill = getSetting(3).asSlider().getValueInt();

			for (Entity e: mc.world.getEntities()) {
				int[] color = getColor(e);

				if (color != null) {
					if (width != 0)
						Renderer.drawBoxOutline(e.getBoundingBox(), QuadColor.single(color[0], color[1], color[2], 255), width);

					if (fill != 0)
						Renderer.drawBoxFill(e.getBoundingBox(), QuadColor.single(color[0], color[1], color[2], fill));
				}
			}
		}
	}

	private int[] getColor(Entity e) {
		if (e == mc.player)
			return null;

		if (e instanceof PlayerEntity && getSetting(4).asToggle().getState()) {
			return getSetting(4).asToggle().getChild(LambDynLights.friendMang.has(e) ? 1 : 0).asColor().getRGBArray();
		} else if (e instanceof Monster && getSetting(5).asToggle().getState()) {
			return getSetting(5).asToggle().getChild(0).asColor().getRGBArray();
		} else if (EntityUtils.isAnimal(e) && getSetting(6).asToggle().getState()) {
			return getSetting(6).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof ItemEntity && getSetting(7).asToggle().getState()) {
			return getSetting(7).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof EndCrystalEntity && getSetting(8).asToggle().getState()) {
			return getSetting(8).asToggle().getChild(0).asColor().getRGBArray();
		} else if ((e instanceof BoatEntity || e instanceof AbstractMinecartEntity) && getSetting(9).asToggle().getState()) {
			return getSetting(9).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof ArmorStandEntity && getSetting(10).asToggle().getState()) {
			return getSetting(10).asToggle().getChild(0).asColor().getRGBArray();
		}

		return null;
	}
}