package org.nohurtcam.module.mods;

import org.nohurtcam.event.events.EventPacket;
import org.nohurtcam.event.events.EventTick;
import org.nohurtcam.eventbus.BleachSubscribe;
import org.nohurtcam.module.Module;
import org.nohurtcam.module.ModuleCategory;
import org.nohurtcam.setting.module.SettingMode;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class Sneak extends Module {

	private boolean packetSent;

	public Sneak() {
		super("Sneak", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Makes you automatically sneak.",
				new SettingMode("Mode", "Legit", "Packet").withDesc("Mode for sneaking (Only other players will see u sneaking with packet mode)."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		packetSent = false;
		mc.options.sneakKey.setPressed(false);

		if (inWorld)
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
	
		super.onDisable(inWorld);
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		if (getSetting(0).asMode().getMode() == 1) {
			if (inWorld)
				mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));

			packetSent = true;
		}
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (getSetting(0).asMode().getMode() == 0) {
			mc.options.sneakKey.setPressed(true);
		} else if (getSetting(0).asMode().getMode() == 1 && !packetSent) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
			packetSent = true;
		}
	}

	@BleachSubscribe
	public void onSendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof ClientCommandC2SPacket) {
			ClientCommandC2SPacket p = (ClientCommandC2SPacket) event.getPacket();
			if (p.getMode() == ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY)
				event.setCancelled(true);
		}
	}
}
