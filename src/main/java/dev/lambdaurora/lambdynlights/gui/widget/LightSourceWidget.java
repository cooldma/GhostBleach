/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.gui.widget;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import dev.lambdaurora.lambdynlights.gui.widget.window.DynamicLightsOptionsOption;
import dev.lambdaurora.lambdynlights.gui.widget.window.InnerBackground;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.shadow.nightconfig.ConfigSpecCorrectionListener;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;
import org.apache.commons.lang3.StringUtils;
//import org.lambdynlights.command.Command;
import dev.lambdaurora.lambdynlights.gui.window.Window;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public class LightSourceWidget extends LightSourceListWidget {
	
	public static LightSourceWidget INSTANCE = new LightSourceWidget();

	private TextFieldWidget searchField;

	public LightSourceWidget() {
		super(Text.literal("ClickGui"));
	}

	public void init() {
		super.init();

		searchField = new TextFieldWidget(textRenderer, 2, 14, 100, 12, Text.empty() /* @LasnikProgram is author lol */);
		searchField.visible = false;
		searchField.setMaxLength(20);
		searchField.setSuggestion("Search here");
		addDrawableChild(searchField);
	}

	public void initWindows() {
		int len = NightConfigManager.getModule(ConfigSpecCorrectionListener.class).getSetting(0).asSlider().getValueInt();
		
		int y = 50;
		for (NightConfigCategory c: NightConfigCategory.values()) {
			addWindow(new InnerBackground(NightConfigManager.getModulesInCat(c), 30, y, len, StringUtils.capitalize(c.name().toLowerCase()), c.getItem()));
			y += 16;
		}

		for (Window w: getWindows()) {
			if (w instanceof DynamicLightsOptionsOption) {
				((DynamicLightsOptionsOption) w).hiding = true;
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		FileHelper.SCHEDULE_SAVE_CLICKGUI.set(true);
		ConfigSpecCorrectionListener clickGui = NightConfigManager.getModule(ConfigSpecCorrectionListener.class);

		searchField.visible = clickGui.getSetting(1).asToggle().getState();

		if (clickGui.getSetting(1).asToggle().getState()) {
			searchField.setSuggestion(searchField.getText().isEmpty() ? "Search here" : "");

			Set<NightConfig> seachMods = new HashSet<>();
			if (!searchField.getText().isEmpty()) {
				for (NightConfig m : NightConfigManager.getModules()) {
					if (m.getName().toLowerCase(Locale.ENGLISH).contains(searchField.getText().toLowerCase(Locale.ENGLISH).replace(" ", ""))) {
						seachMods.add(m);
					}
				}
			}

			for (Window w : getWindows()) {
				if (w instanceof InnerBackground) {
					((InnerBackground) w).setSearchedModule(seachMods);
				}
			}
		}

		int len = clickGui.getSetting(0).asSlider().getValueInt();
		for (Window w : getWindows()) {
			if (w instanceof InnerBackground) {
				((InnerBackground) w).setLen(len);
			}
		}

		super.render(matrices, mouseX, mouseY, delta);

		textRenderer.draw(matrices, "", 3, 3, 0x305090);
		textRenderer.draw(matrices, "", 2, 2, 0x6090d0);

//		if (clickGui.getSetting(2).asToggle().getState()) {
//			textRenderer.drawWithShadow(matrices, "Current prefix is: \"" + Command.getPrefix() + "\" (" + Command.getPrefix() + "help)", 2, height - 20, 0x99ff99);
//			textRenderer.drawWithShadow(matrices, "Use " + Command.getPrefix() + "widget to reset the widget", 2, height - 10, 0x9999ff);
//		}
	}
}
