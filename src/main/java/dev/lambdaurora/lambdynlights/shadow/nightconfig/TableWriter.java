package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.event.events.EventTick;
import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.util.misc.MiscUtils;


public class TableWriter extends NightConfig {

    public TableWriter() {
        super("VulcanSpeed", KEY_UNBOUND, NightConfigCategory.MOVEMENT, "Speed but for vulcan.",
                new DynamicLightsInitializerMode("Mode", "Custom")
        );
    }
    private int ticks = 0;

    @Override
    public void onEnable(boolean inWorld) {
        super.onEnable(inWorld);
        ticks = 0;

    }

    @Override
    public void onDisable(boolean inWorld) {
        if (inWorld) {
            ticks = 0;
        }


        super.onDisable(inWorld);
    }

    @Subscribe
    public void onTick(EventTick event) {

        if (getSetting(0).asMode().getMode() == 0) {

            mc.options.jumpKey.setPressed(false);

            if (mc.player.isOnGround() && MiscUtils.isMoving()) {
                ticks = 0;
                mc.player.jump();

                MiscUtils.strafe();
                if (MiscUtils.getSpeed() < 0.5f) {
                    MiscUtils.strafe(0.484f);
                }
            }

            if (!mc.player.isOnGround()) {
                ticks++;
            }

            if (ticks == 4) {
                    mc.player.setVelocity(mc.player.getVelocity().getX(), mc.player.getVelocity().getY() - 0.17, mc.player.getVelocity().getZ());
            }

            if (ticks == 1) {
                MiscUtils.strafe(0.33f);
            }
        }
    }

}
