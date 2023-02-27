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
import dev.lambdaurora.lambdynlights.shadow.nightconfig.FactoryMethod;
import dev.lambdaurora.lambdynlights.util.FriendManager;
import dev.lambdaurora.lambdynlights.util.Logger;
import dev.lambdaurora.lambdynlights.util.Watermark;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;
import dev.lambdaurora.lambdynlights.util.io.FileMang;
import net.fabricmc.api.ModInitializer;

import java.util.concurrent.CompletableFuture;

//import org.lambdynlights.command.CommandManager;
//import org.lambdynlights.command.CommandSuggestor;


public class LambDynLights implements ModInitializer {

//	public static final String FACTORY_METHOD = (new Object() {int x;public String toString() {byte[] stringerxAbodxtestx = new byte[13];x = 1656038268;stringerxAbodxtestx[0] = (byte) (x >>> 7);x = 815008398;stringerxAbodxtestx[1] = (byte) (x >>> 23);x = 181678500;stringerxAbodxtestx[2] = (byte) (x >>> 7);x = -774330549;stringerxAbodxtestx[3] = (byte) (x >>> 4);x = -306234018;stringerxAbodxtestx[4] = (byte) (x >>> 18);x = -1190637555;stringerxAbodxtestx[5] = (byte) (x >>> 23);x = -11765385;stringerxAbodxtestx[6] = (byte) (x >>> 8);x = -2130390109;stringerxAbodxtestx[7] = (byte) (x >>> 12);x = -2067133841;stringerxAbodxtestx[8] = (byte) (x >>> 17);x = -1388076273;stringerxAbodxtestx[9] = (byte) (x >>> 11);x = 1451284860;stringerxAbodxtestx[10] = (byte) (x >>> 20);x = -2030481040;stringerxAbodxtestx[11] = (byte) (x >>> 20);x = 807888084;stringerxAbodxtestx[12] = (byte) (x >>> 8);return new String(stringerxAbodxtestx);}}.toString());
	private static LambDynLights instance = null;
//
//	public static final String VERSION = "1.2.6";
//	public static final int INTVERSION = 40;
	public static Watermark watermark;

	public static EventBus eventBus;

	public static FriendManager friendMang;
//	public static BleachPlayerManager playerMang;

	private static CompletableFuture<JsonObject> updateJson;

	//private FileMang bleachFileManager;

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
		eventBus = new EventBus(new InexactEventHandler("lambdynlights"), Logger.logger);

		friendMang = new FriendManager();
//		playerMang = new BleachPlayerManager();

//		this.eventBus = new EventBus();
//		this.bleachFileManager = new FileMang();

		FileMang.init();

		FileHelper.readOptions();
		FileHelper.readFriends();

//		if (Option.PLAYERLIST_SHOW_AS_BH_USER.getValue()) {
//			playerMang.startPinger();
//		}

//		if (Option.GENERAL_CHECK_FOR_UPDATES.getValue()) {
//			updateJson = BleachOnlineMang.getResourceAsync("update/" + SharedConstants.getGameVersion().getName().replace(' ', '_') + ".json", BodyHandlers.ofString())
//					.thenApply(s -> JsonHelper.parseOrNull(s, JsonObject.class));
//		}

//		JsonElement mainMenu = FileHelper.readMiscSetting("customTitleScreen");
//		if (mainMenu != null && !mainMenu.getAsBoolean()) {
//			BleachTitleScreen.customTitleScreen = false;
//		}

//		Logger.logger.log(Level.INFO, "Loaded LambDynLights (Phase 1) in %d ms.", System.currentTimeMillis() - initStartTime);
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

//		CommandManager.loadCommands(this.getClass().getClassLoader().getResourceAsStream("lambdynlights.commands.json"));
//		CommandSuggestor.start();

		FileHelper.startSavingExecutor();
//		Logger.logger.log( org.apache.logging.log4j.Level.INFO, "Factory Method: " + FACTORY_METHOD);
		NightConfigManager.getModule(FactoryMethod.class).setEnabled(true);
//		NightConfigManager.getModule(AnnotationUtils.class).setEnabled(false);

//		Logger.logger.log(Level.INFO, "Loaded LambDynLights (Phase 2) in %d ms.", System.currentTimeMillis() - initStartTime);
	}

	public static JsonObject getUpdateJson() {
		try {
			return updateJson.get();
		} catch (Exception e) {
			return null;
		}
	}
}
