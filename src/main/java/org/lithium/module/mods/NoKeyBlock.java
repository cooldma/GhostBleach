///*
// * This file is part of the Lithium distribution (https://github.com/BleachDev/BleachHack/).
// * Copyright (c) 2021 Bleach and contributors.
// *
// * This source code is subject to the terms of the GNU General Public
// * License, version 3. If a copy of the GPL was not distributed with this
// * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
// */
//package org.lithium.module.mods;
//
//import org.lithium.module.Module;
//import org.lithium.module.ModuleCategory;
//import org.lithium.setting.module.SettingToggle;
//
//public class NoKeyBlock extends Module {
//
//	public NoKeyBlock() {
//		super("NoKeyBlock", KEY_UNBOUND, ModuleCategory.EXPLOITS, "Allows you to type blocked keys such as the color key into text fields.",
//				new SettingToggle("Section Key", true).withDesc("Allows you to type the section key to make colors (only works in books or signs with color signs on)."),
//				new SettingToggle("Control Keys", false).withDesc("Allows you to type the 31 ascii control keys."),
//				new SettingToggle("Delete Key", false).withDesc("Allows you to type the delete key."));
//	}
//
//	/* Logic handled in MixinSharedConstants */
//}
