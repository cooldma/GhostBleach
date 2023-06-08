/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights;

import com.google.gson.JsonObject;

import dev.lambdaurora.lambdynlights.eventbus.EventBus;
import dev.lambdaurora.lambdynlights.eventbus.handler.InexactEventHandler;
import dev.lambdaurora.lambdynlights.gui.widget.LightSourceWidget;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.util.FriendManager;
import dev.lambdaurora.lambdynlights.util.Logger;
import dev.lambdaurora.lambdynlights.util.Watermark;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;
import dev.lambdaurora.lambdynlights.util.io.FileMang;
import net.fabricmc.api.ModInitializer;
import java.util.concurrent.CompletableFuture;


public class LambDynLights implements ModInitializer {

	private static LambDynLights instance = null;
	public static Watermark watermark;

	public static EventBus eventBus;

	public static FriendManager friendMang;

	private static CompletableFuture<JsonObject> updateJson;

	public static LambDynLights getInstance() {
		return instance;
	}

	public LambDynLights() {

	}

	// Phase 1
	// TODO: base-rewrite
	@Override
	public void onInitialize() {
		long initStartTime = System.currentTimeMillis();

		instance = this;
		watermark = new Watermark();
		eventBus = new EventBus(new InexactEventHandler("lambdynlights"), Logger.logger);

		friendMang = new FriendManager();

		FileMang.init();

		FileHelper.readOptions();
		FileHelper.readFriends();
	}

	// Phase 2
	// Called after most of the game has been initialized in MixinMinecraftClient so all game resources can be accessed
	public void postInit() {
		long initStartTime = System.currentTimeMillis();
		NightConfigManager.loadModules(this.getClass().getClassLoader().getResourceAsStream("lambdynlights.lightsource.json"));
		FileHelper.readModules();
		// TODO: move ClickGui and UI to phase 1
		LightSourceWidget.INSTANCE.initWindows();
		FileHelper.readClickGui();
		FileHelper.readUI();
		FileHelper.startSavingExecutor();
	}

	public static JsonObject getUpdateJson() {
		try {
			return updateJson.get();
		} catch (Exception e) {
			return null;
		}
	}
}
