/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam.event.events;

import org.nohurtcam.event.Event;

import net.minecraft.client.util.math.MatrixStack;

public class EventRenderInGameHud extends Event {

	private MatrixStack matrices;

	public EventRenderInGameHud(MatrixStack matrices) {
		this.matrices = matrices;
	}

	public MatrixStack getMatrix() {
		return matrices;
	}
}
