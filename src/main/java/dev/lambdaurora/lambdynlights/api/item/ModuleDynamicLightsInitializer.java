/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.api.item;

import java.util.function.UnaryOperator;

import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.gui.widget.window.DynamicLightsOptionsOption.Tooltip;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.gui.widget.window.InnerBackground;
import dev.lambdaurora.lambdynlights.util.io.FileHelper;

import net.minecraft.client.util.math.MatrixStack;

public abstract class ModuleDynamicLightsInitializer<T> extends DynamicLightsInitializer<T> {

	public ModuleDynamicLightsInitializer(String name, T value, DynamicLightHandlers<T> handler) {
		super(name, "", value, handler);
	}
	
	public ModuleDynamicLightsInitializer(String name, T value, UnaryOperator<T> defaultValue, DynamicLightHandlers<T> handler) {
		super(name, "", value, defaultValue, handler);
	}

	public DynamicLightsInitializerMode asMode() {
		try {
			return (DynamicLightsInitializerMode) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}

	public DynamicLightsInitializerToggle asToggle() {
		try {
			return (DynamicLightsInitializerToggle) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}

	public DynamicLightsInitializerSlider asSlider() {
		try {
			return (DynamicLightsInitializerSlider) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}

	public DynamicLightsInitializerColor asColor() {
		try {
			return (DynamicLightsInitializerColor) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}
	
	public DynamicLightsInitializerButton asButton() {
		try {
			return (DynamicLightsInitializerButton) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <E> DynamicLightsInitializerList<E> asList(Class<E> itemClass) {
		try {
			return (DynamicLightsInitializerList<E>) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}

	public DynamicLightsInitializerRotate asRotate() {
		try {
			return (DynamicLightsInitializerRotate) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}
	
	public DynamicLightsInitializerKey asBind() {
		try {
			return (DynamicLightsInitializerKey) this;
		} catch (Exception e) {
			throw new ClassCastException("Execption parsing item: " + this);
		}
	}

	public Tooltip getTooltip(InnerBackground window, int x, int y, int len) {
		return new Tooltip(x + len + 2, y, getTooltip());
	}
	
	@Override
	public void setValue(T value) {
		super.setValue(value);
		FileHelper.SCHEDULE_SAVE_MODULES.set(true);
	}

	public abstract void render(InnerBackground window, MatrixStack matrices, int x, int y, int len);

	public abstract int getHeight(int len);
}
