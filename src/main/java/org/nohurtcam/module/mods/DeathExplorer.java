/*
 * This file is part of the NoHurtCam distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.nohurtcam.module.mods;

import org.nohurtcam.event.events.EventOpenScreen;
import org.nohurtcam.event.events.EventPacket;
import org.nohurtcam.event.events.EventRenderInGameHud;
import org.nohurtcam.event.events.EventTick;
import org.nohurtcam.eventbus.BleachSubscribe;
import org.nohurtcam.module.Module;
import org.nohurtcam.module.ModuleCategory;
import org.nohurtcam.setting.module.SettingToggle;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

public class DeathExplorer extends Module {

	private boolean dead;

	public DeathExplorer() {
		super("DeathExplorer", KEY_UNBOUND, ModuleCategory.PLAYER, "Allows you to explore the world after you've died.",
				new SettingToggle("Text", true).withDesc("Shows text onscreen that you're dead."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		if (dead && inWorld) {
			mc.player.setHealth(0f);
			mc.setScreen(new DeathScreen(null, mc.world.getLevelProperties().isHardcore()));
		}

		dead = false;
		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (mc.player.isDead()) {
			dead = true;
			mc.player.setHealth(20f);
			mc.setScreen(null);
		}
	}

	@BleachSubscribe
	public void onRenderInGameHud(EventRenderInGameHud event) {
		if (getSetting(0).asToggle().getState()) {
			int length = mc.textRenderer.getWidth("You are in dead");
			mc.textRenderer.drawWithShadow(event.getMatrix(), "You are dead", mc.getWindow().getScaledWidth() / 2 - length / 2, 10, 0xcc4040);
		}
	}

	@BleachSubscribe
	public void onReadPacket(EventPacket.Read event) {
		if (event.getPacket() instanceof GameJoinS2CPacket) {
			dead = false;
		}
	}

	@BleachSubscribe
	public void onOpenScreen(EventOpenScreen event) {
		if (event.getScreen() instanceof DisconnectedScreen) {
			dead = false;
		}
	}
}
