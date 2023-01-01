/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurclient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;

import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

//import org.bleachhack.command.CommandManager;
//import org.bleachhack.command.CommandSuggestor;
import org.purpurclient.eventbus.BleachEventBus;
import org.purpurclient.eventbus.handler.InexactEventHandler;
import org.purpurclient.gui.clickgui.ModuleClickGuiScreen;
import org.purpurclient.module.ModuleManager;
import org.purpurclient.setting.option.Option;
import org.purpurclient.util.BleachLogger;
import org.purpurclient.util.BleachPlayerManager;
import org.purpurclient.util.Watermark;
import org.purpurclient.util.io.BleachFileHelper;
import org.purpurclient.util.io.BleachFileMang;
import org.purpurclient.util.io.BleachJsonHelper;
import org.purpurclient.util.io.BleachOnlineMang;

public class PurpurClient implements ModInitializer {

	private static PurpurClient instance = null;

	public static final String VERSION = "";
	public static final int INTVERSION = 40;
	public static Watermark watermark;

	public static BleachEventBus eventBus;
	public static BleachPlayerManager playerMang;

	private static CompletableFuture<JsonObject> updateJson;

//	private BleachFileMang bleachFileManager;

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

		//this.eventBus = new EventBus();
		//this.bleachFileManager = new BleachFileMang();
		BleachFileMang.init();

		BleachFileHelper.readOptions();

		if (Option.PLAYERLIST_SHOW_AS_BH_USER.getValue()) {
			playerMang.startPinger();
		}

		if (Option.GENERAL_CHECK_FOR_UPDATES.getValue()) {
			updateJson = BleachOnlineMang.getResourceAsync("update/" + SharedConstants.getGameVersion().getName().replace(' ', '_') + ".json", BodyHandlers.ofString())
					.thenApply(s -> BleachJsonHelper.parseOrNull(s, JsonObject.class));
		}

		JsonElement mainMenu = BleachFileHelper.readMiscSetting("customTitleScreen");
		if (mainMenu != null && !mainMenu.getAsBoolean()) {
			//			BleachTitleScreen.customTitleScreen = false;
		}

		//		BleachLogger.logger.log(Level.INFO, "Loaded PurpurClient (Phase 1) in %d ms.", System.currentTimeMillis() - initStartTime);


	}

	// Phase 2
	// Called after most of the game has been initialized in MixinMinecraftClient so all game resources can be accessed
	public void postInit() {
		long initStartTime = System.currentTimeMillis();
		ModuleManager.loadModules(this.getClass().getClassLoader().getResourceAsStream("purpurclient.modules.json"));
		BleachFileHelper.readModules();

		// TODO: move ClickGui and UI to phase 1
		ModuleClickGuiScreen.INSTANCE.initWindows();
		BleachFileHelper.readClickGui();
		BleachFileHelper.readUI();

		//		CommandManager.loadCommands(this.getClass().getClassLoader().getResourceAsStream("purpurclient.commands.json"));
		//		CommandSuggestor.start();

		BleachFileHelper.startSavingExecutor();
		ModuleManager.getModule("Dev").setEnabled(true);

//		BleachLogger.logger.log(Level.INFO, "Loaded PurpurClient (Phase 2) in %d ms.", System.currentTimeMillis() - initStartTime);

	}
}
