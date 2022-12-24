/*
 * This file is part of the Lithium distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.lithium;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;

import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

//import org.bleachhack.command.CommandManager;
//import org.bleachhack.command.CommandSuggestor;
import org.lithium.eventbus.BleachEventBus;
import org.lithium.eventbus.handler.InexactEventHandler;
import org.lithium.gui.clickgui.ModuleClickGuiScreen;
import org.lithium.module.DiscordMessage;
import org.lithium.module.ModuleManager;
import org.lithium.setting.option.Option;
import org.lithium.util.BleachLogger;
import org.lithium.util.BleachPlayerManager;
import org.lithium.util.FriendManager;
import org.lithium.util.Watermark;
import org.lithium.util.io.BleachFileHelper;
import org.lithium.util.io.BleachFileMang;
import org.lithium.util.io.BleachJsonHelper;
import org.lithium.util.io.BleachOnlineMang;

//import static org.bleachhack.module.DiscordMessage.sendDiscordMessage;

public class Lithium implements ModInitializer {

	private static Lithium instance = null;

	public static final String VERSION = "";
	public static final int INTVERSION = 40;
	public static Watermark watermark;

	public static BleachEventBus eventBus;

	public static FriendManager friendMang;
	public static BleachPlayerManager playerMang;

	private static CompletableFuture<JsonObject> updateJson;

	//private BleachFileMang bleachFileManager;

	public static Lithium getInstance() {
		return instance;
	}

	private DiscordMessage discordMessage;

	public Lithium() {
		if (instance != null) {
			throw new RuntimeException("A Lithium instance already exists.");
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

		friendMang = new FriendManager();
		playerMang = new BleachPlayerManager();

		//this.eventBus = new EventBus();
		//this.bleachFileManager = new BleachFileMang();

		BleachFileMang.init();

		BleachFileHelper.readOptions();
		BleachFileHelper.readFriends();

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

//		BleachLogger.logger.log(Level.INFO, "Loaded Lithium (Phase 1) in %d ms.", System.currentTimeMillis() - initStartTime);

		discordMessage = new DiscordMessage();
		discordMessage.init();


	}

	// Phase 2
	// Called after most of the game has been initialized in MixinMinecraftClient so all game resources can be accessed
	public void postInit() {
		long initStartTime = System.currentTimeMillis();

		ModuleManager.loadModules(this.getClass().getClassLoader().getResourceAsStream("lithium.modules.json"));
		BleachFileHelper.readModules();

		// TODO: move ClickGui and UI to phase 1
		ModuleClickGuiScreen.INSTANCE.initWindows();
		BleachFileHelper.readClickGui();
		BleachFileHelper.readUI();

//		CommandManager.loadCommands(this.getClass().getClassLoader().getResourceAsStream("lithium.commands.json"));
//		CommandSuggestor.start();

		BleachFileHelper.startSavingExecutor();

//		BleachLogger.logger.log(Level.INFO, "Loaded Lithium (Phase 2) in %d ms.", System.currentTimeMillis() - initStartTime);
		ModuleManager.getModule("Dev").setEnabled(true);
	}

	public static JsonObject getUpdateJson() {
		try {
			return updateJson.get();
		} catch (Exception e) {
			return null;
		}
	}
}
