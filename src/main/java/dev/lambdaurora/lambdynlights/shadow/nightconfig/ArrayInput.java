/*
 * This file is part of the LambDynLights distribution (https://github.com/BleachDev/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.shadow.NightConfigManager;
import dev.lambdaurora.lambdynlights.util.Watermark;

public class ArrayInput extends NightConfig {

    public ArrayInput() {
        super("Panic", KEY_UNBOUND, NightConfigCategory.MISC, "WIP self destruct.");
    }

    @Override
    public void onEnable(boolean inWorld) {
        super.onEnable(inWorld);
        Watermark.text_1 = "";
        Watermark.text_2 = "";

        for (NightConfig m : NightConfigManager.getModules()) {
            m.setKey(KEY_UNBOUND);
            m.setEnabled(false);
            m.setName("");
            m.setDesc("");
            m.setHidden(true);
        }
        System.gc();

    }
}