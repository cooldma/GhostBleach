package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.event.events.EventPacket;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class GenericBuilder extends NightConfig {

	public GenericBuilder() {
		super("SecretClose", KEY_UNBOUND, NightConfigCategory.EXPLOITS, "Makes the server think you're still in a container after closing it.");
	}

	@Subscribe
	public void onSendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
			event.setCancelled(true);
		}
	}

}
