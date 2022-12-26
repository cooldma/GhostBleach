package org.lithium.module.mods;

import net.minecraft.entity.Entity;
import org.lithium.module.Module;
import org.lithium.module.ModuleCategory;
import org.lithium.setting.module.SettingSlider;

public class HitBoxes extends Module {

    public HitBoxes() {
        super("HitBoxes", KEY_UNBOUND, ModuleCategory.COMBAT, "Expands a player's hitbox",
                new SettingSlider("Size", 0, 1, 0.5, 2).withDesc("How much the hit box is expanded."));
    }

}
