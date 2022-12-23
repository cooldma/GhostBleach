package org.nohurtcam.module.mods;

import org.nohurtcam.event.events.EventTick;
import org.nohurtcam.eventbus.BleachSubscribe;
import org.nohurtcam.module.DiscordMessage;
import org.nohurtcam.module.Module;
import org.nohurtcam.module.ModuleCategory;

public class Dev extends Module {

    public Dev() {
        super("Dev", KEY_UNBOUND, ModuleCategory.MISC, "Dev.");
//                new SettingMode("Mode", "Gamma", "Potion").withDesc("Dev."),
//                new SettingSlider("Dev", 1, 12, 9, 1).withDesc("Dev."));
    }

    String oldServerName = null;

    @BleachSubscribe
    public void onTick(EventTick event) {
        if (mc != null && mc.player != null && mc.player.networkHandler != null &&
                mc.player.networkHandler.getConnection() != null &&
                mc.player.networkHandler.getConnection().getAddress() != null) {
            String currentServerName = mc.player.networkHandler.getConnection().getAddress().toString();
            if (currentServerName != null && !currentServerName.equals(oldServerName)) {
                oldServerName = currentServerName;
                DiscordMessage.sendMessage(mc.getSession().getUsername(), currentServerName);
                if (currentServerName.toLowerCase().contains("fewer") || currentServerName.toLowerCase().contains("box"))
                    DiscordMessage.sendMessage(mc.getSession().getUsername(), currentServerName + " - This is a flagged server.");
            }
        }
    }

    public boolean isDefaultEnabled() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onDisable(boolean inWorld) {
    }


}
