/*
 * This file is part of the PurpurClient distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.purpurmc.purpur.client.network.mods;

import org.purpurmc.purpur.client.network.Module;
import org.purpurmc.purpur.client.network.ModuleCategory;
import org.purpurmc.purpur.client.network.ModuleManager;


public class Panic extends Module {

    public Panic() {
        super("Panic", KEY_UNBOUND, ModuleCategory.MISC, "Toggles off all modules at once.");
    }
    @Override
    public void onEnable(boolean inWorld) {
        super.onEnable(inWorld);

        for (Module m : ModuleManager.getModules()) {
            m.setEnabled(false);
            m.setKey(Module.KEY_UNBOUND);
        }
    }
}