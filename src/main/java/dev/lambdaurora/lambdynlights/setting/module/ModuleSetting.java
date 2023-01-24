/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.setting.module;

import java.util.function.UnaryOperator;

import dev.lambdaurora.lambdynlights.gui.clickgui.window.ClickGuiWindow.Tooltip;
import dev.lambdaurora.lambdynlights.setting.Setting;
import dev.lambdaurora.lambdynlights.setting.SettingDataHandler;
import dev.lambdaurora.lambdynlights.gui.clickgui.window.ModuleWindow;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;

import net.minecraft.client.util.math.MatrixStack;

public abstract class ModuleSetting<T> extends Setting<T> {

	public ModuleSetting(String name, T value, SettingDataHandler<T> handler) {
		super(name, "", value, handler);
	}
	
	public ModuleSetting(String name, T value, UnaryOperator<T> defaultValue, SettingDataHandler<T> handler) {
		super(name, "", value, defaultValue, handler);
	}

	public SettingMode asMode() {
		try {
			return (SettingMode) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}

	public SettingToggle asToggle() {
		try {
			return (SettingToggle) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}

	public SettingSlider asSlider() {
		try {
			return (SettingSlider) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}

	public SettingColor asColor() {
		try {
			return (SettingColor) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}
	
	public SettingButton asButton() {
		try {
			return (SettingButton) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <E> SettingList<E> asList(Class<E> itemClass) {
		try {
			return (SettingList<E>) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}

	public SettingRotate asRotate() {
		try {
			return (SettingRotate) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}
	
	public SettingKey asBind() {
		try {
			return (SettingKey) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing setting: " + this);
		}
	}

	public Tooltip getTooltip(ModuleWindow window, int x, int y, int len) {
		return new Tooltip(x + len + 2, y, getTooltip());
	}
	
	@Override
	public void setValue(T value) {
		super.setValue(value);
		FileHelper.SCHEDULE_SAVE_MODULES.set(true);
	}

	public abstract void render(ModuleWindow window, MatrixStack matrices, int x, int y, int len);

	public abstract int getHeight(int len);
}
