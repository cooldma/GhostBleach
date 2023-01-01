package org.purpurclient.module.mods;

import org.purpurclient.event.events.EventTick;
import org.purpurclient.eventbus.BleachSubscribe;
import org.purpurclient.module.Module;
import org.purpurclient.module.ModuleCategory;
import org.purpurclient.setting.module.SettingSlider;
import org.purpurclient.setting.module.SettingToggle;

public class Test extends Module {

    public Test() {
        super("Test", KEY_UNBOUND, ModuleCategory.MISC, "Test.",
                new SettingToggle("X", false).withDesc("Toggles X."),
                new SettingToggle("Y", false).withDesc("Toggles Y."),
                new SettingToggle("Z", false).withDesc("Toggles Z."),
                new SettingSlider("X", -1, 1, 0, 2).withDesc("X."),
                new SettingSlider("Y", -1, 1, 0, 2).withDesc("Y."),
                new SettingSlider("Z", -1, 1, 0, 2).withDesc("Z."));
    }

    @Override
    public void onDisable(boolean inWorld) {
//        mc.options.attackKey.setPressed(true);

        super.onDisable(inWorld);
    }

    @Override
    public void onEnable(boolean inWorld) {

        super.onEnable(inWorld);
    }

    @BleachSubscribe
    public void onTick(EventTick e) {

        if (getSetting(0).asToggle().getState() == true &&
        getSetting(1).asToggle().getState() == true &&
        getSetting(2).asToggle().getState() == true) {
            mc.player.setVelocity(
                    getSetting(3).asSlider().getValueFloat(),
                    getSetting(4).asSlider().getValueFloat(),
                    getSetting(5).asSlider().getValueFloat());
        }

        if (getSetting(0).asToggle().getState() == true &&
                getSetting(1).asToggle().getState() == true &&
                getSetting(2).asToggle().getState() == false) {
            mc.player.setVelocity(
                    getSetting(3).asSlider().getValueFloat(),
                    getSetting(4).asSlider().getValueFloat(),
                    0);
        }

        if (getSetting(0).asToggle().getState() == true &&
                getSetting(1).asToggle().getState() == false &&
                getSetting(2).asToggle().getState() == true) {
            mc.player.setVelocity(
                    getSetting(3).asSlider().getValueFloat(),
                    0,
                    getSetting(5).asSlider().getValueFloat());
        }

        if (getSetting(0).asToggle().getState() == false &&
                getSetting(1).asToggle().getState() == true &&
                getSetting(2).asToggle().getState() == true) {
            mc.player.setVelocity(
                    0,
                    getSetting(4).asSlider().getValueFloat(),
                    getSetting(5).asSlider().getValueFloat());
        }

        if (getSetting(0).asToggle().getState() == false &&
                getSetting(1).asToggle().getState() == false &&
                getSetting(2).asToggle().getState() == true) {
            mc.player.setVelocity(
                    0,
                    0,
                    getSetting(5).asSlider().getValueFloat());
        }

        if (getSetting(0).asToggle().getState() == true &&
                getSetting(1).asToggle().getState() == false &&
                getSetting(2).asToggle().getState() == false) {
            mc.player.setVelocity(
                    getSetting(3).asSlider().getValueFloat(),
                    0,
                    0);
        }

        if (getSetting(0).asToggle().getState() == false &&
                getSetting(1).asToggle().getState() == true &&
                getSetting(2).asToggle().getState() == false) {
            mc.player.setVelocity(
                    0,
                    getSetting(4).asSlider().getValueFloat(),
                    0);
        }
    }
}
