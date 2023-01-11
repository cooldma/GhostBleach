package org.purpurmc.purpur.client.network.mods;

import org.purpurmc.purpur.client.event.events.EventTick;
import org.purpurmc.purpur.client.eventbus.BleachSubscribe;
import org.purpurmc.purpur.client.network.Module;
import org.purpurmc.purpur.client.network.ModuleCategory;

public class BoxpvpvFloat extends Module {

    public BoxpvpvFloat() {
        super("BoxpvpvFloat", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Float on boxpvpv.");
    }

    @Override
    public void onDisable(boolean inWorld) {
        super.onDisable(inWorld);
    }

    @Override
    public void onEnable(boolean inWorld) {

        super.onEnable(inWorld);
    }

    @BleachSubscribe
    public void onTick(EventTick e) {
        mc.options.forwardKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.player.setVelocity(0, 0, 0);
    }
}
