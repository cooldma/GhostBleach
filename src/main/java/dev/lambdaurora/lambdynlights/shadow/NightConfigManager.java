/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.io.IOUtils;
import dev.lambdaurora.lambdynlights.util.collections.NameableStorage;
import dev.lambdaurora.lambdynlights.util.io.JsonHelper;
import org.lwjgl.glfw.GLFW;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class NightConfigManager {

	private static final NameableStorage<NightConfig> MODULES = new NameableStorage<>(NightConfig::getName);

	public static void loadModules(InputStream jsonInputStream) {
		InputStreamReader inputReader = new InputStreamReader(jsonInputStream, StandardCharsets.UTF_8);

		try {
			ModuleListJson json = JsonHelper.GSON.fromJson(inputReader, ModuleListJson.class);

			for (String moduleString : json.getModules()) {
				try {
					Class<?> moduleClass = Class.forName(String.format("%s.%s", json.getPackage(), moduleString));

					if (NightConfig.class.isAssignableFrom(moduleClass)) {
						try {
							NightConfig nightConfig = (NightConfig) moduleClass.getConstructor().newInstance();

							loadModule(nightConfig);
						} catch (Exception exception) {
//							Logger.logger.error("Failed to load item %s: could not instantiate.", moduleClass);
							exception.printStackTrace();
						}
					} else {
//						Logger.logger.error("Failed to load item %s: not a descendant of NightConfig.", moduleClass);
					}
				} catch (Exception exception) {
//					Logger.logger.error("Failed to load item %s.", moduleString);
					exception.printStackTrace();
				}
			}
		} finally {
			IOUtils.closeQuietly(inputReader);
		}
	}

	public static void loadModule(NightConfig nightConfig) {
		if (!MODULES.add(nightConfig)) {

		}
//			Logger.logger.error("Failed to load item %s: a item with this name is already loaded.", item.getName());
	}

	public static Iterable<NightConfig> getModules() {
		return MODULES.values();
	}

	public static NightConfig getModule(String name) {
		return MODULES.get(name);
	}
	
	public static <M extends NightConfig> M getModule(Class<M> class_) {
		return MODULES.get(class_);
	}

	public static List<NightConfig> getModulesInCat(NightConfigCategory cat) {
		return MODULES.stream().filter(m -> m.getCategory().equals(cat)).collect(Collectors.toList());
	}

	public static void handleKey(int key) {
		if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3)) {
			for (NightConfig m: getModules()) {
				if (m.getKey() == key) {
					m.toggle();
				}
			}
		}
	}

	private static class ModuleListJson {

		@SerializedName("package")
		private String packageName;

		@SerializedName("classes")
		private List<String> modules;

		public String getPackage() {
			return this.packageName;
		}

		public List<String> getModules() {
			return this.modules;
		}
	}
}
