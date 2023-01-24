package dev.lambdaurora.lambdynlights.module.mods;

import net.minecraft.entity.Entity;
import dev.lambdaurora.lambdynlights.module.Module;
import dev.lambdaurora.lambdynlights.module.ModuleCategory;
import dev.lambdaurora.lambdynlights.setting.module.SettingSlider;
import dev.lambdaurora.lambdynlights.util.world.EntityUtils;

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