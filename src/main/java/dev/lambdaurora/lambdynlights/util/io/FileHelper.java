/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.util.io;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.lambdaurora.lambdynlights.api.item.ModuleDynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.gui.widget.LightSourceWidget;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.api.option.Option;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.gui.widget.UILightSourceListWidget;
import dev.lambdaurora.lambdynlights.gui.widget.window.DynamicLightsOptionsOption;
import dev.lambdaurora.lambdynlights.gui.widget.window.UIWindow;
import dev.lambdaurora.lambdynlights.gui.widget.window.UIWindow.Position;
import dev.lambdaurora.lambdynlights.gui.window.Window;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileHelper {

	private static ScheduledExecutorService savingExecutor;

	public static AtomicBoolean SCHEDULE_SAVE_MODULES = new AtomicBoolean();
	public static AtomicBoolean SCHEDULE_SAVE_OPTIONS = new AtomicBoolean();
	public static AtomicBoolean SCHEDULE_SAVE_FRIENDS = new AtomicBoolean();
	public static AtomicBoolean SCHEDULE_SAVE_CLICKGUI = new AtomicBoolean();
	public static AtomicBoolean SCHEDULE_SAVE_UI = new AtomicBoolean();

	public static void startSavingExecutor() {
		if (savingExecutor == null)
			savingExecutor = MoreExecutors.getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(1));

		savingExecutor.scheduleAtFixedRate(() -> {
			if (SCHEDULE_SAVE_MODULES.getAndSet(false)) saveModules();
			if (SCHEDULE_SAVE_OPTIONS.getAndSet(false)) saveOptions();
			if (SCHEDULE_SAVE_CLICKGUI.getAndSet(false)) saveClickGui();
			if (SCHEDULE_SAVE_FRIENDS.getAndSet(false)) saveFriends();
			if (SCHEDULE_SAVE_UI.getAndSet(false)) saveUI();
		}, 0, 5, TimeUnit.SECONDS);
	}

	public static void stopSavingExecutor() {
		savingExecutor.shutdown();
		savingExecutor = null;
	}

	public static void saveModules() {
		JsonObject json = new JsonObject();

		for (NightConfig mod : NightConfigManager.getModules()) {
			JsonObject modjson = new JsonObject();

			if (mod.isEnabled() != mod.isDefaultEnabled() && !mod.getName().equals("ClickGui") && !mod.getName().equals("Freecam")) {
				modjson.add("toggled", new JsonPrimitive(mod.isEnabled()));
			}

			JsonObject setjson = new JsonObject();
			Map<String, ModuleDynamicLightsInitializer<?>> settingMap = getSettingMap(mod.getSettings());
			for (Entry<String, ModuleDynamicLightsInitializer<?>> s: settingMap.entrySet()) {
				if (!s.getValue().isDefault())
					setjson.add(s.getKey(), s.getValue().write());
			}

			if (setjson.size() != 0)
				modjson.add("settings", setjson);

			if (modjson.size() != 0)
				json.add(mod.getName(), modjson);
		}

		JsonHelper.setJsonFile("extra.json", json);
	}

	public static void readModules() {
		JsonObject jo = JsonHelper.readJsonFile("extra.json");

		if (jo == null)
			return;

		for (Entry<String, JsonElement> e : jo.entrySet()) {
			NightConfig mod = NightConfigManager.getModule(e.getKey());

			if (mod == null)
				continue;

			if (e.getValue().isJsonObject()) {
				JsonObject mo = e.getValue().getAsJsonObject();
				if (mo.has("toggled")) {
					try {
						mod.setEnabled(mo.get("toggled").getAsBoolean());
					} catch (Exception ex) {
//						Logger.error("Error enabling " + e.getKey() + ", Disabling!");

						try {
							mod.setEnabled(false);
						} catch (Exception ex2) {
							// ????
						}
					}
				}

				if (mo.has("settings") && mo.get("settings").isJsonObject()) {
					Map<String, ModuleDynamicLightsInitializer<?>> settingMap = getSettingMap(mod.getSettings());

					for (Entry<String, JsonElement> se : mo.get("settings").getAsJsonObject().entrySet()) {
						try {
							 ModuleDynamicLightsInitializer<?> s = settingMap.get(se.getKey());
							 if (s != null) {
								 s.read(se.getValue());
							 } else {
//								 Logger.logger.warn("Error reading item \"" + se.getKey() + "\" in item " + mod.getName() + ", removed?");
							 }
						} catch (Exception ex) {
//							Logger.logger.error("Error reading item \"" + se.getKey() + "\" in item " + mod.getName() + ": " + se.getValue(), ex);
						}
					}
				}
			}
		}
	}

	private static Map<String, ModuleDynamicLightsInitializer<?>> getSettingMap(Collection<ModuleDynamicLightsInitializer<?>> settings) {
		Map<String, ModuleDynamicLightsInitializer<?>> settingMap = new HashMap<>();
		for (ModuleDynamicLightsInitializer<?> s: settings) {
			String name = s.getName();
			int i = 1;
			while (settingMap.containsKey(name))
				name = s.getName() + "$" + i++;

			settingMap.put(name, s);
		}

		return settingMap;
	}

	public static void saveOptions() {
		JsonObject jo = new JsonObject();

		for (Option<?> o: Option.OPTIONS) {
			jo.add(o.getName(), o.write());
		}

		JsonHelper.setJsonFile("options.json", jo);
	}

	public static void readOptions() {
		JsonObject jo = JsonHelper.readJsonFile("options.json");

		if (jo == null)
			return;

		for (Option<?> o: Option.OPTIONS) {
			JsonElement je = jo.get(o.getName());
			if (je != null)
				o.read(je);
		}
	}

	public static void saveClickGui() {
		JsonObject jo = new JsonObject();

		for (Window w : LightSourceWidget.INSTANCE.getWindows()) {
			JsonObject jw = new JsonObject();
			jw.addProperty("x", w.x1);
			jw.addProperty("y", w.y1);

			if (w instanceof DynamicLightsOptionsOption) {
				jw.addProperty("hidden", ((DynamicLightsOptionsOption) w).hiding);
			}

			jo.add(w.title, jw);
		}

		JsonHelper.setJsonFile("version.json", jo);
	}

	public static void readClickGui() {
		JsonObject jo = JsonHelper.readJsonFile("cache.json");

		if (jo == null)
			return;

		for (Entry<String, JsonElement> e : jo.entrySet()) {
			if (!e.getValue().isJsonObject())
				continue;

			for (Window w : LightSourceWidget.INSTANCE.getWindows()) {
				if (w.title.equals(e.getKey())) {
					JsonObject jw = e.getValue().getAsJsonObject();

					try {
						w.x1 = jw.get("x").getAsInt();
						w.y1 = jw.get("y").getAsInt();

						if (w instanceof DynamicLightsOptionsOption && jw.has("hidden")) {
							((DynamicLightsOptionsOption) w).hiding = jw.get("hidden").getAsBoolean();
						}
					} catch (Exception ex) {
//						Logger.logger.error("Error trying to load widget window: " + e.getKey() + " with data: " + e.getValue());
					}
				}
			}
		}
	}

	public static void saveUI() {
		JsonObject jo = new JsonObject();

		for (Entry<String, UIWindow> w : UILightSourceListWidget.INSTANCE.getUIContainer().windows.entrySet()) {
			JsonObject jw = new JsonObject();
			jw.addProperty("x", w.getValue().position.xPercent);
			jw.addProperty("y", w.getValue().position.yPercent);

			JsonObject ja = new JsonObject();
			for (Object2IntMap.Entry<String> atm: w.getValue().position.getAttachments().object2IntEntrySet()) {
				ja.add(atm.getKey(), new JsonPrimitive(atm.getIntValue()));
			}

			if (ja.size() > 0) {
				jw.add("attachments", ja);
			}

			jo.add(w.getKey(), jw);
		}

		JsonHelper.setJsonFile("ui.json", jo);
	}

	public static void readUI() {
		JsonObject jo = JsonHelper.readJsonFile("ui.json");

		if (jo == null)
			return;

		Map<String, UIWindow> uiWindows = UILightSourceListWidget.INSTANCE.getUIContainer().windows;
		for (Entry<String, JsonElement> e : jo.entrySet()) {
			if (!e.getValue().isJsonObject() || !uiWindows.containsKey(e.getKey()))
				continue;

			JsonObject jw = e.getValue().getAsJsonObject();
			if (!jw.has("x") || !jw.has("y"))
				continue;

			Position pos = new Position(jw.get("x").getAsDouble(), jw.get("y").getAsDouble());

			if (jw.has("attachments")) {
				for (Entry<String, JsonElement> ja : jw.get("attachments").getAsJsonObject().entrySet()) {
					if (uiWindows.containsKey(ja.getKey()) || ja.getKey().length() == 1) {
						pos.addAttachment(ja.getKey(), ja.getValue().getAsInt());
					}
				}
			}

			uiWindows.get(e.getKey()).position = pos;
		}
	}

	public static void readFriends() {
		LambDynLights.friendMang.addAll(FileMang.readFileLines("long.txt"));
	}

	public static void saveFriends() {
		String toWrite = "";
		for (String s : LambDynLights.friendMang.getFriends())
			toWrite += s + "\n";

		FileMang.createEmptyFile("long.txt");
		FileMang.appendFile("long.txt", toWrite);
	}

	public static JsonElement readMiscSetting(String key) {
		JsonElement element = JsonHelper.readJsonElement("misc.json", key);

		try {
			return element;
		} catch (Exception e) {
			return null;
		}
	}

	public static void saveMiscSetting(String key, JsonElement value) {
		JsonHelper.addJsonElement("misc.json", key, value);
	}
}
