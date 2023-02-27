/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerColor;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerToggle;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.event.events.EventWorldRender;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.render.Renderer;
import dev.lambdaurora.lambdynlights.util.render.color.LineColor;

import net.minecraft.util.math.Vec3d;

public class AutosaveCommentedFileConfig extends NightConfig {

	private Map<Vec3d, Vec3d> trails = new LinkedHashMap<>();
	private Vec3d lastVec = null;

	public AutosaveCommentedFileConfig() {
		super("Trail", KEY_UNBOUND, NightConfigCategory.RENDER, "Shows a trail behind you.",
				new DynamicLightsInitializerToggle("Trail", true).withDesc("Enable trailing."),
				new DynamicLightsInitializerToggle("KeepTrail", false).withDesc("Keep the trail after turning the item off."),
				new DynamicLightsInitializerColor("Color", 200, 50, 50).withDesc("Main trail color."),
				new DynamicLightsInitializerToggle("BlendColor", true).withDesc("Blends the main color with a second color.").withChildren(
						new DynamicLightsInitializerColor("Second Color", 50, 200, 50).withDesc("The second color.")),

				new DynamicLightsInitializerSlider("Width", 0.1, 10, 3, 1).withDesc("Thickness of the trail."),
				new DynamicLightsInitializerSlider("Opacity", 0, 1, 0.75, 2).withDesc("Opacity of the trail."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		if (!getSetting(1).asToggle().getState()) {
			trails.clear();
		}

		lastVec = null;
		super.onDisable(inWorld);
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (!getSetting(0).asToggle().getState()) {
			return;
		}

		if (trails.isEmpty() || lastVec == null) {
			lastVec = mc.player.getPos().add(0, 0.1, 0);
			trails.put(mc.player.getPos(), lastVec);
		} else if (mc.player.getPos().add(0, 0.1, 0).distanceTo(lastVec) > 0.15) {
			trails.put(lastVec, mc.player.getPos().add(0, 0.1, 0));
			lastVec = mc.player.getPos().add(0, 0.1, 0);
		}
	}

	@Subscribe
	public void onRender(EventWorldRender.Post event) {
		int color = getSetting(2).asColor().getRGB();
		int secondColor = getSetting(3).asToggle().getChild(0).asColor().getRGB();

		int count = 250;
		boolean rev = false;
		for (Entry<Vec3d, Vec3d> e : trails.entrySet()) {
			if (getSetting(3).asToggle().getState()) {
				color = blendColor(color, secondColor, count / 255f);
			}

			Renderer.drawLine(
					e.getKey().x, e.getKey().y, e.getKey().z,
					e.getValue().x, e.getValue().y, e.getValue().z,
					LineColor.single((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff, (int) (getSetting(5).asSlider().getValueFloat() * 255)),
					getSetting(4).asSlider().getValueFloat());

			if (count < 5 || count > 250) {
				rev = !rev;
			}

			count += rev ? 3 : -3;
		}
	}

	// https://stackoverflow.com/questions/19398238/how-to-mix-two-int-colors-correctly moment
	private int blendColor(int color1, int color2, float ratio) {
		float iRatio = 1.0f - ratio;

		int a1 = (color1 >> 24 & 0xff);
		int r1 = ((color1 & 0xff0000) >> 16);
		int g1 = ((color1 & 0xff00) >> 8);
		int b1 = (color1 & 0xff);

		int a2 = (color2 >> 24 & 0xff);
		int r2 = ((color2 & 0xff0000) >> 16);
		int g2 = ((color2 & 0xff00) >> 8);
		int b2 = (color2 & 0xff);

		int a = (int) ((a1 * iRatio) + (a2 * ratio));
		int r = (int) ((r1 * iRatio) + (r2 * ratio));
		int g = (int) ((g1 * iRatio) + (g2 * ratio));
		int b = (int) ((b1 * iRatio) + (b2 * ratio));

		return a << 24 | r << 16 | g << 8 | b;
	}

}
