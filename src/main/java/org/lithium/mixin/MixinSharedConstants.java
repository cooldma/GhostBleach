/*
 * This file is part of the Lithium distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.lithium.mixin;

//import org.lithium.module.mods.NoKeyBlock;
import org.spongepowered.asm.mixin.Mixin;

        import net.minecraft.SharedConstants;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {

//	@Overwrite
//	public static boolean isValidChar(char chr) {
//		Module noKeyBlock = ModuleManager.getModule(NoKeyBlock.class);
//
//		if (!noKeyBlock.isEnabled()) {
//			return chr != 167 && chr >= ' ' && chr != 127;
//		}
//
//		return (noKeyBlock.getSetting(0).asToggle().getState() || chr != 167)
//				&& (noKeyBlock.getSetting(1).asToggle().getState() || chr >= ' ')
//				&& (noKeyBlock.getSetting(2).asToggle().getState() || chr != 127);
//	}
}
