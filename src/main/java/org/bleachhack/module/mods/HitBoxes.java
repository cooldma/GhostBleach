package org.bleachhack.module.mods;

import net.minecraft.entity.Entity;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.util.world.EntityUtils;

import static org.bleachhack.module.Module.KEY_UNBOUND;

public class HitBoxes extends Module {

    public HitBoxes() {
        super("Hitboxes", KEY_UNBOUND, ModuleCategory.COMBAT, "Expands player's hitboxes",
                new SettingSlider("hitboxer", 0.1, 1, 0.3, 1).withDesc("how big"));
    }

    public double getEntityValue(Entity entity) {
        if (EntityUtils.isPlayer(entity))
            return getSetting(0).asSlider().getValue();
        return isEnabled() ? getSetting(0).asSlider().getValue() : 0.1;
    }
}