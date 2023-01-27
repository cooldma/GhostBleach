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

import dev.lambdaurora.lambdynlights.eventbus.BleachEventBus;
import dev.lambdaurora.lambdynlights.eventbus.handler.InexactEventHandler;
import dev.lambdaurora.lambdynlights.gui.clickgui.ModuleClickGuiScreen;
import dev.lambdaurora.lambdynlights.module.ModuleManager;
import dev.lambdaurora.lambdynlights.util.BleachLogger;
import dev.lambdaurora.lambdynlights.util.BleachPlayerManager;
import dev.lambdaurora.lambdynlights.util.FriendManager;
import dev.lambdaurora.lambdynlights.util.Watermark;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;
import dev.lambdaurora.lambdynlights.util.io.BleachFileMang;
import net.fabricmc.api.ModInitializer;

import java.util.concurrent.CompletableFuture;

//import org.lambdynlights.command.CommandManager;
//import org.lambdynlights.command.CommandSuggestor;


public class LambDynLights implements ModInitializer {

	private static LambDynLights instance = null;

	public static final String VERSION = "1.2.6";
	public static final int INTVERSION = 40;
	public static Watermark watermark;

	public static BleachEventBus eventBus;

	public static FriendManager friendMang;
	public static BleachPlayerManager playerMang;

	private static CompletableFuture<JsonObject> updateJson;

	//private BleachFileMang bleachFileManager;

	public static LambDynLights getInstance() {
		return instance;
	}

	public LambDynLights() {
//		if (instance != null) {
//			throw new RuntimeException("A LambDynLights instance already exists.");
//		}
	}

	// Phase 1
	// TODO: base-rewrite
	@Override
	public void onInitialize() {
		long initStartTime = System.currentTimeMillis();

		instance = this;
		watermark = new Watermark();
		eventBus = new BleachEventBus(new InexactEventHandler("lambdynlights"), BleachLogger.logger);

		friendMang = new FriendManager();
		playerMang = new BleachPlayerManager();

//		this.eventBus = new EventBus();
//		this.bleachFileManager = new BleachFileMang();

		BleachFileMang.init();

		FileHelper.readOptions();
		FileHelper.readFriends();

//		if (Option.PLAYERLIST_SHOW_AS_BH_USER.getValue()) {
//			playerMang.startPinger();
//		}

//		if (Option.GENERAL_CHECK_FOR_UPDATES.getValue()) {
//			updateJson = BleachOnlineMang.getResourceAsync("update/" + SharedConstants.getGameVersion().getName().replace(' ', '_') + ".json", BodyHandlers.ofString())
//					.thenApply(s -> BleachJsonHelper.parseOrNull(s, JsonObject.class));
//		}

//		JsonElement mainMenu = FileHelper.readMiscSetting("customTitleScreen");
//		if (mainMenu != null && !mainMenu.getAsBoolean()) {
//			BleachTitleScreen.customTitleScreen = false;
//		}

//		BleachLogger.logger.log(Level.INFO, "Loaded LambDynLights (Phase 1) in %d ms.", System.currentTimeMillis() - initStartTime);
	}

	// Phase 2
	// Called after most of the game has been initialized in MixinMinecraftClient so all game resources can be accessed
	public void postInit() {
		long initStartTime = System.currentTimeMillis();

		ModuleManager.loadModules(this.getClass().getClassLoader().getResourceAsStream("lambdaurora.modules.json"));
		FileHelper.readModules();

		// TODO: move ClickGui and UI to phase 1
		ModuleClickGuiScreen.INSTANCE.initWindows();
		FileHelper.readClickGui();
		FileHelper.readUI();

//		CommandManager.loadCommands(this.getClass().getClassLoader().getResourceAsStream("lambdynlights.commands.json"));
//		CommandSuggestor.start();

		FileHelper.startSavingExecutor();

//		BleachLogger.logger.log(Level.INFO, "Loaded LambDynLights (Phase 2) in %d ms.", System.currentTimeMillis() - initStartTime);
	}

	public static JsonObject getUpdateJson() {
		try {
			return updateJson.get();
		} catch (Exception e) {
			return null;
		}
	}
}
