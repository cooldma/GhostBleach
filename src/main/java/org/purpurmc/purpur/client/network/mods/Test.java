package org.purpurmc.purpur.client.network.mods;

import net.minecraft.util.Hand;
import org.purpurmc.purpur.client.event.events.EventTick;
import org.purpurmc.purpur.client.eventbus.BleachSubscribe;
import org.purpurmc.purpur.client.network.Module;
import org.purpurmc.purpur.client.network.ModuleCategory;

public class Test extends Module {

    public Test() {
        super("BoxpvpvFloat", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Float on boxpvpv.");
    }

    @BleachSubscribe
    public void onTick(EventTick e) {
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
