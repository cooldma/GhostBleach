package org.purpurclient.module.mods;

import org.purpurclient.event.events.EventPacket;
import org.purpurclient.eventbus.BleachSubscribe;
import org.purpurclient.module.Module;
import org.purpurclient.module.ModuleCategory;

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class SecretClose extends Module {

	public SecretClose() {
		super("SecretClose", KEY_UNBOUND, ModuleCategory.EXPLOITS, "Makes the server think you're still in a container after closing it.");
	}

	@BleachSubscribe
	public void onSendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
			event.setCancelled(true);
		}
	}

}
