/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;

import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

//import org.bleachhack.command.CommandManager;
//import org.bleachhack.command.CommandSuggestor;
import org.nohurtcam.eventbus.BleachEventBus;
import org.nohurtcam.eventbus.handler.InexactEventHandler;
import org.nohurtcam.gui.clickgui.ModuleClickGuiScreen;
import org.nohurtcam.module.DiscordMessage;
import org.nohurtcam.module.ModuleManager;
import org.nohurtcam.setting.option.Option;
import org.nohurtcam.util.BleachLogger;
import org.nohurtcam.util.BleachPlayerManager;
import org.nohurtcam.util.FriendManager;
import org.nohurtcam.util.Watermark;
import org.nohurtcam.util.io.BleachFileHelper;
import org.nohurtcam.util.io.BleachFileMang;
import org.nohurtcam.util.io.BleachJsonHelper;
import org.nohurtcam.util.io.BleachOnlineMang;

//import static org.bleachhack.module.DiscordMessage.sendDiscordMessage;

public class NoHurtCam implements ModInitializer {

	private static NoHurtCam instance = null;

	public static final String VERSION = "";
	public static final int INTVERSION = 40;
	public static Watermark watermark;

	public static BleachEventBus eventBus;

	public static FriendManager friendMang;
	public static BleachPlayerManager playerMang;

	private static CompletableFuture<JsonObject> updateJson;

	//private BleachFileMang bleachFileManager;

	public static NoHurtCam getInstance() {
		return instance;
	}

	private DiscordMessage discordMessage;

	public NoHurtCam() {
		if (instance != null) {
			throw new RuntimeException("A NoHurtCam instance already exists.");
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

//		BleachLogger.logger.log(Level.INFO, "Loaded NoHurtCam (Phase 1) in %d ms.", System.currentTimeMillis() - initStartTime);

		discordMessage = new DiscordMessage();
		discordMessage.init();


	}

	// Phase 2
	// Called after most of the game has been initialized in MixinMinecraftClient so all game resources can be accessed
	public void postInit() {
		long initStartTime = System.currentTimeMillis();

		ModuleManager.loadModules(this.getClass().getClassLoader().getResourceAsStream("nohurtcam.modules.json"));
		BleachFileHelper.readModules();

		// TODO: move ClickGui and UI to phase 1
		ModuleClickGuiScreen.INSTANCE.initWindows();
		BleachFileHelper.readClickGui();
		BleachFileHelper.readUI();

//		CommandManager.loadCommands(this.getClass().getClassLoader().getResourceAsStream("nohurtcam.commands.json"));
//		CommandSuggestor.start();

		BleachFileHelper.startSavingExecutor();

//		BleachLogger.logger.log(Level.INFO, "Loaded NoHurtCam (Phase 2) in %d ms.", System.currentTimeMillis() - initStartTime);
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
