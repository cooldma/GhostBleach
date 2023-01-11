package org.purpurmc.purpur.client.network.mods;

import org.purpurmc.purpur.client.network.Module;
import org.purpurmc.purpur.client.network.ModuleCategory;

public class AntiChunkBan extends Module {

	public AntiChunkBan() {
		super("AntiChunkBan", KEY_UNBOUND, ModuleCategory.EXPLOITS, "Bypasses chunks bans (and bookbans).");
	}

}
