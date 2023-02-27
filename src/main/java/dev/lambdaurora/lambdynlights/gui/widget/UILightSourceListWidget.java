/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.gui.widget;

import java.util.List;

import dev.lambdaurora.lambdynlights.gui.widget.window.InnerBackground;
import dev.lambdaurora.lambdynlights.gui.widget.window.UIContainer;
import dev.lambdaurora.lambdynlights.gui.widget.window.UIWindow;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.shadow.nightconfig.AnnotationUtils;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;


public class UILightSourceListWidget extends LightSourceListWidget {

	public static UILightSourceListWidget INSTANCE = new UILightSourceListWidget();

	private UIContainer uiContainer;

	public UILightSourceListWidget() {
		this(new UIContainer());
	}

	public UILightSourceListWidget(UIContainer uiContainer) {
		super(Text.literal("UI Editor"));
		this.uiContainer = uiContainer;
	}

	public void init() {
		super.init();

		clearWindows();
		uiContainer.windows.values().forEach(this::addWindow);

		addWindow(new InnerBackground(List.of(NightConfigManager.getModule(AnnotationUtils.class)),
				200, 200, 75, "Render", new ItemStack(Items.YELLOW_STAINED_GLASS)));
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		FileHelper.SCHEDULE_SAVE_UI.set(true);

		uiContainer.updatePositions(width, height);

		for (UIWindow w: uiContainer.windows.values()) {
			boolean shouldClose = w.shouldClose();
			if (shouldClose && !w.closed)
				w.detachFromOthers(false);

			w.closed = shouldClose;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	public UIContainer getUIContainer() {
		return uiContainer;
	}
}
