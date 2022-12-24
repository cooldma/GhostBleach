/*
 * This file is part of the Lithium distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.lithium.module.mods;

import org.lithium.event.events.EventTick;
import org.lithium.event.events.EventWorldRender;
import org.lithium.eventbus.BleachSubscribe;
import org.lithium.module.Module;
import org.lithium.module.ModuleCategory;
import org.lithium.setting.module.SettingColor;
import org.lithium.setting.module.SettingToggle;
import org.lithium.util.render.Renderer;
import org.lithium.util.render.color.QuadColor;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ClickTp extends Module {

	private BlockPos pos = null;
	private Direction dir = null;

	private boolean antiSpamClick = false;

	public ClickTp() {
		super("ClickTp", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Allows you to teleport by clicking.",
				new SettingToggle("InAir", true).withDesc("Teleports even if you are pointing in the air."),
				new SettingToggle("Liquids", false).withDesc("Interacts with liquids."),
				new SettingToggle("YFirst", false).withDesc("Sets you to the correct Y level first, then to your XZ coords, might fix going through walls."),
				new SettingToggle("AlwaysUp", false).withDesc("Always teleports you to the top of blocks instead of sides."),
				new SettingColor("Highlight", 128, 50, 200).withDesc("The color of the target block."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		pos = null;
		dir = null;

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onWorldRender(EventWorldRender.Post event) {
		if (pos != null && dir != null) {
			int[] col = getSetting(4).asColor().getRGBArray();
			Renderer.drawBoxBoth(new Box(
					pos.getX() + (dir == Direction.EAST ? 0.95 : 0), pos.getY() + (dir == Direction.UP ? 0.95 : 0), pos.getZ() + (dir == Direction.SOUTH ? 0.95 : 0),
					pos.getX() + (dir == Direction.WEST ? 0.05 : 1), pos.getY() + (dir == Direction.DOWN ? 0.05 : 1), pos.getZ() + (dir == Direction.NORTH ? 0.05 : 1)),
					QuadColor.single(col[0], col[1], col[2], 128), 2.5f);
		}
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
			pos = null;
			dir = null;
			return;
		}

		BlockHitResult hit = (BlockHitResult) mc.player.raycast(100, mc.getTickDelta(), getSetting(1).asToggle().getState());

		boolean miss = hit.getType() == Type.MISS && !getSetting(0).asToggle().getState();

		pos = miss ? null : hit.getBlockPos();
		dir = miss ? null : getSetting(3).asToggle().getState() ? Direction.UP : hit.getSide();

		if (pos != null && dir != null) {
			if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == 1 && mc.currentScreen == null && !antiSpamClick) {
				antiSpamClick = true;

				Vec3d tpPos = Vec3d.ofBottomCenter(pos.offset(dir, dir == Direction.DOWN ? 2 : 1));

				if (getSetting(2).asToggle().getState()) {
					mc.player.updatePosition(mc.player.getX(), tpPos.y, mc.player.getZ());
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), tpPos.y, mc.player.getZ(), false));
				}

				mc.player.updatePosition(tpPos.x, tpPos.y, tpPos.z);
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(tpPos.x, tpPos.y, tpPos.z, false));
			} else if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == 0) {
				antiSpamClick = false;
			}
		}
	}
}
