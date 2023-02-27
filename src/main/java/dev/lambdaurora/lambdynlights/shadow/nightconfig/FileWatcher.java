/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import org.lambdynlights.command.Command;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.io.FileMang;

public class FileWatcher extends NightConfig {

	private Random rand = new Random();
	private List<String> lines = new ArrayList<>();
	private int lineCount;
	private int tickCount;

	public FileWatcher() {
		super("Spammer", KEY_UNBOUND, NightConfigCategory.MISC, "Spams chat with messages you set (edit with spammer).",
				new DynamicLightsInitializerMode("Read", "Random", "Order").withDesc("How to read the spammer file."),
				new DynamicLightsInitializerSlider("Delay", 1, 120, 20, 0).withDesc("Delay between messages (in seconds)."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		FileMang.createFile("spammer.txt");
		lines = FileMang.readFileLines("spammer.txt");
		lineCount = 0;
	}

	@Subscribe
	public void onTick(EventTick event) {
		tickCount++;

		if (lines.isEmpty())
			return;

		if (tickCount % (getSetting(1).asSlider().getValueInt() * 20) == 0) {
			if (getSetting(0).asMode().getMode() == 0) {
				mc.player.sendChatMessage(lines.get(rand.nextInt(lines.size())), null);
			} else if (getSetting(0).asMode().getMode() == 1) {
				mc.player.sendChatMessage(lines.get(lineCount), null);
			}

			if (lineCount >= lines.size() - 1) {
				lineCount = 0;
			} else {
				lineCount++;
			}
		}
	}

}
