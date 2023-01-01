package org.purpurclient.module.mods;

import org.purpurclient.module.Module;
import org.purpurclient.module.ModuleCategory;
import org.purpurclient.setting.module.SettingSlider;

public class HitBoxes extends Module {

    public HitBoxes() {
        super("HitBoxes", KEY_UNBOUND, ModuleCategory.COMBAT, "Expands a player's hitbox",
                new SettingSlider("Size", 0, 10, 0.5, 2).withDesc("How much the hit box is expanded."));
    }

    public void onEnable(boolean inWorld) {
        super.onEnable(inWorld);
    }

    public void onDisable(boolean inWorld) {
        super.onDisable(inWorld);
    }



}