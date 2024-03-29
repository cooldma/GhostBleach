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
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerBlockList;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.event.events.EventRenderBlock;
import dev.lambdaurora.lambdynlights.event.events.EventRenderFluid;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.world.WorldUtils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FernBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.client.render.RenderLayer;

public class AdvancedPath extends NightConfig {

	private double gamma;

	public AdvancedPath() {
		super("OreSearcher", KEY_UNBOUND, NightConfigCategory.RENDER, "Makes non ores invisible ig.",
				new DynamicLightsInitializerToggle("Fluids", false).withDesc("Show fluids."),
				new DynamicLightsInitializerToggle("Opacity", true).withDesc("Toggles an adjustable alpha level for non-xray blocks.").withChildren(
						new DynamicLightsInitializerSlider("Value", 0, 255, 64, 0).withDesc("Block alpha value."),
						new DynamicLightsInitializerToggle("HideSurface", false).withDesc("Hides the surface of the world to make it easier to see blocks.")),
				new DynamicLightsInitializerBlockList("Edit Blocks", "Edit Xray Blocks",
						Blocks.COPPER_ORE,
						Blocks.IRON_ORE,
						Blocks.GOLD_ORE,
						Blocks.LAPIS_ORE,
						Blocks.REDSTONE_ORE,
						Blocks.DIAMOND_ORE,
						Blocks.EMERALD_ORE,
						Blocks.DEEPSLATE_COPPER_ORE,
						Blocks.DEEPSLATE_IRON_ORE,
						Blocks.DEEPSLATE_GOLD_ORE,
						Blocks.DEEPSLATE_LAPIS_ORE,
						Blocks.DEEPSLATE_REDSTONE_ORE,
						Blocks.DEEPSLATE_DIAMOND_ORE,
						Blocks.DEEPSLATE_EMERALD_ORE,
						Blocks.COPPER_BLOCK,
						Blocks.IRON_BLOCK,
						Blocks.GOLD_BLOCK,
						Blocks.LAPIS_BLOCK,
						Blocks.REDSTONE_BLOCK,
						Blocks.DIAMOND_BLOCK,
						Blocks.EMERALD_BLOCK,
						Blocks.NETHER_GOLD_ORE,
						Blocks.ANCIENT_DEBRIS).withDesc("Edit the xray blocks."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		mc.chunkCullingEnabled = false;
		mc.worldRenderer.reload();

		gamma = mc.options.getGamma().getValue();
	}

	@Override
	public void onDisable(boolean inWorld) {
		mc.options.getGamma().setValue(gamma);

		mc.chunkCullingEnabled = true;
		mc.worldRenderer.reload();

		super.onDisable(inWorld);
	}

	@Subscribe
	public void onTick(EventTick eventPreUpdate) {
		mc.options.getGamma().setValue(69.420);
	}

	@Subscribe
	public void onRenderBlockLight(EventRenderBlock.Light event) {
		event.setLight(1f);
	}

	@Subscribe
	public void onRenderBlockOpaque(EventRenderBlock.Opaque event) {
		event.setOpaque(true);
	}

	@Subscribe
	public void onRenderBlockDrawSide(EventRenderBlock.ShouldDrawSide event) {
		if (getSetting(2).asList(Block.class).contains(event.getState().getBlock())) {
			event.setDrawSide(true);
		} else if (!getSetting(1).asToggle().getState()) {
			event.setDrawSide(false);
		}
	}

	@Subscribe
	public void onRenderBlockTesselate(EventRenderBlock.Tesselate event) {
		if (!getSetting(2).asList(Block.class).contains(event.getState().getBlock())) {
			if (getSetting(1).asToggle().getState()) {
				if (getSetting(1).asToggle().getChild(1).asToggle().getState()
						&& (event.getState().getBlock() instanceof FernBlock
								|| event.getState().getBlock() instanceof TallPlantBlock
								|| WorldUtils.getTopBlockIgnoreLeaves(event.getPos().getX(), event.getPos().getZ()) == event.getPos().getY())) {
					event.setCancelled(true);
					return;
				}

				event.getVertexConsumer().fixedColor(-1, -1, -1, getSetting(1).asToggle().getChild(0).asSlider().getValueInt());
			} else {
				event.setCancelled(true);
			}
		}
	}

	@Subscribe
	public void onRenderBlockLayer(EventRenderBlock.Layer event) {
		if (getSetting(1).asToggle().getState() && !getSetting(2).asList(Block.class).contains(event.getState().getBlock())) {
			event.setLayer(RenderLayer.getTranslucent());
		}
	}

	@Subscribe
	public void onRenderFluid(EventRenderFluid event) {
		if (!getSetting(0).asToggle().getState()) {
			event.setCancelled(true);
		}
	}
}
