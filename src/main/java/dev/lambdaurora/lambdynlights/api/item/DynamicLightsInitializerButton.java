/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.api.item;

import dev.lambdaurora.lambdynlights.gui.widget.window.InnerBackground;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

public class DynamicLightsInitializerButton extends ModuleDynamicLightsInitializer<Void> {

	public Runnable action;

	public DynamicLightsInitializerButton(String text, Runnable action) {
		super(text, null, DynamicLightHandler.NULL);
		this.action = action;
	}

	public void render(InnerBackground window, MatrixStack matrices, int x, int y, int len) {
		if (window.mouseOver(x, y, x + len, y + 12)) {
			DrawableHelper.fill(matrices, x + 1, y, x + len, y + 12, 0xFF841D1D);
		}

		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, getName(), x + 3, y + 2, 0xFF841D1D);

		if (window.mouseOver(x, y, x + len, y + 12) && window.lmDown) {
			window.mouseReleased(window.mouseX, window.mouseY, 1);
			MinecraftClient.getInstance().currentScreen.mouseReleased(window.mouseX, window.mouseY, 0);
			MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.3F));
			action.run();
		}
	}

	public DynamicLightsInitializerButton withDesc(String desc) {
		setTooltip(desc);
		return this;
	}

	public int getHeight(int len) {
		return 12;
	}
}
