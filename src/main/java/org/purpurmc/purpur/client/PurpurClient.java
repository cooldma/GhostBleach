/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurmc.purpur.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;

import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

//import org.bleachhack.command.CommandManager;
//import org.bleachhack.command.CommandSuggestor;
import org.purpurmc.purpur.client.eventbus.BleachEventBus;
import org.purpurmc.purpur.client.eventbus.handler.InexactEventHandler;
import org.purpurmc.purpur.client.gui.clickgui.ModuleClickGuiScreen;
import org.purpurmc.purpur.client.network.ModuleManager;
import org.purpurmc.purpur.client.setting.option.Option;
import org.purpurmc.purpur.client.util.BleachLogger;
import org.purpurmc.purpur.client.util.BleachPlayerManager;
import org.purpurmc.purpur.client.util.Watermark;
import org.purpurmc.purpur.client.util.io.BleachFileHelper;
import org.purpurmc.purpur.client.util.io.BleachFileMang;
import org.purpurmc.purpur.client.util.io.BleachJsonHelper;
import org.purpurmc.purpur.client.util.io.BleachOnlineMang;

public class PurpurClient implements ModInitializer {

	private static PurpurClient instance = null;

	public static final String VERSION = "";
	public static final int INTVERSION = 40;
	public static Watermark watermark;

	public static BleachEventBus eventBus;
	public static BleachPlayerManager playerMang;

	private static CompletableFuture<JsonObject> updateJson;

	public static PurpurClient getInstance() {
		return instance;
	}
	public PurpurClient() {
		if (instance != null) {
			throw new RuntimeException("A PurpurClient instance already exists.");
		}
	}

	// Phase 1
	// TODO: base-rewrite
	@Override
	public void onInitialize() {
		long initStartTime = System.currentTimeMillis();
		instance = this;
		watermark = new Watermark();
		eventBus = new BleachEventBus(new InexactEventHandler("bleachhack"), BleachLogger.logger);
		playerMang = new BleachPlayerManager();
		BleachFileMang.init();

		BleachFileHelper.readOptions();

		if (Option.PLAYERLIST_SHOW_AS_BH_USER.getValue()) {
			playerMang.startPinger();
		}

		if (Option.GENERAL_CHECK_FOR_UPDATES.getValue()) {
			updateJson = BleachOnlineMang.getResourceAsync("update/" + SharedConstants.getGameVersion().getName().replace(' ', '_') + ".json", BodyHandlers.ofString())
					.thenApply(s -> BleachJsonHelper.parseOrNull(s, JsonObject.class));
		}
	}

	// Phase 2
	// Called after most of the game has been initialized in MixinMinecraftClient so all game resources can be accessed
	public void postInit() {
		long initStartTime = System.currentTimeMillis();
		ModuleManager.loadModules(this.getClass().getClassLoader().getResourceAsStream("purpurclient.refmap.json"));
		BleachFileHelper.readModules();
		// TODO: move ClickGui and UI to phase 1
		ModuleClickGuiScreen.INSTANCE.initWindows();
		BleachFileHelper.readClickGui();
		BleachFileHelper.readUI();
		BleachFileHelper.startSavingExecutor();
	}
}
