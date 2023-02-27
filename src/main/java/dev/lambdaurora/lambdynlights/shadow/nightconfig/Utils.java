package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import net.minecraft.entity.Entity;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.world.EntityUtils;

public class Utils extends NightConfig {

    public Utils() {
        super("Hitboxes", KEY_UNBOUND, NightConfigCategory.COMBAT, "Expands player's hitboxes",
                new DynamicLightsInitializerSlider("Size", 0.1, 1, 0.3, 1));
    }

    public double getEntityValue(Entity entity) {
        if (EntityUtils.isPlayer(entity))
            return getSetting(0).asSlider().getValue();
        return isEnabled() ? getSetting(0).asSlider().getValue() : 0.1;
    }
}