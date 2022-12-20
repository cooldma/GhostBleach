package org.bleachhack.module.mods;

import net.minecraft.item.Item;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingItemList;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;

import static org.bleachhack.Extra.Test.Train;

public class ClientSpoofer extends Module {

    public ClientSpoofer() {
        super("ClientSpoofer", KEY_UNBOUND, ModuleCategory.MISC, "Spoofs your minecraft client.",
                new SettingMode("Mode", "Vanilla", "Feather Fabric", "Forge", "Lunar").withDesc("Relog required."));
    }

    private int client;

    @Override
    public void onEnable(boolean inWorld) {
        super.onEnable(inWorld);
//        Train();
        BleachLogger.info(mc.getName());
    }
}
